/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.Unmarshaller;

import javax.json.JsonArray;

/**
 * Item for JsonArray.
 *
 * @author Roman Grigoriadi
 */
public class JsonArrayDeserializer extends AbstractJsonpDeserializer<JsonArray> {

    private JsonArray jsonArray;

    /**
     * Create instance.
     *
     * @param builder Builder to initialize from.
     */
    protected JsonArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        this.jsonArray = parser.getArray();
    }

    @Override
    public JsonArray getInstance(Unmarshaller unmarshaller) {
        return jsonArray;
    }
}
