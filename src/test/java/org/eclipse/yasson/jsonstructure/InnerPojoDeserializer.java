/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.jsonstructure;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

public class InnerPojoDeserializer implements JsonbDeserializer<InnerPojo> {
    @Override
    public InnerPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        InnerPojo innerPojo = new InnerPojo();
        //KEY first
        parser.next();
        //VALUE
        parser.next();
        innerPojo.setInnerFirst(parser.getString());
        //KEY second
        parser.next();
        //VALUE
        parser.next();
        innerPojo.setInnerSecond(parser.getString());
        //END_OBJECT
        parser.next();
        return innerPojo;
    }
}
