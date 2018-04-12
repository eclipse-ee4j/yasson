/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.adapters.model;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
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
