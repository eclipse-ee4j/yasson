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

package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.types.TypeDeserializer;

/**
 * Extracts the value out of the {@link JsonParser} based upon the last obtained event.
 */
public class ValueExtractor implements ModelDeserializer<JsonParser> {

    private final TypeDeserializer delegate;

    /**
     * Create new instance.
     *
     * @param delegate delegate to accept extracted value
     */
    public ValueExtractor(TypeDeserializer delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        JsonParser.Event last = context.getLastValueEvent();
        switch (last) {
        case VALUE_TRUE:
            return delegate.deserialize(Boolean.TRUE, context);
        case VALUE_FALSE:
            return delegate.deserialize(Boolean.FALSE, context);
        case KEY_NAME:
        case VALUE_STRING:
            return delegate.deserialize(value.getString(), context);
        case VALUE_NUMBER:
            //We don't know for sure how to handle the number value, it can be int, long etc.
            //Value extraction has to be delegated to the TypeDeserializer
            return delegate.deserialize(value, context);
        default:
            throw new JsonbException("Could not extract data. Received event: " + last);
        }
    }
}
