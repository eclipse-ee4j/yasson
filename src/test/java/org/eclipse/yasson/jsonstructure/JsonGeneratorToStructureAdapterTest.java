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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.YassonJsonb;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsonGeneratorToStructureAdapterTest {

    @Test
    public void testSimplePojo() {
        Pojo pojo = new Pojo();
        pojo.setBigDecimalProperty(BigDecimal.TEN);
        pojo.setLongProperty(10L);
        pojo.setStringProperty("String value");

        JsonObject result = (JsonObject) yassonJsonb.toJsonStructure(pojo);

        assertEquals("String value", getString(result.get("stringProperty")));
        JsonValue bigDecimalProperty = result.get("bigDecimalProperty");
        assertEquals(JsonValue.ValueType.NUMBER, bigDecimalProperty.getValueType());
        assertEquals(BigDecimal.TEN, ((JsonNumber) bigDecimalProperty).bigDecimalValue());
        JsonValue longProperty = result.get("longProperty");
        assertEquals(JsonValue.ValueType.NUMBER, longProperty.getValueType());
        assertEquals(10L, ((JsonNumber) longProperty).longValueExact());
    }

    @Test
    public void testInnerObjects() {
        Pojo pojo = new Pojo();
        pojo.setBigDecimalProperty(BigDecimal.TEN);
        pojo.setLongProperty(10L);
        pojo.setStringProperty("String value");
        pojo.setInner(new InnerPojo());
        pojo.getInner().setInnerFirst("First");
        pojo.getInner().setInnerSecond("Second");

        JsonObject result = (JsonObject) yassonJsonb.toJsonStructure(pojo, Pojo.class);
        assertEquals("String value", getString(result.get("stringProperty")));
        JsonValue bigDecimalProperty = result.get("bigDecimalProperty");
        assertTrue(bigDecimalProperty instanceof JsonNumber);
        assertEquals(BigDecimal.TEN, ((JsonNumber) bigDecimalProperty).bigDecimalValue());
        JsonValue longProperty = result.get("longProperty");
        assertTrue(longProperty instanceof JsonNumber);
        assertEquals(10L, ((JsonNumber) longProperty).longValueExact());

        JsonValue inner = result.get("inner");
        assertEquals(JsonValue.ValueType.OBJECT, inner.getValueType());
        assertEquals("First", ((JsonObject)inner).getString("innerFirst"));
        assertEquals("Second", ((JsonObject)inner).getString("innerSecond"));
    }

    @Test
    public void testSimpleJsonArray() {
        List<Object> objList = new ArrayList<>();
        objList.add("First");
        objList.add(10L);
        objList.add(BigDecimal.ONE);
        objList.add(Boolean.TRUE);
        objList.add(null);

        JsonArray result = (JsonArray) yassonJsonb.toJsonStructure(objList);
        assertEquals("First", result.getString(0));
        assertEquals(10L, result.getJsonNumber(1).longValueExact());
        assertEquals(BigDecimal.ONE, result.getJsonNumber(2).bigDecimalValue());
        assertEquals(Boolean.TRUE, result.getBoolean(3));
        assertEquals(JsonValue.ValueType.NULL, result.get(4).getValueType());
    }

    @Test
    public void testJsonArrayInJsonObject() {
        Pojo pojo = new Pojo();
        pojo.setStrings(new ArrayList<>());
        pojo.setBigDecimals(new ArrayList<>());
        pojo.setBooleans(new ArrayList<>());
        pojo.getStrings().add("First");
        pojo.getBigDecimals().add(BigDecimal.TEN);
        pojo.getBooleans().add(Boolean.TRUE);

        JsonObject result = (JsonObject) yassonJsonb.toJsonStructure(pojo);
        assertEquals(JsonValue.ValueType.ARRAY, result.get("strings").getValueType());
        assertEquals(JsonValue.ValueType.ARRAY, result.get("bigDecimals").getValueType());
        assertEquals(JsonValue.ValueType.ARRAY, result.get("booleans").getValueType());
        assertEquals("First", result.getJsonArray("strings").getString(0));
        assertEquals(BigDecimal.TEN, result.getJsonArray("bigDecimals").getJsonNumber(0).bigDecimalValue());
        assertEquals(Boolean.TRUE, result.getJsonArray("booleans").getBoolean(0));
    }

    @Test
    public void testNestedJsonArrays() {
        List<List<Object>> outer = new ArrayList<>();
        List<Object> inner = new ArrayList<>();
        inner.add("First");
        inner.add(10L);
        inner.add(BigDecimal.ONE);
        inner.add(Boolean.TRUE);
        inner.add(null);
        outer.add(inner);

        JsonArray result = (JsonArray) yassonJsonb.toJsonStructure(outer);
        assertEquals(JsonValue.ValueType.ARRAY, result.get(0).getValueType());
        JsonArray resultInner = result.getJsonArray(0);

        assertEquals("First", resultInner.getString(0));
        assertEquals(10L, resultInner.getJsonNumber(1).longValueExact());
        assertEquals(BigDecimal.ONE, resultInner.getJsonNumber(2).bigDecimalValue());
        assertEquals(Boolean.TRUE, resultInner.getBoolean(3));
        assertEquals(JsonValue.ValueType.NULL, resultInner.get(4).getValueType());
    }

    @Test
    public void testCustomJsonbSerializer() {
        Pojo pojo = new Pojo();
        pojo.setInner(new InnerPojo());
        pojo.getInner().setInnerFirst("First value");
        pojo.getInner().setInnerSecond("Second value");
        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create(new JsonbConfig().withSerializers(new InnerPojoSerializer()));
        JsonStructure result = jsonb.toJsonStructure(pojo);
        assertEquals(JsonValue.ValueType.OBJECT, result.getValueType());
        assertEquals(JsonValue.ValueType.OBJECT, ((JsonObject) result).get("inner").getValueType());
        JsonObject inner = (JsonObject) ((JsonObject) result).get("inner");
        assertEquals(JsonValue.ValueType.STRING, inner.get("first").getValueType());
        assertEquals("First value", ((JsonString) inner.get("first")).getString());
        assertEquals(JsonValue.ValueType.STRING, inner.get("second").getValueType());
        assertEquals("Second value", ((JsonString) inner.get("second")).getString());
    }

    private static String getString(JsonValue value) {
        if (value instanceof JsonString) {
            return ((JsonString) value).getString();
        }
        return value.toString();
    }
}
