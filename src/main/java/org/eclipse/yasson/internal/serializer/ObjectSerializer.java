/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Serializes arbitrary object by reading its properties.
 *
 * @param <T> object type
 */
public class ObjectSerializer<T> extends AbstractContainerSerializer<T> {

    /**
     * Creates a new instance.
     *
     * @param builder Builder to initialize the instance.
     */
    public ObjectSerializer(SerializerBuilder builder) {
        super(builder);
    }

    /**
     * Creates a new instance.
     *
     * @param wrapper     wrapped item
     * @param runtimeType class type
     */
    public ObjectSerializer(Type runtimeType, CurrentItem<?> wrapper) {
        super(wrapper, runtimeType);
    }

    @Override
    protected void serializeInternal(T object, JsonGenerator generator, SerializationContext ctx) {
        Marshaller context = (Marshaller) ctx;
        try {
            if (context.addProcessedObject(object)) {
                final PropertyModel[] allProperties = context.getJsonbContext().getMappingContext().getOrCreateClassModel(object.getClass())
                        .getSortedProperties();
                for (PropertyModel model : allProperties) {
                    try {
                        marshallProperty(object, generator, context, model);
                    } catch (Exception e) {
                        throw new JsonbException(Messages.getMessage(MessageKeys.SERIALIZE_PROPERTY_ERROR, model.getWriteName(),
                                                                     object.getClass().getCanonicalName()), e);
                    }
                }
            } else {
                throw new JsonbException(Messages.getMessage(MessageKeys.RECURSIVE_REFERENCE, object.getClass()));
            }
        } finally {
            context.removeProcessedObject(object);
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }

    private void marshallProperty(T object, JsonGenerator generator, SerializationContext ctx, PropertyModel propertyModel) {
        Marshaller marshaller = (Marshaller) ctx;

        if (propertyModel.isReadable()) {
            final Object propertyValue = propertyModel.getValue(object);
            if (propertyValue == null || isEmptyOptional(propertyValue)) {
                if (propertyModel.getCustomization().isNillable()) {
                    generator.writeNull(propertyModel.getWriteName());
                }
                return;
            }

            generator.writeKey(propertyModel.getWriteName());

            final JsonbSerializer<?> propertyCachedSerializer = propertyModel.getPropertySerializer();
            if (propertyCachedSerializer != null) {
                serializerCaptor(propertyCachedSerializer, propertyValue, generator, ctx);
                return;
            }

            Optional<Type> runtimeTypeOptional = ReflectionUtils
                    .resolveOptionalType(this, propertyModel.getPropertySerializationType());
            Type genericType = runtimeTypeOptional.orElse(null);
            final JsonbSerializer<?> serializer = new SerializerBuilder(marshaller.getJsonbContext())
                    .withWrapper(this)
                    .withObjectClass(propertyValue.getClass())
                    .withCustomization(propertyModel.getCustomization())
                    .withType(genericType).build();
            serializerCaptor(serializer, propertyValue, generator, ctx);
        }
    }

    private static boolean isEmptyOptional(Object object) {
        if (object instanceof Optional) {
            return !((Optional) object).isPresent();
        } else if (object instanceof OptionalInt) {
            return !((OptionalInt) object).isPresent();
        } else if (object instanceof OptionalLong) {
            return !((OptionalLong) object).isPresent();
        } else if (object instanceof OptionalDouble) {
            return !((OptionalDouble) object).isPresent();
        }
        return false;
    }
}
