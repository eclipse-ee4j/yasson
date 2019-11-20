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

import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializer for {@link JsonObject} type.
 */
public class JsonObjectSerializer extends AbstractJsonpSerializer<JsonObject> {

    /**
     * Creates new instance of json object serializer.
     *
     * @param builder serializer builder
     */
    protected JsonObjectSerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void serializeInternal(JsonObject obj, JsonGenerator generator, SerializationContext ctx) {
        for (Map.Entry<String, JsonValue> entry : obj.entrySet()) {
            generator.write(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }
}
