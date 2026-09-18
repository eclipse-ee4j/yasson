/*
 * Copyright (c) 2025 IBM and/or its affiliates. All rights reserved.
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

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link jakarta.json.bind.annotation.JsonbTypeSerializer @JsonbTypeSerializer} annotated types are
 * properly detected and used when those types are used as elements/values in containers (Maps, Collections,
 * Arrays, Optionals).
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class TypeSerializerOnContainersTest {

    // Test interface with type-level serializer annotation
    @JsonbTypeSerializer(TestInterfaceSerializer.class)
    public interface TestInterface {
        String getValue();
    }

    // Implementation of the test interface
    public static class TestImpl implements TestInterface {
        private final String value;

        public TestImpl(final String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    // Custom serializer for TestInterface
    public static class TestInterfaceSerializer implements JsonbSerializer<TestInterface> {
        @Override
        public void serialize(final TestInterface obj, final JsonGenerator generator, final SerializationContext ctx) {
            generator.write("SERIALIZED:" + obj.getValue());
        }
    }

    // Container classes for testing
    public static class MapContainer {
        public final Map<String, TestInterface> map;
        public final Map<?, ?> questionKeyMap;
        public final Map<String, ?> questionValueMap;

        public MapContainer(final Map<String, TestInterface> map, final Map<?, ?> questionKeyMap, final Map<String, ?> questionValueMap) {
            this.map = map;
            this.questionKeyMap = questionKeyMap;
            this.questionValueMap = questionValueMap;
        }
    }

    public static class ListContainer {
        public final List<TestInterface> list;
        public final List<?> questionList;

        public ListContainer(final List<TestInterface> list, final List<?> questionList) {
            this.list = list;
            this.questionList = questionList;
        }
    }

    public static class ArrayContainer {
        public final TestInterface[] array;

        public ArrayContainer(TestInterface[] array) {
            this.array = array;
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class OptionalContainer {
        public final Optional<TestInterface> optional;
        public final Optional<?> questionOptional;

        public OptionalContainer(final Optional<TestInterface> optional, final Optional<?> questionOptional) {
            this.optional = optional;
            this.questionOptional = questionOptional;
        }
    }

    private Jsonb jsonb;

    @BeforeEach
    public void createJsonb() {
        // Create a new Jsonb for each test to avoid type caching
        jsonb = JsonbBuilder.create();
    }

    @AfterEach
    public void closeJsonb() throws Exception {
        if (jsonb != null) {
            jsonb.close();
        }
    }

    @Test
    public void testTypeSerializerOnMapValues() {
        final MapContainer container = new MapContainer(Map.of(
                "key1", new TestImpl("value1"),
                "key2", new TestImpl("value2")
        ), Map.of("qKey1", "value1", "qKey2", "value2"),
                Map.of("key1", "qValue1", "key2", "qValue2")
        );

        final JsonObject json = toJsonObject(container);
        final JsonObject map = json.getJsonObject("map");
        final JsonObject questionKeyMap = json.getJsonObject("questionKeyMap");
        final JsonObject questionValueMap = json.getJsonObject("questionValueMap");

        Supplier<String> errorMessage = () -> String.format("Expected value not found in %s", map);
        Assertions.assertEquals("SERIALIZED:value1", map.getString("key1"), errorMessage);
        Assertions.assertEquals("SERIALIZED:value2", map.getString("key2"), errorMessage);


        errorMessage = () -> String.format("Expected value not found in %s", questionKeyMap);
        Assertions.assertEquals("value1", questionKeyMap.getString("qKey1"), errorMessage);
        Assertions.assertEquals("value2", questionKeyMap.getString("qKey2"), errorMessage);


        errorMessage = () -> String.format("Expected value not found in %s", questionValueMap);
        Assertions.assertEquals("qValue1", questionValueMap.getString("key1"), errorMessage);
        Assertions.assertEquals("qValue2", questionValueMap.getString("key2"), errorMessage);
    }

    @Test
    public void testTypeSerializerOnListElements() {
        final ListContainer container = new ListContainer(List.of(
                new TestImpl("value1"),
                new TestImpl("value2")
        ), List.of("qValue1", "qValue2"));

        final JsonObject json = toJsonObject(container);
        final JsonArray list = json.getJsonArray("list");
        final JsonArray questionList = json.getJsonArray("questionList");

        Supplier<String> errorMessage = () -> String.format("Expected value not found in %s", list);
        Assertions.assertEquals(2, list.size(), () -> String.format("Expected a size of 2 in %s", list));
        Assertions.assertEquals("SERIALIZED:value1", list.getString(0), errorMessage);
        Assertions.assertEquals("SERIALIZED:value2", list.getString(1), errorMessage);

        errorMessage = () -> String.format("Expected value not found in %s", questionList);
        Assertions.assertEquals(2, questionList.size(), () -> String.format("Expected a size of 2 in %s", questionList));
        Assertions.assertEquals("qValue1", questionList.getString(0), errorMessage);
        Assertions.assertEquals("qValue2", questionList.getString(1), errorMessage);
    }

    @Test
    public void testTypeSerializerOnArrayElements() {
        final ArrayContainer container = new ArrayContainer(new TestInterface[] {
                new TestImpl("value1"),
                new TestImpl("value2")
        });

        final String json = jsonb.toJson(container);

        Assertions.assertEquals("{\"array\":[\"SERIALIZED:value1\",\"SERIALIZED:value2\"]}", json);
    }

    @Test
    public void testTypeSerializerOnOptionalValue() {
        final OptionalContainer container = new OptionalContainer(Optional.of(new TestImpl("value1")), Optional.of("value2"));

        final JsonObject json = toJsonObject(container);

        Assertions.assertEquals("SERIALIZED:value1", json.getString("optional"));
        Assertions.assertEquals("value2", json.getString("questionOptional"));
    }

    private JsonObject toJsonObject(final Object object) throws JsonbException {
        final String value = jsonb.toJson(object);
        try (
                StringReader reader = new StringReader(value);
                JsonReader jsonReader = Json.createReader(reader)
        ) {
            return jsonReader.readObject();
        }
    }
}
