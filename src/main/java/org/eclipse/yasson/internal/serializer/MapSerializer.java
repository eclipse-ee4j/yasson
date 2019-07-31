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
    private ContainerSerializer<Map<K,V>> delegate;

    /**
     * Creates an instance of {@link Map} serialization.
     *
     * @param builder current instance of {@link SerializerBuilder}
     */
    protected MapSerializer(SerializerBuilder builder) {
        super(builder);
        nullable = builder.getJsonbContext().getConfigProperties().getConfigNullable();
        delegate = null;
    }

    /**
     * Check {@link Map} before serialization.
     * Decide whether provided {@link Map} can be serialized as {@code JsonObject} or as {@code JsonArray} of map entries.
     *
     * @param obj {@link Map} to be serialized
     */
    @Override
    public void beforeSerialize(Map<K,V> obj) {
        if (delegate == null) {
            // All keys can be serialized as String
            boolean allStrings = true;
            boolean first = true;
            Class<? extends Object> cls = null;
            // Cycle shall exit on first negative check
            for (Iterator<? extends Object> i = obj.keySet().iterator(); allStrings && i.hasNext(); ) {
                Object key = i.next();
                // 1st pass: check types allowed for JsonObject serialization and store type
                if (first) {
                    first = false;
                    // Keep cls value as null when key is null too
                    if (key != null) {
                        if ((key instanceof String) || (key instanceof Number) || (key instanceof Enum) || key == null) {
                            cls = key.getClass();
                        } else {
                            allStrings = false;
                        }
                    }
                // 2nd and later pass: make sure that key types are the same in whole Map for JsonObject serialization
                } else {
                    if (cls == null) {
                        if (key != null) {
                            allStrings = false;
                        }
                    } else {
                        if (!cls.equals(key.getClass())) {
                            allStrings = false;
                        }
                    }
                }
            }
            // Set proper serializing algorithm
            if (allStrings) {
                delegate = new MapToObjectSerializer<>(this);
            } else {
                delegate = new MapToEntriesArraySerializer<>(this);
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
        delegate.serializeContainer(obj, generator, ctx);
    }

    /**
     * Write start of {@link Map} serialization.
     * Passing execution to delegate instance.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeStart(JsonGenerator generator) {
        delegate.writeStart(generator);
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
        delegate.writeStart(key, generator);
    }

    /**
     * Write end of {@link Map} serialization.
     * Passing execution to delegate instance.
     *
     * @param generator JSON format generator
     */
    @Override
    public void writeEnd(JsonGenerator generator) {
        delegate.writeEnd(generator);
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
