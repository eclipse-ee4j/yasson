/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.GenericArrayType;
import java.util.List;

import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.ClassModel;

/**
 * Common array unmarshalling item implementation.
 *
 * @param <T> array type
 */
public abstract class AbstractArrayDeserializer<T> extends AbstractContainerDeserializer<T> implements EmbeddedItem {

    /**
     * Runtime type class of an array.
     */
    private final Class<?> componentClass;
    private final ClassModel componentClassModel;

    /**
     * Creates new class instance.
     *
     * @param builder deserializer builder
     */
    AbstractArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
        if (getRuntimeType() instanceof GenericArrayType) {
            componentClass = ReflectionUtils
                    .resolveRawType(this, ((GenericArrayType) getRuntimeType()).getGenericComponentType());
        } else {
            componentClass = ReflectionUtils.getRawType(getRuntimeType()).getComponentType();
        }
        if (!DefaultSerializers.getInstance().isKnownType(componentClass)) {
            componentClassModel = builder.getJsonbContext().getMappingContext().getOrCreateClassModel(componentClass);
        } else {
            componentClassModel = null;
        }
    }

    /**
     * Returns component class.
     *
     * @return component class
     */
    Class<?> getComponentClass() {
        return componentClass;
    }

    @Override
    public void appendResult(Object result) {
        appendCaptor(convertNullToOptionalEmpty(componentClass, result));
    }

    @SuppressWarnings("unchecked")
    private <X> void appendCaptor(X value) {
        ((List<X>) getItems()).add(value);
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        final JsonbDeserializer<?> deserializer = newUnmarshallerItemBuilder(context.getJsonbContext()).withType(componentClass)
                .withCustomization(componentClassModel == null ? null : componentClassModel.getClassCustomization()).build();
        appendResult(deserializer.deserialize(parser, context, componentClass));
    }

    /**
     * Returns list of deserialized items.
     *
     * @return list of items
     */
    protected abstract List<?> getItems();

    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        parser.moveTo(JsonParser.Event.START_ARRAY);
        return parser.getCurrentLevel();
    }
}