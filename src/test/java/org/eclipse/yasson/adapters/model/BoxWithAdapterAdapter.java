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

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.spi.JsonProvider;

/**
 * @author David Kral
 */
public class BoxWithAdapterAdapter implements JsonbAdapter<BoxWithAdapter, JsonObject> {
    @Override
    public JsonObject adaptToJson(BoxWithAdapter obj) {
        final JsonObjectBuilder builder = JsonProvider.provider().createObjectBuilder();
        builder.add("boxInteger", obj.getBoxIntegerField());
        builder.add("boxStr", obj.getBoxStrField());
        return builder.build();
    }

    @Override
    public BoxWithAdapter adaptFromJson(JsonObject jsonObj) {
        BoxWithAdapter box = new BoxWithAdapter();
        box.setBoxIntegerField(jsonObj.getInt("boxInteger"));
        box.setBoxStrField(jsonObj.getString("boxStr"));
        return box;
    }
}
