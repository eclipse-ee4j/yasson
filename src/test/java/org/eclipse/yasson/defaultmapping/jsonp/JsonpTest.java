/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.jsonp;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.defaultmapping.jsonp.model.JsonpPojo;

import javax.json.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.spi.JsonProvider;
import java.math.BigDecimal;

/**
 * Default mapping JSONP integration tests.
 *
 * @author Dmitry Kornilov
 */
public class JsonpTest {

    public static class JsonValueWrapper {
        public JsonValue jsonValue;

        public JsonValueWrapper(JsonValue jsonValue) {
            this.jsonValue = jsonValue;
        }

        public JsonValueWrapper() {
        }
    }

    @Test
    public void testInnerJsonObject() {
        
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject jsonObject = factory.createObjectBuilder()
                .add("name", "home")
                .add("city", "Prague")
                .build();

        final JsonObjectBuilder customerBuilder = factory.createObjectBuilder();
        customerBuilder.add("f1", "abc123");
        customerBuilder.add("f2", BigDecimal.TEN);
        customerBuilder.add("f3", 12);
        customerBuilder.add("city", jsonObject);

        final JsonObjectBuilder wrapperBuilder = factory.createObjectBuilder();
        wrapperBuilder.add("f1", "abc");
        wrapperBuilder.add("cust", customerBuilder);

        final JsonObject wrapper = wrapperBuilder.build();

        String expected = "{\"f1\":\"abc\",\"cust\":{\"f1\":\"abc123\",\"f2\":10,\"f3\":12,\"city\":{\"name\":\"home\",\"city\":\"Prague\"}}}";
        assertEquals(expected, defaultJsonb.toJson(wrapper));

        JsonObject result = defaultJsonb.fromJson(expected, JsonObject.class);
        assertEquals("home", result.getJsonObject("cust").getJsonObject("city").getString("name"));
        assertEquals("abc123", result.getJsonObject("cust").getString("f1"));
        assertEquals("abc123", result.getJsonObject("cust").getString("f1"));

    }

    @Test
    public void testMarshallJsonArray() {
        
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArray jsonArray = factory.createArrayBuilder()
                .add(1)
                .add(2)
                .build();

        assertEquals("{\"jsonValue\":[1,2]}", defaultJsonb.toJson(new JsonValueWrapper(jsonArray)));
    }

    @Test
    public void testMarshallJsonValue() {
        assertEquals("{\"jsonValue\":true}", defaultJsonb.toJson(new JsonValueWrapper(JsonValue.TRUE)));
    }

    @Test
    public void testMarshallJsonNumber() {
                assertEquals("{\"jsonValue\":10}", defaultJsonb.toJson(new JsonValueWrapper(new JsonpLong(10))));
    }

    @Test
    public void testMarshallJsonString() {
                assertEquals("{\"jsonValue\":\"hello\"}", defaultJsonb.toJson(new JsonValueWrapper(new JsonpString("hello"))));
    }

    @Test
    public void testJsonPojo() {
        JsonbConfig config = new JsonbConfig();
//        config.withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);


        JsonpPojo pojo = new JsonpPojo();
        final JsonObjectBuilder obj1builder = JsonProvider.provider().createObjectBuilder();
        obj1builder.add("strVal", "string value");
        obj1builder.add("numVal", 2.0d);
        obj1builder.addNull("nullVal");
        obj1builder.add("boolVal", Boolean.TRUE);

        final JsonObjectBuilder obj2Builder = JsonProvider.provider().createObjectBuilder();
        obj2Builder.add("innerStr", "string val");
        obj2Builder.add("innerNum", 11.1d);
        final JsonObject obj2 = obj2Builder.build();

        JsonArrayBuilder array1Builder = JsonProvider.provider().createArrayBuilder();
        array1Builder.addNull().add(false).add(11L).add(BigDecimal.TEN).add("array STR value").add(obj2);
        JsonArray jsonArray1 = array1Builder.build();

        obj1builder.add("innerJsonObject", obj2);
        obj1builder.add("innerArrayObject", jsonArray1);

        final JsonObject obj1 = obj1builder.build();
        pojo.jsonObject = obj1;

        JsonArrayBuilder arrayBuilder = JsonProvider.provider().createArrayBuilder();
        arrayBuilder.add(obj1).add(true).add(obj2).add(101.0d).add(BigDecimal.TEN);
        pojo.jsonArray = arrayBuilder.build();


        String expected = "{\"jsonArray\":[{\"strVal\":\"string value\",\"numVal\":2.0,\"nullVal\":null,\"boolVal\":true,\"innerJsonObject\":{\"innerStr\":\"string val\",\"innerNum\":11.1},\"innerArrayObject\":[null,false,11,10,\"array STR value\",{\"innerStr\":\"string val\",\"innerNum\":11.1}]},true,{\"innerStr\":\"string val\",\"innerNum\":11.1},101.0,10],\"jsonObject\":{\"strVal\":\"string value\",\"numVal\":2.0,\"nullVal\":null,\"boolVal\":true,\"innerJsonObject\":{\"innerStr\":\"string val\",\"innerNum\":11.1},\"innerArrayObject\":[null,false,11,10,\"array STR value\",{\"innerStr\":\"string val\",\"innerNum\":11.1}]}}";
        final String actual = jsonb.toJson(pojo);
        assertEquals(expected, actual);

        JsonpPojo result = jsonb.fromJson(expected, JsonpPojo.class);
        assertEquals(pojo.jsonObject, result.jsonObject);
        assertEquals(pojo.jsonArray, result.jsonArray);
    }

