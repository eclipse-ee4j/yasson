/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Tomas Kraus
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import java.util.Map;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serialize {@link Map} with {@link String} keys as JSON Object:
 * <pre>
 * {
 *     "key1": JsonValue,
 *     "key2": JsonValue,
 *     ...
 * }
 * </pre>
 * @param <K> {@link Map} key type to serialize
 * @param <V> {@link Map} value type to serialize
 */
public class MapToObjectSerializer<K,V> implements ContainerSerializer<Map<K,V>> {

    /** Reference to {@link Map} serialization entry point. Contains serialization setup information. */
    private final MapSerializer<K,V> serializer;

    /**
     * Creates an instance of {@link Map} serialization to {@code JsonObject}.
     *
     * @param serializer reference to {@link Map} serialization entry point
     */
    protected MapToObjectSerializer(MapSerializer<K,V> serializer) {
        this.serializer = serializer;
    }

    /**
     * Write start of {@link Map} serialization.
     * Opens {@code JsonObject} block.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

   /**
     * Write start of {@link Map} serialization.
     * Opens {@code JsonObject} block.
     *
     * @param key JSON key name
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }

    /**
     * Serialize content of provided {@link Map}.
     * Content of provided {@link Map} is written into {@code JsonObject} block. Map keys are written
     * as {@code JsonObject} property name {@link String}s.
     *
     * @param obj {@link Map} to be serialized
     * @param generator JSON format generator
     * @param ctx JSON serialization context
     */
    @Override
    public void serializeContainer(Map<K,V> obj, JsonGenerator generator, SerializationContext ctx) {
        for (Map.Entry<?,?> entry : obj.entrySet()) {
            final String keyString = String.valueOf(entry.getKey());
            final Object value = entry.getValue();
            if (value == null) {
                if (serializer.isNullable()) {
                    generator.writeNull(keyString);
                }
                continue;
            }
            generator.writeKey(keyString);
            serializer.serializeItem(value, generator, ctx);
        }
    }

}
