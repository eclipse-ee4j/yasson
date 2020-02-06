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

import static org.junit.jupiter.api.Assertions.*;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AnnotatedGenericWithSerializerTypeDeserializer implements JsonbDeserializer<AnnotatedGenericWithSerializerType<?>> {

    @Override
    public AnnotatedGenericWithSerializerType<?> deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        AnnotatedGenericWithSerializerType<?> result = new AnnotatedGenericWithSerializerType<>();
        assertEquals(Event.KEY_NAME, parser.next());
        assertEquals("generic", parser.getString());
        result.value = ctx.deserialize(((ParameterizedType) rtType).getActualTypeArguments()[0], parser);
        return result;
    }
}
