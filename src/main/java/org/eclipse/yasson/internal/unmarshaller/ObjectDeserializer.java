/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson.internal.unmarshaller;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.CreatorParam;
import org.eclipse.yasson.model.JsonbCreator;
import org.eclipse.yasson.model.PropertyModel;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    private Map<String, Object> values = new HashMap<>();

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
     * @return instance
     * @param unmarshaller
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

        for(Iterator<ClassModel> classModelIterator = unmarshaller.getMappingContext().classModelIterator(rawType); classModelIterator.hasNext();) {
            classModelIterator.next().getProperties().entrySet().stream()
                    .filter((entry)->creator == null || !creator.contains(entry.getKey()))
                    .forEach((entry)->{
                if (values.containsKey(entry.getKey())) {
                    final Object value = values.get(entry.getKey());
                    entry.getValue().setValue(instance, value);
                }
            });
        }
        return instance;
    }

    /**
     * Creates instance with custom jsonb creator (parameterized constructor or factory method)
     */
    private T createInstance(Class<T> rawType, JsonbCreator creator) {
        final T instance;
        final List<Object> paramValues = new ArrayList<>();
        for(CreatorParam param : creator.getParams().values()) {
            paramValues.add(values.get(param.getName()));
        }
        instance = creator.call(paramValues.toArray(), rawType);
        return instance;
    }

    /**
     * Set populated instance of current object to its unfinished wrapper values map.
     * @param result
     */
    @Override
    public void appendResult(Object result) {
        values.put(getModel().getPropertyName(), result);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {

        final JsonbCreator creator = getClassModel().getClassCustomization().getCreator();
        //first check jsonb creator param, since it can be different from property name
        if (creator != null) {
            final CreatorParam param = creator.getParams().get(parserContext.getLastKeyName());
            if (param != null) {
                final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext()).withType(param.getType()).build();
                Object result = deserializer.deserialize(parser, context, param.getType());
                values.put(param.getName(), result);
                return;
            }
        }

        //identify field model of currently processed class model
        PropertyModel newPropertyModel = getModel();
        if (newPropertyModel != null) {
            //create current item instance of identified object field
            final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext()).
                    withModel(newPropertyModel).build();

            Object result = deserializer.deserialize(parser, context, newPropertyModel.getPropertyType());
            values.put(newPropertyModel.getPropertyName(), result);
            return;
        }

        //ignore JSON property, which is missing in class model
        ((JsonbParser) parser).skipJsonStructure();

    }

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_OBJECT);
        return parser.getCurrentLevel();
    }

    @Override
    protected PropertyModel getModel() {
        final String lastKeyName = parserContext.getLastKeyName();
        if (lastPropertyModel != null && lastPropertyModel.getJsonKeyName().equals(lastKeyName)) {
            return lastPropertyModel.getPropertyModel();
        }
        lastPropertyModel = new LastPropertyModel(lastKeyName, getClassModel().findPropertyModelByJsonReadName(lastKeyName));
        return lastPropertyModel.getPropertyModel();
    }
}
