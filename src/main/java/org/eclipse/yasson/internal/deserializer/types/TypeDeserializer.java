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

package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;

import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

/**
 * Base for all type deserializers.
 */
public abstract class TypeDeserializer implements ModelDeserializer<String> {

    private final ModelDeserializer<Object> delegate;
    private final Class<?> clazz;

    TypeDeserializer(TypeDeserializerBuilder builder) {
        this.delegate = builder.getDelegate();
        this.clazz = builder.getClazz();
    }

    @Override
    public final Object deserialize(String value, DeserializationContextImpl context) {
        return delegate.deserialize(deserializeStringValue(value, context, clazz), context);
    }

    public final Object deserialize(boolean value, DeserializationContextImpl context) {
        return delegate.deserialize(deserializeBooleanValue(value, context, clazz), context);
    }

    public final Object deserialize(JsonParser value, DeserializationContextImpl context) {
        return delegate.deserialize(deserializeNumberValue(value, context, clazz), context);
    }

    abstract Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType);

    Object deserializeBooleanValue(boolean value, DeserializationContextImpl context, Type rType) {
        return deserializeStringValue(String.valueOf(value), context, rType);
    }

    Object deserializeNumberValue(JsonParser value, DeserializationContextImpl context, Type rType) {
        return deserializeStringValue(value.getString(), context, rType);
    }

    Class<?> getType() {
        return clazz;
    }

}
