/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObject;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link JsonNumber} type.
 */
public class JsonNumberTypeDeserializer extends AbstractValueTypeDeserializer<JsonNumber> {

    private static final String NUMBER = "number";

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public JsonNumberTypeDeserializer(Customization customization) {
        super(JsonNumber.class, customization);
    }

    @Override
    protected JsonNumber deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonBuilderFactory factory = unmarshaller.getJsonbContext().getJsonProvider().createBuilderFactory(null);
        JsonObject jsonObject;
        try {
            Integer integer = Integer.parseInt(jsonValue);

            jsonObject = factory.createObjectBuilder()
                    .add(NUMBER, integer)
                    .build();
            return jsonObject.getJsonNumber(NUMBER);
        } catch (NumberFormatException exception) {
        }
        try {
            Long l = Long.parseLong(jsonValue);

            jsonObject = factory.createObjectBuilder()
                    .add(NUMBER, l)
                    .build();
            return jsonObject.getJsonNumber(NUMBER);
        } catch (NumberFormatException exception) {
        }

        jsonObject = factory.createObjectBuilder()
                .add(NUMBER, new BigDecimal(jsonValue))
                .build();
        return jsonObject.getJsonNumber(NUMBER);
    }
}
