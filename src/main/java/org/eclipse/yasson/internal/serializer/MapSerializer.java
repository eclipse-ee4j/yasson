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

import java.util.Iterator;
import java.util.Map;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serialize {@link Map}.
 *
 * @param <K> {@link Map} key type to serialize
 * @param <V> {@link Map} value type to serialize
 */
public class MapSerializer<K,V> extends AbstractContainerSerializer<Map<K,V>> implements EmbeddedItem {

    /** Whether to serialize null values too. */
    private final boolean nullable;

    /** Instance that is responsible for serialization. */
    private ContainerSerializer<Map<K,V>> serializer;

    /**
     * Creates an instance of {@link Map} serialization.
     *
     * @param builder current instance of {@link SerializerBuilder}
     */
    protected MapSerializer(SerializerBuilder builder) {
        super(builder);
        nullable = builder.getJsonbContext().getConfigProperties().getConfigNullable();
        serializer = null;
    }

    /**
     * Check {@link Map} before serialization.
     * Decide whether provided {@link Map} can be serialized as {@code JsonObject} or as {@code JsonArray} of map entries.
     *
     * @param obj {@link Map} to be serialized
     */
    @Override
    public void beforeSerialize(Map<K,V> obj) {
        if (serializer == null) {
            // All keys can be serialized as String
            boolean allStrings = true;
            boolean first = true;
            Class<? extends Object> cls = null;
            // Cycle shall exit on first negative check
            for (Iterator<? extends Object> i = obj.keySet().iterator(); allStrings && i.hasNext(); ) {
                Object key = i.next();
                // 2nd and later pass: check whether all Map keys are of the same type
                if (cls != null) {
                    if (key == null) {
                        allStrings = false;
                    } else {
                        allStrings = cls.equals(key.getClass());
                    }
                // 1st pass: check whether key type is supported for Map to JSON Object serialization
                } else if (key instanceof String || key instanceof Number || key instanceof Enum ) {
                    cls = key.getClass();
                    first = false;
                // 1st pass: check whether key is null, which is also supported for Map to JSON Object serialization
                // Map shall contain only single mapping for null value and nothing else
                } else if (key == null && first) {
                    first = false;
                } else {
                    allStrings = false;
                }
            }
            // Set proper serializing algorithm
            if (allStrings) {
                serializer = new MapToObjectSerializer<>(this);
            } else {
                serializer = new MapToEntriesArraySerializer<>(this);
            }
        }
    }

    /**
     * Serialize content of provided {@link Map}.
     * Passing execution to delegate instance.
     *
     * @param obj {@link Map} to be serialized
     * @param generator JSON format generator
     * @param ctx JSON serialization context
     */
    @Override
    public void serializeContainer(Map<K,V> obj, JsonGenerator generator, SerializationContext ctx) {
        serializer.serializeContainer(obj, generator, ctx);
    }

    /**
     * Write start of {@link Map} serialization.
     * Passing execution to delegate instance.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(JsonGenerator generator) {
        serializer.writeStart(generator);
    }

    /**
     * Write start of {@link Map} serialization.
     * Passing execution to delegate instance.
     *
     * @param key JSON key name
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(String key, JsonGenerator generator) {
        serializer.writeStart(key, generator);
    }

    /**
     * Write end of {@link Map} serialization.
     * Passing execution to delegate instance.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeEnd(JsonGenerator generator) {
        serializer.writeEnd(generator);
    }

    /**
     * Return an information whether to serialize {@code null} values too.
     *
     * @return {@code null} values shall be serialized too when {@code true}
     */
    protected boolean isNullable() {
        return nullable;
    }

}
