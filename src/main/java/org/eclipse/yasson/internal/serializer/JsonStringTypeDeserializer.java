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

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonString;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link JsonString} type.
 */
public class JsonStringTypeDeserializer extends AbstractValueTypeDeserializer<JsonString> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public JsonStringTypeDeserializer(Customization customization) {
        super(JsonString.class, customization);
    }

    @Override
    protected JsonString deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonBuilderFactory factory = unmarshaller.getJsonbContext().getJsonProvider().createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("json", jsonValue)
                .build();
        return jsonObject.getJsonString("json");
    }
}
