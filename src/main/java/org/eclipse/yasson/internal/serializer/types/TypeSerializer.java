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

package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

/**
 * Base for all the type serializers.
 */
abstract class TypeSerializer<T> implements ModelSerializer {

    private final ModelSerializer serializer;

    TypeSerializer(TypeSerializerBuilder serializerBuilder) {
        if (serializerBuilder.isKey()) {
            serializer = new KeySerializer();
        } else {
            serializer = new ValueSerializer();
        }
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        serializer.serialize(value, generator, context);
    }

    abstract void serializeValue(T value, JsonGenerator generator, SerializationContextImpl context);

    void serializeKey(T key, JsonGenerator generator, SerializationContextImpl context) {
        generator.writeKey(String.valueOf(key));
    }

    private final class ValueSerializer implements ModelSerializer {

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            serializeValue((T) value, generator, context);
        }

    }

    private final class KeySerializer implements ModelSerializer {

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
            serializeKey((T) value, generator, context);
        }

    }
}
