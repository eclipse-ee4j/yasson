/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Assertions.shouldFail;
import static org.eclipse.yasson.Jsonbs.testWithJsonbBuilderCreate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        /**
         * Force to lower case to check toString is not used during serialization of maps
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * MapObject to test different parametrized maps.
     * @param <K> The map key
     * @param <V>The map value
     */
    public static class MapObject<K, V> {

        private Map<K, V> values;

        public MapObject() {
            this.values = new HashMap<>();
        }

        public Map<K, V> getValues() {
            return values;
        }

        public void setValues(Map<K, V> values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MapObject) {
                MapObject<?,?> to = (MapObject<?,?>) o;
                return values.equals(to.values);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    public static class MapObjectIntegerString extends MapObject<Integer, String> {}

	public static class MapObjectBigIntegerString extends MapObject<BigInteger, String> {}

	public static class MapObjectEnumString extends MapObject<TestEnum, String> {}

	public static class MapObjectStringString extends MapObject<String, String> {}

	public static class MapObjectBooleanString extends MapObject<Boolean, String> {}

	/**
     * Test serialization of Map with Number keys and String values.
     */
    @Test
    public void testSerializeEnumMapToObject() {
        Map<TestEnum, Object> map = new EnumMap<>(TestEnum.class);
        testWithJsonbBuilderCreate(new JsonbConfig().withFormatting(true), jsonb -> {
            map.put(TestEnum.ONE, "value1");
            map.put(TestEnum.TWO, "value2");
            String json = jsonb.toJson(map);
            for (TestEnum e : TestEnum.values()) {
                assertTrue(json.contains(e.name()), "Enumeration not well serialized");
            }
        });
    }

    /**
     * Test for Integer/String map.
     */
    @Test
    public void testIntegerString() {
        testWithJsonbBuilderCreate(new JsonbConfig(), jsonb -> {

            MapObjectIntegerString mapObject = new MapObjectIntegerString();
            mapObject.getValues().put(12, "twelve");
            mapObject.getValues().put(48, "forty eight");
            mapObject.getValues().put(256, "two hundred fifty-six");

            String json = jsonb.toJson(mapObject);
            MapObjectIntegerString resObject = jsonb.fromJson(json, MapObjectIntegerString.class);
            assertEquals(mapObject, resObject);
        });
    }

    /**
     * Test for BigInteger/String map.
     */
    @Test
    public void testBigIntegerString() {
        testWithJsonbBuilderCreate(new JsonbConfig(), jsonb -> {

            MapObjectBigIntegerString mapObject = new MapObjectBigIntegerString();
            mapObject.getValues().put(new BigInteger("12"), "twelve");
            mapObject.getValues().put(new BigInteger("48"), "forty eight");
            mapObject.getValues().put(new BigInteger("256"), "two hundred fifty-six");

            String json = jsonb.toJson(mapObject);
            MapObjectBigIntegerString resObject = jsonb.fromJson(json, MapObjectBigIntegerString.class);
            assertEquals(mapObject, resObject);
        });
    }

    /**
     * Test for Enum/String map.
     */
    @Test
    public void testEnumString() {
        testWithJsonbBuilderCreate(jsonb -> {

            MapObjectEnumString mapObject = new MapObjectEnumString();
            mapObject.getValues().put(TestEnum.ONE, "one");
            mapObject.getValues().put(TestEnum.TWO, "two");

            String json = jsonb.toJson(mapObject);
            MapObjectEnumString resObject = jsonb.fromJson(json, MapObjectEnumString.class);
            assertEquals(mapObject, resObject);
        });
    }

    /**
     * Test for String/String map.
     */
    @Test
    public void testStringString() {
        testWithJsonbBuilderCreate(new JsonbConfig().setProperty("lala", "lala"), jsonb -> {

            MapObjectStringString mapObject = new MapObjectStringString();
            mapObject.getValues().put("one", "one");
            mapObject.getValues().put("two", "two");

            String json = jsonb.toJson(mapObject);
            MapObjectStringString resObject = jsonb.fromJson(json, MapObjectStringString.class);
            assertEquals(mapObject, resObject);
        });
    }

    /**
     * Test for a non parametrized map that should use Strings as keys.
     */
    @Test
    public void testNotParametrizedMap() {
        testWithJsonbBuilderCreate(new JsonbConfig(), jsonb -> {

            Map<Integer, String> mapObject = new HashMap<>();
            mapObject.put(12, "twelve");
            mapObject.put(48, "forty eight");
            mapObject.put(256, "two hundred fifty-six");

            String json = jsonb.toJson(mapObject);
            Map<?, ?> resObject = jsonb.fromJson(json, Map.class);
            assertEquals(3, resObject.size());
            assertTrue(resObject.keySet().iterator().next() instanceof String);
        });
    }

    /**
     * Test for Boolean/String map. This map is not even generated by the
     * MapToObjectSerializer as a boolean is not managed by that serializer.
     * But the json string should be deserialized in the same way.
     */
    @Test
    public void testBooleanStringMapToObjectSerializer() {
        testWithJsonbBuilderCreate(jsonb -> {

            String json = "{\"values\":{\"true\":\"TRUE\",\"false\":\"FALSE\"}}";
            MapObjectBooleanString resObject = jsonb.fromJson(json, MapObjectBooleanString.class);
            assertEquals(2, resObject.getValues().size());
            assertEquals("TRUE", resObject.getValues().get(true));
            assertEquals("FALSE", resObject.getValues().get(false));
        });
    }

    /**
     * Test for Integer/String map but giving an incorrect integer key.
     * JsonbException is expected.
     */
    @Test
    public void testIncorrectTypeMapToObjectSerializer() {
        testWithJsonbBuilderCreate(jsonb -> {

            String json = "{\"values\":{\"1\":\"OK\",\"error\":\"KO\"}}";
            shouldFail(() -> jsonb.fromJson(json, MapObjectIntegerString.class));
        });
    }
}
