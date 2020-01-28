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

package org.eclipse.yasson.serializers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.Map;

/**
 * Test various use-cases with {@code Map} serializer which
 * stores Map as JSON object.
 */
public class MapToObjectSerializerTest {

    /**
     * Enum used as key in maps during tests
     */
    enum TestEnum {
        ONE, TWO;

        @Override
        /**
         * Force to lower case to check toString is not used during serialization of maps
         */
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Test serialization of Map with Number keys and String values.
     */
    @Test
    public void testSerializeEnumMapToObject() {
        Map<TestEnum, Object> map = new EnumMap<>(TestEnum.class);
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        map.put(TestEnum.ONE, "value1");
        map.put(TestEnum.TWO, "value2");
        String json = jsonb.toJson(map);
        for (TestEnum e : TestEnum.values()) {
            assertTrue(json.contains(e.name()), "Enumeration not well serialized");
        }
    }
}
