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

import java.util.ArrayList;
import java.util.Collection;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Array container deserializer.
 */
class ArrayDeserializer implements ModelDeserializer<JsonParser> {

    private final ModelDeserializer<JsonParser> delegate;

    ArrayDeserializer(ModelDeserializer<JsonParser> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
        Collection<Object> collection = new ArrayList<>();
        while (parser.hasNext()) {
            final JsonParser.Event next = parser.next();
            context.setLastValueEvent(next);
            switch (next) {
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NUMBER:
            case VALUE_NULL:
                DeserializationContextImpl newContext = new DeserializationContextImpl(context);
                collection.add(delegate.deserialize(parser, newContext));
                break;
            case END_ARRAY:
                return collection;
            default:
                throw new JsonbException("Unexpected state: " + next);
            }
        }
        return collection;
    }

}
