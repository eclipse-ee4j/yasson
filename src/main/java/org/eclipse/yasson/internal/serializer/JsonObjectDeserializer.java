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

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;

/**
 * Item for JsonObject.
 *
 * @author Roman Grigoriadi
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
