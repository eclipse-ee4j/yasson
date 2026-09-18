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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link jakarta.json.bind.annotation.JsonbTypeDeserializer @JsonbTypeDeserializer} annotated types are
 * properly detected and used when those types are used as elements/values in containers (Maps, Collections,
 * Arrays, Optionals).
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class TypeDeserializerOnContainersTest {

    // Test interface with type-level deserializer annotation
    @JsonbTypeDeserializer(TestInterfaceDeserializer.class)
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

    // Custom deserializer for TestInterface
    public static class TestInterfaceDeserializer implements JsonbDeserializer<TestInterface> {
        @Override
        public TestInterface deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
            // Parse the JSON object to get the value field
            Assertions.assertTrue(parser.hasNext(), "Expected the key name");
            parser.next();
            Assertions.assertTrue(parser.hasNext(), "Expected the value");
            parser.next();
            final String value = parser.getString();
            Assertions.assertTrue(parser.hasNext(), "Expected the end of an object");
            parser.next();
            return new TestImpl("DESERIALIZED:" + value);
        }
    }

    // Container classes for testing
    public static class MapContainer {
        public Map<String, TestInterface> map;
        public Map<?, ?> questionKeyMap;
        public Map<String, ?> questionValueMap;
    }

    public static class ListContainer {
        public List<TestInterface> list;
        public List<?> questionList;
    }

    public static class ArrayContainer {
        public TestInterface[] array;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class OptionalContainer {
        public Optional<TestInterface> optional;
        public Optional<?> questionOptional;
    }

    public static class ByteArrayContainer {
        public byte[] data;
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
    public void testTypeDeserializerOnMapValues() {
        final String json = "{\"map\":{\"key1\":{\"value\":\"value1\"},\"key2\":{\"value\":\"value2\"}}, \"questionKeyMap\":{\"qKey1\":\"value1\",\"qKey2\":\"value2\"},\"questionValueMap\":{\"key1\":\"qValue1\",\"key2\":\"qValue2\"}}";

        final MapContainer result = jsonb.fromJson(json, MapContainer.class);

        Assertions.assertNotNull(result.map);
        Assertions.assertEquals(2, result.map.size(), () -> String.format("Expected two entries got %s", result.map));
        Assertions.assertEquals("DESERIALIZED:value1", result.map.get("key1").getValue());
        Assertions.assertEquals("DESERIALIZED:value2", result.map.get("key2").getValue());

        Assertions.assertNotNull(result.questionKeyMap);
        Assertions.assertEquals(2, result.questionKeyMap.size(), () -> String.format("Expected two entries got %s", result.questionKeyMap));
        Assertions.assertEquals("value1", result.questionKeyMap.get("qKey1"));
        Assertions.assertEquals("value2", result.questionKeyMap.get("qKey2"));

        Assertions.assertNotNull(result.questionValueMap);
        Assertions.assertEquals(2, result.questionValueMap.size(), () -> String.format("Expected two entries got %s", result.questionValueMap));
        Assertions.assertEquals("qValue1", result.questionValueMap.get("key1"));
        Assertions.assertEquals("qValue2", result.questionValueMap.get("key2"));
    }

    @Test
    public void testTypeDeserializerOnListElements() {
        final String json = "{\"list\":[{\"value\":\"value1\"},{\"value\":\"value2\"}], \"questionList\": [\"value1\", \"value2\"]}";

        final ListContainer result = jsonb.fromJson(json, ListContainer.class);

        Assertions.assertNotNull(result.list);
        Assertions.assertEquals(2, result.list.size(), () -> String.format("Expected two entries got %s", result.list));
        Assertions.assertEquals("DESERIALIZED:value1", result.list.get(0).getValue());
        Assertions.assertEquals("DESERIALIZED:value2", result.list.get(1).getValue());

        Assertions.assertNotNull(result.questionList);
        Assertions.assertEquals(2, result.questionList.size(), () -> String.format("Expected two entries got %s", result.questionList));
        Assertions.assertEquals("value1", result.questionList.get(0));
        Assertions.assertEquals("value2", result.questionList.get(1));
    }

    @Test
    public void testTypeDeserializerOnArrayElements() {
        final String json = "{\"array\":[{\"value\":\"value1\"},{\"value\":\"value2\"}]}";

        final ArrayContainer result = jsonb.fromJson(json, ArrayContainer.class);

        Assertions.assertNotNull(result.array);
        Assertions.assertEquals(2, result.array.length, () -> String.format("Expected two entries got %s", Arrays.toString(result.array)));
        Assertions.assertEquals("DESERIALIZED:value1", result.array[0].getValue());
        Assertions.assertEquals("DESERIALIZED:value2", result.array[1].getValue());
    }

    @Test
    public void testTypeDeserializerOnOptionalValue() {
        final String json = "{\"optional\":{\"value\":\"value1\"},\"questionOptional\":\"value2\"}";

        final OptionalContainer result = jsonb.fromJson(json, OptionalContainer.class);

        Assertions.assertNotNull(result.optional);
        Assertions.assertTrue(result.optional.isPresent(), "Expected value to be present, but the optional was empty.");
        Assertions.assertEquals("DESERIALIZED:value1", result.optional.get().getValue());

        Assertions.assertNotNull(result.questionOptional);
        Assertions.assertTrue(result.questionOptional.isPresent(), "Expected value to be present, but the optional was empty.");
        Assertions.assertEquals("value2", result.questionOptional.get());
    }

    @Test
    public void testTypeDeserializerOnByteArray() {
        final String json = "{\"data\":[1,2,3,4,5]}";

        final ByteArrayContainer result = jsonb.fromJson(json, ByteArrayContainer.class);

        Assertions.assertNotNull(result.data);
        Assertions.assertEquals(5, result.data.length);
        Assertions.assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, result.data);
    }

    @Test
    public void testTypeDeserializerOnByteArrayWithBase64() throws Exception {
        try (Jsonb base64Jsonb = JsonbBuilder.create(new JsonbConfig()
                .withBinaryDataStrategy(BinaryDataStrategy.BASE_64))) {

            // "SGVsbG8=" is "Hello" in base64
            final String json = "{\"data\":\"SGVsbG8=\"}";

            final ByteArrayContainer result = base64Jsonb.fromJson(json, ByteArrayContainer.class);

            Assertions.assertNotNull(result.data);
            Assertions.assertArrayEquals("Hello".getBytes(), result.data);
        }
    }
}
