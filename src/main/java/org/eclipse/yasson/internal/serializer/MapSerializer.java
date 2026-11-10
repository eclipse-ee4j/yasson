/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.util.Map;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.types.TypeSerializers;

/**
 * Map container serializer.
 * <p>
 * Handles serialization of {@link Map} instances into JSON according to the JSON-B specification:
 * <ul>
 *     <li>Maps with supported string/primitive/enum/date keys are serialized as JSON objects ({@code { "key": value }}).</li>
 *     <li>Maps with complex object keys or keys with custom serializers are serialized as JSON arrays of key-value pair objects ({@code [ { "key": ..., "value": ... } ]}).</li>
 *     <li>Maps with {@link Object} key types are inspected dynamically at runtime to select the appropriate representation.</li>
 * </ul>
 */
abstract class MapSerializer implements ModelSerializer {

    private final ModelSerializer keySerializer;
    private final ModelSerializer valueSerializer;

    /**
     * Creates a new map serializer instance with the specified key and value serializers.
     *
     * @param keySerializer   serializer for map keys
     * @param valueSerializer serializer for map values
     */
    MapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /**
     * Gets the serializer used for map keys.
     *
     * @return key serializer
     */
    ModelSerializer getKeySerializer() {
        return keySerializer;
    }

    /**
     * Gets the serializer used for map values.
     *
     * @return value serializer
     */
    ModelSerializer getValueSerializer() {
        return valueSerializer;
    }

    /**
     * Factory method to create an appropriate {@link MapSerializer} based on key type and configuration.
     *
     * @param keyClass        declared class of map keys
     * @param keySerializer   serializer for keys
     * @param valueSerializer serializer for values
     * @param jsonbContext    JSON-B context
     * @return matching {@link MapSerializer} implementation
     */
    static MapSerializer create(Class<?> keyClass, ModelSerializer keySerializer, ModelSerializer valueSerializer, JsonbContext jsonbContext) {
        if (TypeSerializers.isSupportedMapKey(keyClass)) {
            //Issue #663: A custom JsonbSerializer is available for an already supported Map key. Serialization must
            //not use normal key:value map. No further checking needed. Wrapping object needs to be used.
            if (TypeSerializers.hasCustomJsonbSerializer(keyClass, jsonbContext)) {
                return new ObjectKeyMapSerializer(keySerializer, valueSerializer);
            } else {
                return new StringKeyMapSerializer(keySerializer, valueSerializer);
            }
        } else if (Object.class.equals(keyClass)) {
            return new DynamicMapSerializer(keySerializer, valueSerializer);
        }
        return new ObjectKeyMapSerializer(keySerializer, valueSerializer);
    }

    /**
     * Serializer for maps where the key type is declared as {@link Object}.
     * <p>
     * Dynamically inspects map keys during serialization to select either standard
     * JSON object mapping or an array of key-value pair objects per map instance.
     */
    private static final class DynamicMapSerializer extends MapSerializer {

        private final StringKeyMapSerializer stringMap;
        private final ObjectKeyMapSerializer objectMap;

        DynamicMapSerializer(ModelSerializer keySerializer,
                             ModelSerializer valueSerializer) {
            super(keySerializer, valueSerializer);
            this.stringMap = new StringKeyMapSerializer(keySerializer, valueSerializer);
            this.objectMap = new ObjectKeyMapSerializer(keySerializer, valueSerializer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            MapSerializer delegate = isSuitableForStringMap(map, context.getJsonbContext()) ? stringMap : objectMap;
            delegate.serialize(value, generator, context);
        }

        private boolean isSuitableForStringMap(Map<Object, Object> map, JsonbContext jsonbContext) {
            boolean forceMapArrayForNullKeys = jsonbContext.getConfigProperties().isForceMapArraySerializerForNullKeys();

            for (Object key : map.keySet()) {
                if (key == null) {
                    if (forceMapArrayForNullKeys) {
                        return false;
                    }
                    continue;
                }

                Class<?> keyClass = key.getClass();
                if (TypeSerializers.isSupportedMapKey(keyClass)) {
                    // Issue #663: A custom JsonbSerializer is available for an already supported Map key.
                    // Serialization must not use normal key:value map. Wrapping object needs to be used.
                    if (TypeSerializers.hasCustomJsonbSerializer(keyClass, jsonbContext)) {
                        return false;
                    }
                    continue;
                }

                // Map is not suitable for normal key:value map. Wrapping object needs to be used.
                return false;
            }
            return true;
        }

    }

    /**
     * Serializer for maps with supported key types, formatting the output as a standard JSON object.
     * <p>
     * Output format: {@code { "key1": value1, "key2": value2, ... }}
     */
    private static final class StringKeyMapSerializer extends MapSerializer {

        StringKeyMapSerializer(ModelSerializer keySerializer,
                               ModelSerializer valueSerializer) {
            super(keySerializer, valueSerializer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            generator.writeStartObject();
            map.forEach((key, val) -> {
                getKeySerializer().serialize(key, generator, context);
                getValueSerializer().serialize(val, generator, context);
            });
            generator.writeEnd();
        }

    }

    /**
     * Serializer for maps with complex or unsupported key types, formatting the output as a JSON array of entry objects.
     * <p>
     * Output format: {@code [ { "key": key1, "value": value1 }, { "key": key2, "value": value2 }, ... ]}
     */
    private static final class ObjectKeyMapSerializer extends MapSerializer {

        ObjectKeyMapSerializer(ModelSerializer keySerializer,
                               ModelSerializer valueSerializer) {
            super(keySerializer, valueSerializer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            generator.writeStartArray();
            map.forEach((key, val) -> {
                generator.writeStartObject();
                generator.writeKey("key");
                if (key == null) {
                    generator.writeNull();
                } else {
                    getKeySerializer().serialize(key, generator, context);
                }
                generator.writeKey("value");
                getValueSerializer().serialize(val, generator, context);
                generator.writeEnd();
            });
            generator.writeEnd();
        }

    }

}
