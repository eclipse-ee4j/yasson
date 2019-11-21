/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;

import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link JsonValue} containing null, false, true, string and number.
 */
public class JsonValueDeserializer extends AbstractValueTypeDeserializer<JsonValue> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public JsonValueDeserializer(Customization customization) {
        super(JsonValue.class, customization);
    }

    @Override
    public JsonValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event next = ((JsonbRiParser) parser).getLastEvent();
        switch (next) {
        case VALUE_TRUE:
            return JsonValue.TRUE;
        case VALUE_FALSE:
            return JsonValue.FALSE;
        case VALUE_NULL:
            return JsonValue.NULL;
        case VALUE_STRING:
        case VALUE_NUMBER:
            return parser.getValue();
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Unknown JSON value: " + next));
        }
    }

    @Override
    protected JsonValue deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        throw new UnsupportedOperationException();
    }
}
