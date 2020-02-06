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

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonJsonb;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonStructureToParserAdapterTest {
    private final JsonProvider jsonProvider = JsonProvider.provider();

    @Test
    public void testBasicJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
    }

    @Test
    public void testNullValues() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.addNull("stringProperty");
        objectBuilder.addNull("bigDecimalProperty");
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
    }

    @Test
    public void testInnerJsonObjectWrappedWithProperties() {
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("innerFirst", "Inner value 1");
        innerBuilder.add("innerSecond", "Inner value 2");

        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("inner", innerBuilder.build());
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
        assertEquals("Inner value 1", result.getInner().getInnerFirst());
        assertEquals("Inner value 2", result.getInner().getInnerSecond());
    }

    @Test
    public void testInnerJsonObjectAtEndProperty() {
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("innerFirst", "Inner value 1");
        innerBuilder.add("innerSecond", "Inner value 2");

        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        objectBuilder.add("inner", innerBuilder.build());

        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
        assertEquals("Inner value 1", result.getInner().getInnerFirst());
        assertEquals("Inner value 2", result.getInner().getInnerSecond());

    }

    @Test
    public void testEmptyJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertNull(result.getLongProperty());
    }

    @Test
    public void testEmptyInnerJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        JsonObject innerObject = innerBuilder.build();

        objectBuilder.add("inner", innerObject);

        JsonObject jsonObject = objectBuilder.build();

        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertNull(result.getLongProperty());

        assertNotNull(result.getInner());
        assertNull(result.getInner().getInnerFirst());
        assertNull(result.getInner().getInnerSecond());
    }

    @Test
    public void testSimpleArray() {
        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(BigDecimal.TEN).add("String value").addNull();
        JsonArray jsonArray = arrayBuilder.build();
        List result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
        assertEquals(3, result.size());
        assertEquals(BigDecimal.TEN, result.get(0));
        assertEquals("String value", result.get(1));
        assertNull(result.get(2));
    }

    @Test
    public void testArraysInsideObject() {
        JsonArrayBuilder bigDecBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder strBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder blnBuilder = jsonProvider.createArrayBuilder();

        bigDecBuilder.add(BigDecimal.TEN);
        strBuilder.add("String value 1");
        blnBuilder.add(Boolean.TRUE);

        JsonObjectBuilder pojoBuilder = jsonProvider.createObjectBuilder();
        pojoBuilder.add("strings", strBuilder.build());
        pojoBuilder.add("bigDecimals", bigDecBuilder.build());
        pojoBuilder.add("booleans", blnBuilder.build());

        JsonObject jsonObject = pojoBuilder.build();
        Pojo pojo = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals(1, pojo.getBigDecimals().size());
        assertEquals(1, pojo.getStrings().size());
        assertEquals(1, pojo.getBooleans().size());
    }

    @Test
    public void testNestedArrays() {
        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder innerArrBuilder = jsonProvider.createArrayBuilder();
        innerArrBuilder.add("first").add("second");
        arrayBuilder.add(BigDecimal.TEN);
        arrayBuilder.add(innerArrBuilder.build());

        JsonArray jsonArray = arrayBuilder.build();

        ArrayList result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.TEN, result.get(0));
        assertTrue(result.get(1) instanceof List);
        List inner = (List) result.get(1);
        assertEquals(2, inner.size());
        assertEquals("first", inner.get(0));
        assertEquals("second", inner.get(1));
    }

    @Test
    public void testObjectsNestedInArrays() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);

        JsonArrayBuilder innerArrayBuilder = jsonProvider.createArrayBuilder();
        innerArrayBuilder.add("String value 1");
        objectBuilder.add("strings", innerArrayBuilder.build());

        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(objectBuilder.build());

        JsonArray rootArray = arrayBuilder.build();

        List<Object> result = yassonJsonb.fromJsonStructure(rootArray, new TestTypeToken<List<Pojo>>(){}.getType());
        assertTrue(result.get(0) instanceof Pojo);
        Pojo pojo = (Pojo) result.get(0);
        assertNotNull(pojo);
        assertEquals("value 1", pojo.getStringProperty());
        assertEquals(new BigDecimal("1.1"), pojo.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), pojo.getLongProperty());
        assertNotNull(pojo.getStrings());
        assertEquals(1, pojo.getStrings().size());
        assertEquals("String value 1", pojo.getStrings().get(0));
    }

    @Test
    public void testObjectsNestedInArraysRaw() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);

        JsonArrayBuilder innerArrayBuilder = jsonProvider.createArrayBuilder();
        innerArrayBuilder.add("String value 1");

        objectBuilder.add("strings", innerArrayBuilder.build());

        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(10L);
        arrayBuilder.add(objectBuilder.build());
        arrayBuilder.add("10");

        JsonArray rootArray = arrayBuilder.build();

        List<Object> result = yassonJsonb.fromJsonStructure(rootArray, new TestTypeToken<List<Object>>(){}.getType());
        assertEquals(new BigDecimal("10"), result.get(0));
        assertTrue(result.get(1) instanceof Map);
        Map pojo = (Map) result.get(1);
        assertNotNull(pojo);
        assertEquals("value 1", pojo.get("stringProperty"));
        assertEquals(new BigDecimal("1.1"), pojo.get("bigDecimalProperty"));
        assertEquals(new BigDecimal(10), pojo.get("longProperty"));
        assertTrue(pojo.get("strings") instanceof List);
        List strings = (List) pojo.get("strings");
        assertNotNull(strings);
        assertEquals(1, strings.size());
        assertEquals("String value 1", strings.get(0));
    }


    @Test
    public void testCustomJsonbDeserializer() {
        JsonObjectBuilder outerBuilder = jsonProvider.createObjectBuilder();
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("first", "String value 1");
        innerBuilder.add("second", "String value 2");
        outerBuilder.add("inner", innerBuilder.build());
        JsonObject object = outerBuilder.build();

        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create(new JsonbConfig().withDeserializers(new InnerPojoDeserializer()));
        Pojo result = jsonb.fromJsonStructure(object, Pojo.class);
        assertNotNull(result.getInner());
        assertEquals("String value 1", result.getInner().getInnerFirst());
        assertEquals("String value 2", result.getInner().getInnerSecond());
    }
}
