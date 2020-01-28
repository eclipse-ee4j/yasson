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

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author Roman Grigoriadi
 */
public class CrateDeserializerWithConversion implements JsonbDeserializer<Crate> {

    @Override
    public Crate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Crate result = new Crate();
        while (parser.hasNext()) {
            final JsonParser.Event next = parser.next();
            if (next.equals(JsonParser.Event.KEY_NAME) && parser.getString().equals("date-converted")) {
                parser.next();
                result.date = ctx.deserialize(Date.class, parser);
                break;
            }
        }
        return result;
    }
}
