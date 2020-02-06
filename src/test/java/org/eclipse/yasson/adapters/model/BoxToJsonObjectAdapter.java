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

package org.eclipse.yasson.adapters.model;

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.spi.JsonProvider;

/**
 * @author Roman Grigoriadi
 */
public class BoxToJsonObjectAdapter implements JsonbAdapter<Box, JsonObject> {
    @Override
    public JsonObject adaptToJson(Box obj) throws Exception {
        final JsonObjectBuilder builder = JsonProvider.provider().createObjectBuilder();
        builder.add("boxStrField", obj.getBoxStrField());
        builder.add("boxIntegerField", obj.getBoxIntegerField());
        return builder.build();
    }

    @Override
    public Box adaptFromJson(JsonObject jsonObj) throws Exception {
        Box box = new Box();
        box.setBoxStrField(jsonObj.getString("boxStrField"));
        box.setBoxIntegerField(jsonObj.getInt("boxIntegerField"));
        return box;
    }
}