    @Test
    public void testJsonObject() {
        final JsonObjectBuilder objBuilder = JsonProvider.provider().createObjectBuilder();
        objBuilder.add("boolTrue", Boolean.TRUE).add("boolFalse", Boolean.FALSE)
                .addNull("null").add("str", "String");

        JsonArrayBuilder arrBuilder = JsonProvider.provider().createArrayBuilder();
        arrBuilder.add(11L).add(Boolean.FALSE).add(BigDecimal.TEN);
        objBuilder.add("array", arrBuilder);

        JsonObject object = objBuilder.build();

        String expected = "{\"boolTrue\":true,\"boolFalse\":false,\"null\":null,\"str\":\"String\",\"array\":[11,false,10]}";
        assertEquals(expected, defaultJsonb.toJson(object));

        JsonObject result = defaultJsonb.fromJson(expected, JsonObject.class);

        assertEquals(object, result);
    }

    @Test
    public void testJsonArray() {

        JsonArrayBuilder arrBuilder = JsonProvider.provider().createArrayBuilder();
        arrBuilder.add(11L).add(Boolean.FALSE).add(BigDecimal.TEN);

        JsonObjectBuilder objBuilder = JsonProvider.provider().createObjectBuilder();
        objBuilder.add("boolTrue", Boolean.TRUE).add("boolFalse", Boolean.FALSE)
                .addNull("null").add("str", "String");

        arrBuilder.add(objBuilder);

        JsonArray arr = arrBuilder.build();

        String expected = "[11,false,10,{\"boolTrue\":true,\"boolFalse\":false,\"null\":null,\"str\":\"String\"}]";
        assertEquals(expected, defaultJsonb.toJson(arr));

        JsonArray result = defaultJsonb.fromJson(expected, JsonArray.class);

        assertEquals(arr, result);
    }

    @Test
    public void testJsonObjectAsValue() {
        final JsonValueWrapper jsonValueWrapper = defaultJsonb.fromJson("{ \"jsonValue\" : { \"stringInstance\" : \"Test String\" } }", JsonValueWrapper.class);
        assertEquals("Test String", ((JsonObject) jsonValueWrapper.jsonValue).getString("stringInstance"));
    }

    @Test
    public void testJsonValueString() {
        JsonValueWrapper pojo = new JsonValueWrapper(Json.createValue("abc"));
        String json = defaultJsonb.toJson(pojo);
        assertEquals("{\"jsonValue\":\"abc\"}", json);

        JsonValueWrapper result = defaultJsonb.fromJson("{\"jsonValue\":\"def\"}", JsonValueWrapper.class);
        assertTrue(result.jsonValue instanceof  JsonString);
        assertEquals("def", ((JsonString)result.jsonValue).getString());
    }

    @Test
    public void testJsonValueAsObject() {
        JsonObject build = Json.createObjectBuilder().add("prop1", "val1")
                .add("prop2", "val2")
                .add("innerObj1", Json.createObjectBuilder().add("inner1", "innerVal1").build())
                .build();
        JsonValueWrapper pojo = new JsonValueWrapper(build);
        String expected = "{\"jsonValue\":{\"prop1\":\"val1\",\"prop2\":\"val2\",\"innerObj1\":{\"inner1\":\"innerVal1\"}}}";
        String json = defaultJsonb.toJson(pojo);
        assertEquals(expected, json);

        JsonValueWrapper result = defaultJsonb.fromJson(expected, JsonValueWrapper.class);
        assertTrue(result.jsonValue instanceof JsonObject);
        JsonObject jsonObject = (JsonObject) result.jsonValue;
        assertEquals("val1", jsonObject.getString("prop1"));
        assertEquals("innerVal1", jsonObject.getJsonObject("innerObj1").getString("inner1"));
    }

    @Test
    public void testJsonValueAsArray() {
        JsonArray jsonArray = Json.createArrayBuilder().add(1).add(2).add(3).add(Json.createObjectBuilder().add("a","b").build()).build();
        JsonValueWrapper pojo = new JsonValueWrapper(jsonArray);
        String expected = "{\"jsonValue\":[1,2,3,{\"a\":\"b\"}]}";
        String json = defaultJsonb.toJson(pojo);
        assertEquals(expected, json);

        JsonValueWrapper result = defaultJsonb.fromJson(expected, JsonValueWrapper.class);
        assertTrue(result.jsonValue instanceof JsonArray);
        JsonArray resultArray = (JsonArray) result.jsonValue;
        assertEquals(1, resultArray.getInt(0));
        assertEquals(2, resultArray.getInt(1));
        assertEquals(3, resultArray.getInt(2));
        assertEquals("b", resultArray.getJsonObject(3).getString("a"));

    }
}
