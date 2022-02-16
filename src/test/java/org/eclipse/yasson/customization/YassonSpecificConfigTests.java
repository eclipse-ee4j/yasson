/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization;

import java.util.Optional;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.YassonConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for Yasson specific config properties.
 */
public class YassonSpecificConfigTests {

    private static final String NULL_VALUE_STRING = "null value handled";
    private static final String NULL_VALUE_SERIALIZED = "\"" + NULL_VALUE_STRING + "\"";

    @Test
    public void nullRootSerializerTest() {
        Jsonb jsonb = JsonbBuilder.create(new YassonConfig().withNullRootSerializer(new RootNullSerializer()));
        assertEquals(NULL_VALUE_SERIALIZED, jsonb.toJson(null));
    }

    @Test
    public void emptyOptionalRootSerializerTest() {
        Jsonb jsonb = JsonbBuilder.create(new YassonConfig().withNullRootSerializer(new RootNullSerializer()));
        assertEquals(NULL_VALUE_SERIALIZED, jsonb.toJson(Optional.empty()));
    }

    @Test
    public void nullSerializerNotUsedTest() {
        Jsonb jsonb = JsonbBuilder.create(new YassonConfig().withNullRootSerializer(new RootNullSerializer()));
        assertEquals("[null]", jsonb.toJson(new String[] {null}));
    }

    private static final class RootNullSerializer implements JsonbSerializer<Object> {

        @Override
        public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(NULL_VALUE_STRING);
        }
    }

}
