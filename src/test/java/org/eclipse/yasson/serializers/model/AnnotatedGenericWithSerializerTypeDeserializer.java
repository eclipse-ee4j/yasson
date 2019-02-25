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

package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import static org.junit.Assert.assertEquals;

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
