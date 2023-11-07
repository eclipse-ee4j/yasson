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

package org.eclipse.yasson.jsonstructure;

import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonJsonb;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JsonStructureToParserAdapterTest {
    private static final EnumSet<JsonParser.Event> GET_STRING_EVENT_ENUM_SET =
            EnumSet.of(JsonParser.Event.KEY_NAME, JsonParser.Event.VALUE_STRING, JsonParser.Event.VALUE_NUMBER);

    private static final EnumSet<JsonParser.Event> NOT_GET_VALUE_EVENT_ENUM_SET = EnumSet.of(JsonParser.Event.END_OBJECT, JsonParser.Event.END_ARRAY);

    private static final Collector<Map.Entry<String, JsonValue>, ?, ArrayList<String>> MAP_TO_LIST_COLLECTOR = Collector.of(ArrayList::new,
            (list, entry) -> {
                list.add(entry.getKey());
                list.add(entry.getValue().toString());
            },
            (left, right) -> {
                left.addAll(right);
                return left;
            },
            Collector.Characteristics.IDENTITY_FINISH);

    private static final JsonProvider jsonProvider = JsonProvider.provider();

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
        List<?> result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
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

        ArrayList<?> result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.TEN, result.get(0));
        assertTrue(result.get(1) instanceof List);
        @SuppressWarnings("unchecked")
        List<String> inner = (List<String>) result.get(1);
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
        @SuppressWarnings("unchecked")
        Map<String, ?> pojo = (Map<String, ?>) result.get(1);
        assertNotNull(pojo);
        assertEquals("value 1", pojo.get("stringProperty"));
        assertEquals(new BigDecimal("1.1"), pojo.get("bigDecimalProperty"));
        assertEquals(new BigDecimal(10), pojo.get("longProperty"));
        assertTrue(pojo.get("strings") instanceof List);
        @SuppressWarnings("unchecked")
        List<String> strings = (List<String>) pojo.get("strings");
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

        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new InnerPojoDeserializer()), jsonb -> {
            Pojo result = ((YassonJsonb)jsonb).fromJsonStructure(object, Pojo.class);
            assertNotNull(result.getInner());
            assertEquals("String value 1", result.getInner().getInnerFirst());
            assertEquals("String value 2", result.getInner().getInnerSecond());
        });
    }

    @Nested
    public class DirectParserTests {
        @Test
        public void testNumbers() {
            JsonObject jsonObject = jsonProvider.createObjectBuilder()
                    .add("int", 1)
                    .add("long", 1L)
                    .add("double", 1d)
                    .add("BigInteger", BigInteger.TEN)
                    .add("BigDecimal", BigDecimal.TEN)
                    .build();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                parser.next();
                parser.getString();
                parser.next();
                assertTrue(parser.isIntegralNumber());
                assertEquals(1, parser.getInt());

                parser.next();
                parser.getString();
                parser.next();
                assertTrue(parser.isIntegralNumber());
                assertEquals(1L, parser.getLong());

                parser.next();
                parser.getString();
                parser.next();
                assertFalse(parser.isIntegralNumber());
                assertEquals(BigDecimal.valueOf(1d), parser.getBigDecimal());

                parser.next();
                parser.getString();
                parser.next();
                assertTrue(parser.isIntegralNumber());
                assertEquals(BigDecimal.TEN, parser.getBigDecimal());

                parser.next();
                parser.getString();
                parser.next();
                assertTrue(parser.isIntegralNumber());
                assertEquals(BigDecimal.TEN, parser.getBigDecimal());
            }
        }

        @Test
        public void testParser_getString(){
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                List<String> values = new ArrayList<>();
                parser.next();
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    if (GET_STRING_EVENT_ENUM_SET.contains(event)) {
                        String strValue = Objects.toString(parser.getString(), "null");
                        values.add(strValue);
                    }
                }

                assertThat(values,TestData.FAMILY_MATCHER_WITH_NO_QUOTATION);
            }
        }

        @Test
        public void testParser_getValue(){
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                List<String> values = new ArrayList<>();
                parser.next();
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    if (!NOT_GET_VALUE_EVENT_ENUM_SET.contains(event)) {
                        String strValue = Objects.toString(parser.getValue(), "null");
                        values.add(strValue);
                    }
                }

                assertThat(values, TestData.FAMILY_MATCHER_KEYS_WITH_QUOTATION);
            }
        }

        @Test
        public void testSkipArray() {
            JsonObject jsonObject = TestData.createObjectWithArrays();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                parser.next();
                parser.getString();
                parser.next();
                parser.skipArray();
                parser.next();
                String key = parser.getString();

                assertEquals("secondElement", key);
            }
        }

        @Test
        public void testSkipObject() {
            JsonObject jsonObject = TestData.createJsonObject();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                parser.next();
                parser.getString();
                parser.next();
                parser.skipObject();
                parser.next();
                String key = parser.getString();

                assertEquals("secondPerson", key);
            }
        }
    }

    @Nested
    public class StreamTests {
        @Test
        public void testGetValueStream_GetOneElement() {
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                JsonString name = (JsonString) parser.getValueStream()
                        .map(JsonValue::asJsonObject)
                        .map(JsonObject::values)
                        .findFirst()
                        .orElseThrow()
                        .stream()
                        .filter(e -> e.getValueType()  == JsonValue.ValueType.STRING)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Name not found"));

                assertEquals("John", name.getString());
            }
        }

        @Test
        public void testGetValueStream_GetList() {
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                List<String> values = parser.getValueStream().map(value -> Objects.toString(value, "null")).collect(Collectors.toList());

                assertThat(values, contains(TestData.JSON_FAMILY_STRING));
            }
        }

        @Test
        public void testGetArrayStream_GetOneElement() {
            JsonObject jsonObject = TestData.createObjectWithArrays();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                parser.next();
                String key = parser.getString();
                parser.next();
                JsonString element = (JsonString) parser.getArrayStream().filter(e -> e.getValueType()  == JsonValue.ValueType.STRING)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Element not found"));

                assertEquals("first", element.getString());
                assertEquals("firstElement", key);
            }
        }

        @Test
        public void testGetArrayStream_GetList() {
            JsonObject jsonObject = TestData.createObjectWithArrays();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                parser.next();
                String key = parser.getString();
                parser.next();
                List<String> values = parser.getArrayStream().map(value -> Objects.toString(value, "null")).collect(Collectors.toList());

                assertThat(values, TestData.ARRAY_STREAM_MATCHER);
                assertEquals("firstElement", key);
            }
        }

        @Test
        public void testGetObjectStream_GetOneElement() {
            JsonObject jsonObject = TestData.createJsonObject();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                String surname = parser.getObjectStream().filter(e -> e.getKey().equals("firstPerson"))
                        .map(Map.Entry::getValue)
                        .map(JsonValue::asJsonObject)
                        .map(obj -> obj.getString("surname"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Surname not found"));

                assertEquals("Smith", surname);
            }
        }

        @Test
        public void testGetObjectStream_GetList() {
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject, jsonProvider)) {
                parser.next();
                List<String> values = parser.getObjectStream().collect(MAP_TO_LIST_COLLECTOR);

                assertThat(values, TestData.FAMILY_MATCHER_KEYS_WITHOUT_QUOTATION);
            }
        }
    }

	@Nested
    public class JSONPStandardParserTests {
        @Test
        public void testStandardStringParser_getValueStream() {
            try (JsonParser parser = Json.createParser(new StringReader(TestData.JSON_FAMILY_STRING))) {
                List<String> values = parser.getValueStream().map(value -> Objects.toString(value, "null")).collect(Collectors.toList());

                assertThat(values, contains(TestData.JSON_FAMILY_STRING));
            }
        }

        @Test
        public void testStandardStringParser_getArrayStream() {
            try (JsonParser parser = Json.createParser(new StringReader("{\"firstElement\":[\"first\", \"second\"],\"secondElement\":[\"third\", \"fourth\"]}"))) {
                parser.next();
                parser.next();
                String key = parser.getString();
                parser.next();
                List<String> values = parser.getArrayStream().map(value -> Objects.toString(value, "null")).collect(Collectors.toList());

                assertThat(values, TestData.ARRAY_STREAM_MATCHER);
                assertEquals("firstElement", key);
            }
        }

        @Test
        public void testStandardStringParser_getObjectStream() {
            try (JsonParser parser = Json.createParser(new StringReader(TestData.JSON_FAMILY_STRING))) {

                parser.next();
                List<String> values = parser.getObjectStream().collect(MAP_TO_LIST_COLLECTOR);

                assertThat(values, TestData.FAMILY_MATCHER_KEYS_WITHOUT_QUOTATION);
            }
        }

        @Test
        public void testStandardStringParser_getValue() {
            try (JsonParser parser = Json.createParser(new StringReader(TestData.JSON_FAMILY_STRING))) {
                List<String> values = new ArrayList<>();
                parser.next();
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    if (!NOT_GET_VALUE_EVENT_ENUM_SET.contains(event)) {
                        String strValue = Objects.toString(parser.getValue(), "null");
                        values.add(strValue);
                    }
                }

                assertThat(values, TestData.FAMILY_MATCHER_KEYS_WITH_QUOTATION);
            }
        }

        @Test
        public void testStandardStringParser_getString() {
            try (JsonParser parser = Json.createParser(new StringReader(TestData.JSON_FAMILY_STRING))) {
                List<String> values = new ArrayList<>();
                parser.next();
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    if (GET_STRING_EVENT_ENUM_SET.contains(event)) {
                        String strValue = Objects.toString(parser.getString(), "null");
                        values.add(strValue);
                    }
                }

                assertThat(values, TestData.FAMILY_MATCHER_WITH_NO_QUOTATION);
            }
        }

        @Test
        public void testStandardStructureParser_getString() {
            JsonParserFactory factory = Json.createParserFactory(Map.of());
            JsonObject jsonObject = TestData.createFamilyPerson();

            try (JsonParser parser = factory.createParser(jsonObject)) {
                List<String> values = new ArrayList<>();
                parser.next();
                while (parser.hasNext()) {
                    JsonParser.Event event = parser.next();
                    if (GET_STRING_EVENT_ENUM_SET.contains(event)) {
                        String strValue = Objects.toString(parser.getString(), "null");
                        values.add(strValue);
                    }
                }

                assertThat(values, TestData.FAMILY_MATCHER_WITH_NO_QUOTATION);
            }
        }
    }

    private static class TestData {
        private static final String JSON_FAMILY_STRING = "{\"name\":\"John\",\"surname\":\"Smith\",\"age\":30,\"married\":true," +
                "\"wife\":{\"name\":\"Deborah\",\"surname\":\"Harris\"},\"children\":[\"Jack\",\"Mike\"]}";

        private static final Matcher<Iterable<? extends String>> FAMILY_MATCHER_KEYS_WITHOUT_QUOTATION =
                contains("name", "\"John\"", "surname", "\"Smith\"", "age", "30", "married", "true", "wife",
                        "{\"name\":\"Deborah\",\"surname\":\"Harris\"}", "children", "[\"Jack\",\"Mike\"]");

        private static final Matcher<Iterable<? extends String>> FAMILY_MATCHER_KEYS_WITH_QUOTATION =
                contains("\"name\"", "\"John\"", "\"surname\"", "\"Smith\"", "\"age\"", "30", "\"married\"", "true",
                        "\"wife\"", "{\"name\":\"Deborah\",\"surname\":\"Harris\"}", "\"children\"", "[\"Jack\",\"Mike\"]");

        private static final Matcher<Iterable<? extends String>> FAMILY_MATCHER_WITH_NO_QUOTATION =
                contains("name", "John", "surname", "Smith", "age", "30", "married",
                        "wife", "name", "Deborah", "surname", "Harris", "children", "Jack", "Mike");

        private static final Matcher<Iterable<? extends String>> ARRAY_STREAM_MATCHER = contains("\"first\"", "\"second\"");

        private static JsonObject createFamilyPerson() {
            return jsonProvider.createObjectBuilder()
                    .add("name", "John")
                    .add("surname", "Smith")
                    .add("age", 30)
                    .add("married", true)
                    .add("wife", createPerson("Deborah", "Harris"))
                    .add("children", createArray("Jack", "Mike"))
                    .build();
        }

		private static JsonObject createObjectWithArrays() {
			return jsonProvider.createObjectBuilder()
					.add("firstElement", createArray("first", "second"))
					.add("secondElement", createArray("third", "fourth"))
					.build();
		}

		private static JsonArrayBuilder createArray(String firstElement, String secondElement) {
			return jsonProvider.createArrayBuilder().add(firstElement).add(secondElement);
		}

		private static JsonObject createJsonObject() {
			return jsonProvider.createObjectBuilder()
				.add("firstPerson", createPerson("John", "Smith"))
				.add("secondPerson", createPerson("Deborah", "Harris"))
				.build();
		}

		private static JsonObjectBuilder createPerson(String name, String surname) {
			return jsonProvider.createObjectBuilder()
				.add("name", name)
				.add("surname", surname);
		}
	}
}
