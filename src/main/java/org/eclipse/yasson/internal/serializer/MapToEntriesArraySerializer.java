/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serialize {@link Map} with {@link Object} keys as an array of map entries JSON Objects:
 * <pre>
 * [
 *     {
 *         "key": JsonValue,
 *         "value": JsonValue
 *     }, ...
 * ]
 * </pre>
 *
 * @param <K> {@link Map} key type to serialize
 * @param <V> {@link Map} value type to serialize
 */
public class MapToEntriesArraySerializer<K, V> implements MapSerializer.Delegate<K, V> {

    /**
     * Default {@code JsonObject} property name for map entry key.
     */
    private static final String DEFAULT_KEY_ENTRY_NAME = "key";

    /**
     * Default {@code JsonObject} property name for map entry value.
     */
    private static final String DEFAULT_VALUE_ENTRY_NAME = "value";

    /**
     * Reference to {@link Map} serialization entry point. Contains serialization setup information.
     */
    private final MapSerializer<K, V> serializer;

    /**
     * {@code JsonObject} property name for map entry key.
     */
    private final String keyEntryName;

    /**
     * {@code JsonObject} property name for map entry value.
     */
    private final String valueEntryName;

    /**
     * Creates new map to entries array serializer.
     *
     * @param serializer map serializer
     */
    protected MapToEntriesArraySerializer(MapSerializer<K, V> serializer) {
        this.serializer = serializer;
        this.keyEntryName = DEFAULT_KEY_ENTRY_NAME;
        this.valueEntryName = DEFAULT_VALUE_ENTRY_NAME;
    }

    /**
     * Write start of {@link Map} serialization.
     * Opens {@code JsonArray} block.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(JsonGenerator generator) {
        generator.writeStartArray();
    }

    /**
     * Write start of {@link Map} serialization.
     * Opens {@code JsonArray} block.
     *
     * @param key       JSON key name
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(String key, JsonGenerator generator) {
        generator.writeStartArray();
    }

    /**
     * Serialize content of provided {@link Map}.
     * Content of provided {@link Map} is written into {@code JsonArray} of {@code JsonObject}s representing individual
     * map entries.
     *
     * @param obj       {@link Map} to be serialized
     * @param generator JSON format generator
     * @param ctx       JSON serialization context
     */
    @Override
    public void serializeContainer(Map<K, V> obj, JsonGenerator generator, SerializationContext ctx) {
        obj.forEach((key, value) -> {
            generator.writeStartObject();
            generator.writeKey(keyEntryName);
            serializer.serializeItem(key, generator, ctx);
            generator.writeKey(valueEntryName);
            serializer.serializeItem(value, generator, ctx);
            generator.writeEnd();
        });
    }

}
