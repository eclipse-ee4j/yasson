/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.serializer.JsonbSerializer;

import org.eclipse.yasson.internal.ComponentMatcher;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.ComponentBoundCustomization;

/**
 * Builder for serializers.
 */
public class SerializerBuilder extends AbstractSerializerBuilder<SerializerBuilder> {

    private Class<?> objectClass;

    /**
     * Creates a new builder.
     *
     * @param jsonbContext JSON-B context.
     */
    public SerializerBuilder(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    /**
     * Adds object class.
     *
     * @param objectClass object class
     * @return Builder.
     */
    public SerializerBuilder withObjectClass(Class<?> objectClass) {
        this.objectClass = objectClass;
        return this;
    }

    /**
     * Builds a {@link JsonbSerializer}.
     *
     * @return JsonbSerializer.
     */
    public JsonbSerializer<?> build() {
        withRuntimeType(resolveRuntimeType());

        if (getCustomization() instanceof ComponentBoundCustomization) {
            ComponentBoundCustomization customization = (ComponentBoundCustomization) this.getCustomization();
            //First check if user deserializer is registered for such type
            final ComponentMatcher componentMatcher = getJsonbContext().getComponentMatcher();
            Optional<SerializerBinding<?>> userSerializer = componentMatcher
                    .getSerializerBinding(getRuntimeType(), customization);
            if (userSerializer.isPresent()) {
                return new UserSerializerSerializer<>(getClassModel(), userSerializer.get().getJsonbSerializer());
            }

            //Second user components is registered.
            Optional<AdapterBinding> adapterInfoOptional = componentMatcher
                    .getSerializeAdapterBinding(getRuntimeType(), customization);
            if (adapterInfoOptional.isPresent()) {
                return new AdaptedObjectSerializer<>(getClassModel(), adapterInfoOptional.get());
            }
        }

        final Optional<AbstractValueTypeSerializer<?>> supportedTypeSerializer = getSupportedTypeSerializer(objectClass);
        if (supportedTypeSerializer.isPresent()) {
            return supportedTypeSerializer.get();
        }

        if (Collection.class.isAssignableFrom(objectClass)) {
            return new CollectionSerializer<>(this);
        } else if (Map.class.isAssignableFrom(objectClass)) {
            return new MapSerializer<>(this);
        } else if (isByteArray(objectClass)) {
            String strategy = getJsonbContext().getConfigProperties().getBinaryDataStrategy();
            switch (strategy) {
            case BinaryDataStrategy.BYTE:
                return new ByteArraySerializer(this);
            default:
                return new ByteArrayBase64Serializer(getCustomization());
            }
        } else if (objectClass.isArray() || getRuntimeType() instanceof GenericArrayType) {
            return createArrayItem(objectClass.getComponentType());

        } else if (JsonValue.class.isAssignableFrom(objectClass)) {
            if (JsonObject.class.isAssignableFrom(objectClass)) {
                return new JsonObjectSerializer(this);
            } else {
                return new JsonArraySerializer(this);
            }
        } else if (Optional.class.isAssignableFrom(objectClass)) {
            return new OptionalObjectSerializer<>(this);
        } else {
            getJsonbContext().getMappingContext().addSerializerProvider(objectClass, new ObjectSerializerProvider());
            return new ObjectSerializer<>(this);
        }

    }

    private boolean isByteArray(Class<?> rawType) {
        return rawType.isArray() && rawType.getComponentType() == Byte.TYPE;
    }

    /**
     * Instance is not created in case of array items, because, we don't know how long it should be
     * till parser ends parsing.
     */
    private JsonbSerializer<?> createArrayItem(Class<?> componentType) {
        if (componentType == byte.class) {
            return new ByteArraySerializer(this);
        } else if (componentType == short.class) {
            return new ShortArraySerializer(this);
        } else if (componentType == char.class) {
            return new CharArraySerializer(this);
        } else if (componentType == int.class) {
            return new IntArraySerializer(this);
        } else if (componentType == long.class) {
            return new LongArraySerializer(this);
        } else if (componentType == float.class) {
            return new FloatArraySerializer(this);
        } else if (componentType == double.class) {
            return new DoubleArraySerializer(this);
        } else {
            return new ObjectArraySerializer<>(this);
        }
    }

    private Optional<AbstractValueTypeSerializer<?>> getSupportedTypeSerializer(Class<?> rawType) {
        final Optional<? extends SerializerProviderWrapper> supportedTypeSerializerOptional = DefaultSerializers.getInstance()
                .findValueSerializerProvider(rawType);
        if (supportedTypeSerializerOptional.isPresent()) {
            return Optional
                    .of(supportedTypeSerializerOptional.get().getSerializerProvider().provideSerializer(getCustomization()));
        }
        return Optional.empty();
    }

    private Type resolveRuntimeType() {
        Type genericType = getGenericType();
        if (genericType != null && genericType != Object.class) {
            return genericType;
        }
        return objectClass;
    }
}
