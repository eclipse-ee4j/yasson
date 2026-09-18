/*
 * Copyright (c) 2026 Eclipse and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.yasson.adapters.model;

import java.lang.reflect.Type;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

public class CustomBooleanDeserializer implements JsonbDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext ctx, Type rtType) {

        // The provided parser is already positioned on the value to be deserialized, 
        // so we can just check the event type and return the corresponding boolean value.
        // Should Yasson have instead provided a parser that is positioned at the start of the key-value pair?
        // The specification nor API Javadoc seem to specifiy where the parser should be positioned.
        JsonParser.Event event = jsonParser.currentEvent();

        switch(event) {
            case VALUE_TRUE:
                return true;
            case VALUE_FALSE:
                return false;
            default:
                throw new IllegalStateException("Unexpected value: " + event);
        }        
    }
    
}
