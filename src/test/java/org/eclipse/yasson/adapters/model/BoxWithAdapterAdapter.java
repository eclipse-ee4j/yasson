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

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.spi.JsonProvider;

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
