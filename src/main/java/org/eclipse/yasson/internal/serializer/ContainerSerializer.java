/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Tomas Kraus
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Internal container serializing interface.
 *
 * @param <T> container type
 */
interface ContainerSerializer<T> {
    
    /**
     * Process container before serialization begins.
     * Does nothing by default.
     *
     * @param obj item to be serialized
     */
    default void beforeSerialize(T obj) {
    }

    /**
     * Write start of an object or an array without a key.
     *
     * @param generator JSON format generator
     */
    void writeStart(JsonGenerator generator);
   
    /**
     * Write start of an object or an array with a key.
     *
     * @param key JSON key name.
     * @param generator JSON format generator
     */
    void writeStart(String key, JsonGenerator generator);

    /**
     * Writes end of an object or an array.
     *
     * @param generator JSON format generator
     */
    default void writeEnd(JsonGenerator generator) {
        generator.writeEnd();
    }

    /**
     * Serialize content of provided container.
     *
     * @param obj container to be serialized
     * @param generator JSON format generator
     * @param ctx JSON serialization context
     */
    void serializeContainer(T obj, JsonGenerator generator, SerializationContext ctx);

}
