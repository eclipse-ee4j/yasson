/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * @author David Kral
 */
public class BoxWithDeserializerDeserializer implements JsonbDeserializer<BoxWithDeserializer> {

    @Override
    public BoxWithDeserializer deserialize(JsonParser jsonParser, DeserializationContext ctx, Type type) {
        BoxWithDeserializer box = new BoxWithDeserializer();
        while (jsonParser.hasNext()) {
            JsonParser.Event next = jsonParser.next();
            if (next.equals(JsonParser.Event.KEY_NAME) && jsonParser.getString().equals("boxInteger")) {
                box.setBoxIntegerField(ctx.deserialize(Integer.class, jsonParser));
            } else if (next.equals(JsonParser.Event.KEY_NAME) && jsonParser.getString().equals("boxStr")) {
                box.setBoxStrField(ctx.deserialize(String.class, jsonParser));
            }
        }
        return box;
    }
}
