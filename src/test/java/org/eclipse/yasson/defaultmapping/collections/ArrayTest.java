/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.collections;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class ArrayTest {

    @Test
    public void testStringArray() {
        String[] stringArray = new String[3];
        stringArray[0] = "first";
        stringArray[1] = "second";
        stringArray[2] = "third";

        String expected = "[\"first\",\"second\",\"third\"]";

        assertEquals(expected, nullableJsonb.toJson(stringArray));

        String[] result = nullableJsonb.fromJson(expected, stringArray.getClass());
        assertEquals("first", result[0]);
        assertEquals("second", result[1]);
        assertEquals("third", result[2]);
    }

    @Test
    public void testObjectArrayOfObjects() {
        Object[] objectArray = new Object[2];
        objectArray[0] = new KeyValue("first");
        objectArray[1] = new KeyValue("second");

        String expected = "[{\"field\":\"first\"},{\"field\":\"second\"}]";

        assertEquals(expected, nullableJsonb.toJson(objectArray));
        Object[] result = nullableJsonb.fromJson(expected, objectArray.getClass());
        assertEquals(HashMap.class, result[0].getClass());
        assertEquals(HashMap.class, result[1].getClass());
        assertEquals("first", ((Map) result[0]).get("field"));
        assertEquals("second", ((Map) result[1]).get("field"));
    }

    @Test
    public void testListOfArrays() {
        List<String[]> listOfArrays = new ArrayList<>();
        String[] stringArray = new String[2];
        stringArray[0] = "first";
        stringArray[1] = "second";
        listOfArrays.add(stringArray);
        listOfArrays.add(stringArray);

        String expected = "[[\"first\",\"second\"],[\"first\",\"second\"]]";
        assertEquals(expected, nullableJsonb.toJson(listOfArrays));
        List<String[]> result = nullableJsonb.fromJson(expected, new TestTypeToken<ArrayList<String[]>>(){}.getType());
        assertEquals("first", result.get(0)[0]);
        assertEquals("second", result.get(0)[1]);
        assertEquals("first", result.get(1)[0]);
        assertEquals("second", result.get(1)[1]);
    }

    @Test
    public void testMultidimensionalArrays() {
        String[][] multi = new String[2][2];
        multi[0][0] = "[0],[0]";
        multi[0][1] = "[0],[1]";
        multi[1][0] = "[1],[0]";
        multi[1][1] = "[1],[1]";
        String expected = "[[\"[0],[0]\",\"[0],[1]\"],[\"[1],[0]\",\"[1],[1]\"]]";
        assertEquals(expected, nullableJsonb.toJson(multi));
        String[][] result = nullableJsonb.fromJson(expected, multi.getClass());
        assertEquals("[0],[0]", result[0][0]);
        assertEquals("[0],[1]", result[0][1]);
        assertEquals("[1],[0]", result[1][0]);
        assertEquals("[1],[1]", result[1][1]);
    }

    @Test
    public void testDeserializeJsonArrayIntoObject() {
        String json = "[\"first\",\"second\",\"third\"]";
        Object result = nullableJsonb.fromJson(json, Object.class);
        assertTrue(result instanceof List);
        assertEquals("first", ((List)result).get(0));
        assertEquals("second", ((List)result).get(1));
        assertEquals("third", ((List)result).get(2));
    }

    @Test
    public void testDeserializeJsonObjectIntoListOfMaps() {
        String json = "[{\"first\":1,\"second\":10}]";
        Object result = nullableJsonb.fromJson(json, List.class);
        assertTrue(result instanceof List);
        assertEquals(BigDecimal.ONE, ((Map) ((List) result).get(0)).get("first"));
        assertEquals(BigDecimal.TEN, ((Map) ((List) result).get(0)).get("second"));
    }

    @Test
    public void testUnmarshallMapWithArrayValue() {
        Map<String, String[]> arrayValueMap = new HashMap<>();
        String[] strings = new String[2];
        strings[0] = "zero";
        strings[1] = "one";
        arrayValueMap.put("first", strings);
        String expected = "{\"first\":[\"zero\",\"one\"]}";
        assertEquals(expected, nullableJsonb.toJson(arrayValueMap));
        Map<String, String[]> result = nullableJsonb.fromJson(expected, new TestTypeToken<HashMap<String, String[]>>(){}.getType());
        assertEquals("zero", result.get("first")[0]);
        assertEquals("one", result.get("first")[1]);
    }

    @Test
    public void testArrayOfNulls() {
        String[] nulls = new String[2];
        String expected = "[null,null]";
        assertEquals(expected, nullableJsonb.toJson(nulls));
        String[] result = nullableJsonb.fromJson(expected, nulls.getClass());
        assertTrue(result.length == 2);
        assertNull(result[0]);
        assertNull(result[1]);

        Integer ints[] = new Integer[2];
        assertEquals(expected, nullableJsonb.toJson(ints));
    }

    @Test
    public void testByteArray() {
        byte[] byteArr = {-128, 127};
        assertEquals("[-128,127]", nullableJsonb.toJson(byteArr));
        assertArrayEquals(byteArr, nullableJsonb.fromJson("[-128, 127]", byte[].class));
    }

    @Test
    public void testCharArray() {
        char[] charArr = {'a', 'b', 'c'};
        assertEquals("[\"a\",\"b\",\"c\"]", nullableJsonb.toJson(charArr));
        assertArrayEquals(charArr, nullableJsonb.fromJson("[\"a\",\"b\",\"c\"]", char[].class));
    }

    @Test
    public void testShortArray() {
        short[] shortArr = {-128, 127};
        assertEquals("[-128,127]", nullableJsonb.toJson(shortArr));
        assertArrayEquals(shortArr, nullableJsonb.fromJson("[-128, 127]", short[].class));
    }

    @Test
    public void testIntArray() {
        int[] intArr = {-128, 127};
        assertEquals("[-128,127]", nullableJsonb.toJson(intArr));
        assertArrayEquals(intArr, nullableJsonb.fromJson("[-128, 127]", int[].class));
    }

    @Test
    public void testLongArray() {
        long[] longArr = {-128, 127};
        assertEquals("[-128,127]", nullableJsonb.toJson(longArr));
        assertArrayEquals(longArr, nullableJsonb.fromJson("[-128, 127]", long[].class));
    }

    @Test
    public void testFloatArray() {
        float[] floatArr = {-128, 127};
        assertEquals("[-128.0,127.0]", nullableJsonb.toJson(floatArr));
        assertArrayEquals(floatArr, nullableJsonb.fromJson("[-128.0, 127.0]", float[].class), 0f);
    }

    @Test
    public void testDoubleArray() {
        double[] doubleArr = {-128, 127};
        assertEquals("[-128.0,127.0]", nullableJsonb.toJson(doubleArr));
        assertArrayEquals(doubleArr, nullableJsonb.fromJson("[-128.0, 127.0]", double[].class), 0d);
    }

    public static class KeyValue {
        public String field;

        public KeyValue(String field) {
            this.field = field;
        }
    }
}
