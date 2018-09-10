/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.*;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.PropertyModel;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Item for handling all types of unknown objects by reflection, parsing their fields, according to json key name.
 *
 * @author Roman Grigoriadi
 */
class ObjectDeserializer<T> extends AbstractContainerDeserializer<T> {

    /**
     * Last property model cache to avoid lookup by jsonKey on every access.
     */
    private static class LastPropertyModel {

        private final String jsonKeyName;
        private final PropertyModel propertyModel;

        public LastPropertyModel(String jsonKeyName, PropertyModel propertyModel) {
            this.jsonKeyName = jsonKeyName;
            this.propertyModel = propertyModel;
        }

        public String getJsonKeyName() {
            return jsonKeyName;
        }

        public PropertyModel getPropertyModel() {
            return propertyModel;
        }
    }

    private static final Logger log = Logger.getLogger(ObjectDeserializer.class.getName());

    private Map<String, ValueWrapper> values = new LinkedHashMap<>();

    private T instance;

    private LastPropertyModel lastPropertyModel;

    /**
     * Creates instance of an item.
     * @param builder builder to build from
     */
    protected ObjectDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    /**
     * Due to support of custom (parametrized) constructors and factory methods, values are held in map,
     * which is transferred into instance values by calling getInstance.
     *
     * @param unmarshaller Current deserialization context.
     * @return An instance of deserializing item.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getInstance(Unmarshaller unmarshaller) {
        if (instance != null) {
            return instance;
        }
        final Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());
        final JsonbCreator creator = getClassModel().getClassCustomization().getCreator();
        instance = creator != null ? createInstance((Class<T>) rawType, creator)
                : ReflectionUtils.createNoArgConstructorInstance((Class<T>) rawType);

        //values must be set in order, in which they appears in JSON by spec
        values.forEach((key, wrapper) -> {
            //skip creator values
            if (wrapper.getCreatorModel() != null) {
                return;
            }
            final PropertyModel propertyModel = wrapper.getPropertyModel();
            propertyModel.setValue(instance, wrapper.getValue());
        });

        return instance;
    }


    /**
     * Creates instance with custom jsonb creator (parameterized constructor or factory method)
     */
    private T createInstance(Class<T> rawType, JsonbCreator creator) {
        final T instance;
        final List<Object> paramValues = new ArrayList<>();
        for(CreatorModel param : creator.getParams()) {
            final ValueWrapper valueWrapper = values.get(param.getName());
            //required by spec
            if (valueWrapper == null){
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CREATOR_MISSING_PROPERTY, param.getName()));
            }
            paramValues.add(valueWrapper.getValue());
        }
        instance = creator.call(paramValues.toArray(), rawType);
        return instance;
    }

    /**
     * Set populated instance of current object to its unfinished wrapper values map.
     *
     * @param result An instance result of an item.
     */
    @Override
    public void appendResult(Object result) {
        final PropertyModel model = getModel();
        //missing property for null values
        if (model == null) {
            return;
        }
        values.put(model.getReadName(), new ValueWrapper(model, convertNullToOptionalEmpty(model.getPropertyType(), result)));
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {

        final JsonbCreator creator = getClassModel().getClassCustomization().getCreator();
        //first check jsonb creator param, since it can be different from property name
        if (creator != null) {
            final CreatorModel param = creator.findByName(parserContext.getLastKeyName());
            if (param != null) {
                final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext())
                        .withType(param.getType())
                        .withCustomization(param.getCustomization())
                        .build();
                Object result = deserializer.deserialize(parser, context, param.getType());
                values.put(param.getName(), new ValueWrapper(param, result));
                return;
            }
        }

        //identify field model of currently processed class model
        PropertyModel newPropertyModel = getModel();
        if (newPropertyModel != null) {
            //create current item instance of identified object field
            final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext())
                    .withCustomization(newPropertyModel.getCustomization())
                    .withType(newPropertyModel.getPropertyDeserializationType())
                    .build();

            Type resolvedType = ReflectionUtils.resolveType(this, newPropertyModel.getPropertyDeserializationType());
            Object result = deserializer.deserialize(parser, context, resolvedType);
            values.put(newPropertyModel.getPropertyName(), new ValueWrapper(newPropertyModel, result));
            return;
        }
        skipJsonProperty((JsonbParser) parser, context.getJsonbContext());
    }

    /**
     * Rise an exception, or ignore JSON property, which is missing in class model.
     */
    private void skipJsonProperty(JsonbParser parser, JsonbContext jsonbContext) {
        if (jsonbContext.getConfigProperties().getConfigFailOnUnknownProperties()) {
            throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_JSON_PROPERTY, parserContext.getLastKeyName(), getRuntimeType()));
        }
        parser.skipJsonStructure();
    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }

    protected PropertyModel getModel() {
        final String lastKeyName = parserContext.getLastKeyName();
        if (lastPropertyModel != null && lastPropertyModel.getJsonKeyName().equals(lastKeyName)) {
            return lastPropertyModel.getPropertyModel();
        }
        lastPropertyModel = new LastPropertyModel(lastKeyName, getClassModel().findPropertyModelByJsonReadName(lastKeyName));
        return lastPropertyModel.getPropertyModel();
    }

    private static class ValueWrapper {

        private final CreatorModel creatorModel;
        private final PropertyModel propertyModel;
        private final Object value;

        public ValueWrapper(CreatorModel creator, Object value) {
            this.creatorModel = creator;
            this.value = value;
            propertyModel = null;
        }

        public ValueWrapper(PropertyModel propertyModel, Object value) {
            this.propertyModel = propertyModel;
            this.value = value;
            creatorModel = null;
        }

        public CreatorModel getCreatorModel() {
            return creatorModel;
        }

        public PropertyModel getPropertyModel() {
            return propertyModel;
        }

        public Object getValue() {
            return value;
        }
    }
}
