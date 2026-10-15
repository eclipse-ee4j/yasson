/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.records;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Deserializer used to reproduce issue #606: {@code @JsonbTypeDeserializer} on a record component
 * must be recognised and invoked during deserialisation.
 * 
 * Converts a 32-character hex UUID string without hyphens into a {@link UUID}.
 */
public class SysIdDeserializer implements JsonbDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        String value = parser.getString();
        if (value.length() == 32) {
            value = value.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
        }
        return UUID.fromString(value);
    }
}
