/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.JsonObject;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class CrateJsonObjectDeserializer implements JsonbDeserializer<Crate> {

    /**
     * Deserialize an object from JSON.
     * Cursor of JsonParser is at START_OBJECT.
     *
     * @param parser Json parser
     * @param ctx    Deserialization context
     * @param rtType type of returned object
     * @return deserialized instance
     */
    @Override
    public Crate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject crateJsonObject = ctx.deserialize(JsonObject.class, parser);
        Crate crate = new Crate();
        crate.crateInner = new CrateInner();
        crate.crateInner.crateInnerStr = crateJsonObject.getJsonObject("crateInner").getString("crateInnerStr");
        crate.crateInner.crateInnerBigDec = crateJsonObject.getJsonObject("crateInner").getJsonNumber("crateInnerBigDec").bigDecimalValue();
        crate.crateStr = crateJsonObject.getString("crateStr");
        crate.crateBigDec = crateJsonObject.getJsonNumber("crateBigDec").bigDecimalValue();
        return crate;
    }
}
