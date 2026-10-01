/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer of the {@link JsonObject} type.
 */
class JsonObjectDeserializer implements ModelDeserializer<JsonParser> {

    private final ModelDeserializer<Object> delegate;

    JsonObjectDeserializer(TypeDeserializerBuilder builder) {
        this.delegate = builder.getDelegate();
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        JsonParser.Event last = context.getLastValueEvent();
        return delegate.deserialize(deserializeValue(last, value), context);
    }

    private JsonObject deserializeValue(JsonParser.Event last, JsonParser parser) {
        switch (last) {
        case VALUE_NULL:
            return null;
        case START_OBJECT:
            return parser.getObject();
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INVALID_DESERIALIZATION_JSON_TYPE, last));
        }
    }
}
