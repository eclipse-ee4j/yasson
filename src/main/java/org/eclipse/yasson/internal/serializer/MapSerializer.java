/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.types.TypeSerializers;

/**
 * Map container serializer.
 */
abstract class MapSerializer implements ModelSerializer {

    private final ModelSerializer keySerializer;
    private final ModelSerializer valueSerializer;

    MapSerializer(ModelSerializer keySerializer, ModelSerializer valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    ModelSerializer getKeySerializer() {
        return keySerializer;
    }

    ModelSerializer getValueSerializer() {
        return valueSerializer;
    }

    static MapSerializer create(Class<?> keyClass, ModelSerializer keySerializer, ModelSerializer valueSerializer) {
        if (TypeSerializers.isSupportedMapKey(keyClass)) {
            return new StringKeyMapSerializer(keySerializer, valueSerializer);
        } else if (Object.class.equals(keyClass)) {
            return new DynamicMapSerializer(keySerializer, valueSerializer);
        }
        return new ObjectKeyMapSerializer(keySerializer, valueSerializer);
    }

    private static final class DynamicMapSerializer extends MapSerializer {

        private final StringKeyMapSerializer stringMap;
        private final ObjectKeyMapSerializer objectMap;
        private MapSerializer serializer;

        DynamicMapSerializer(ModelSerializer keySerializer,
                                    ModelSerializer valueSerializer) {
            super(keySerializer, valueSerializer);
            stringMap = new StringKeyMapSerializer(keySerializer, valueSerializer);
            objectMap = new ObjectKeyMapSerializer(keySerializer, valueSerializer);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            if (serializer == null) {
                //We have to be sure that Map with Object as a key contains only supported values for key:value format map.
                Map<Object, Object> map = (Map<Object, Object>) value;
                boolean suitable = true;
                for (Object key : map.keySet()) {
                    if (key == null) {
                        if (context.getJsonbContext().getConfigProperties().isForceMapArraySerializerForNullKeys()) {
                            suitable = false;
                            break;
                        }
                        continue;
                    }
                    Class<?> keyClass = key.getClass();
                    if (TypeSerializers.isSupportedMapKey(keyClass)) {
                        continue;
                    }
                    //No other checks needed. Map is not suitable for normal key:value map. Wrapping object needs to be used.
                    suitable = false;
                    break;
                }
                serializer = suitable ? stringMap : objectMap;
            }
            serializer.serialize(value, generator, context);
        }

    }

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
