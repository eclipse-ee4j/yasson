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
    }

    public static class ListContainer {
        public List<TestInterface> list;
    }

    public static class ArrayContainer {
        public TestInterface[] array;
    }

    public static class OptionalContainer {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Optional<TestInterface> optional;
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
        final String json = "{\"map\":{\"key1\":{\"value\":\"value1\"},\"key2\":{\"value\":\"value2\"}}}";

        final MapContainer result = jsonb.fromJson(json, MapContainer.class);

        Assertions.assertNotNull(result.map);
        Assertions.assertEquals(2, result.map.size(), () -> String.format("Expected two entries got %s", result.map));
        Assertions.assertEquals("DESERIALIZED:value1", result.map.get("key1").getValue());
        Assertions.assertEquals("DESERIALIZED:value2", result.map.get("key2").getValue());
    }

    @Test
    public void testTypeDeserializerOnListElements() {
        final String json = "{\"list\":[{\"value\":\"value1\"},{\"value\":\"value2\"}]}";

        final ListContainer result = jsonb.fromJson(json, ListContainer.class);

        Assertions.assertNotNull(result.list);
        Assertions.assertEquals(2, result.list.size(), () -> String.format("Expected two entries got %s", result.list));
        Assertions.assertEquals("DESERIALIZED:value1", result.list.get(0).getValue());
        Assertions.assertEquals("DESERIALIZED:value2", result.list.get(1).getValue());
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
        final String json = "{\"optional\":{\"value\":\"value1\"}}";

        final OptionalContainer result = jsonb.fromJson(json, OptionalContainer.class);

        Assertions.assertNotNull(result.optional);
        Assertions.assertTrue(result.optional.isPresent(), "Expected value to be present, but the optional was empty.");
        Assertions.assertEquals("DESERIALIZED:value1", result.optional.get().getValue());
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
