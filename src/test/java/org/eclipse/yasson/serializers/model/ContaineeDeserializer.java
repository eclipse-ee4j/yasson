/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers.model;

import java.lang.reflect.Type;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

public class ContaineeDeserializer implements JsonbDeserializer<Containee> {

    @Override
    public Containee deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        String key = null;
        String value = "";
        while (parser.hasNext()) {
            var event = parser.next();
            if (event == JsonParser.Event.KEY_NAME && parser.getString().equals("key")) {
                parser.next(); // move to VALUE
                key = parser.getString();
            } else if (event == JsonParser.Event.KEY_NAME && parser.getString().equals("value")) {
                parser.next(); // move to VALUE
                value = parser.getString();
            }
        }
        assert key != null;
        return new Containee(key, value);
    }

}
