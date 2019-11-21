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

import javax.json.JsonObject;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Item for JsonObject.
 */
public class JsonObjectDeserializer extends AbstractJsonpDeserializer<JsonObject> {

    private JsonObject jsonObject;

    @Override
    protected void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        this.jsonObject = parser.getObject();
    }

    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link DeserializerBuilder} used to build this instance
     */
    protected JsonObjectDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    public JsonObject getInstance(Unmarshaller unmarshaller) {
        return jsonObject;
    }
}
