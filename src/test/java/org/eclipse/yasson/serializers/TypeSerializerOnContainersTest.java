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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
        public Map<String, TestInterface> map;

        public MapContainer(Map<String, TestInterface> map) {
            this.map = map;
        }
    }

    public static class ListContainer {
        public List<TestInterface> list;

        public ListContainer(List<TestInterface> list) {
            this.list = list;
        }
    }

    public static class ArrayContainer {
        public TestInterface[] array;

        public ArrayContainer(TestInterface[] array) {
            this.array = array;
        }
    }

    public static class OptionalContainer {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Optional<TestInterface> optional;

        public OptionalContainer(Optional<TestInterface> optional) {
            this.optional = optional;
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
        ));

        final String json = jsonb.toJson(container);

        Assertions.assertTrue(json.contains("\"key1\":\"SERIALIZED:value1\""),
                "Expected serialized value1 but got: " + json);
        Assertions.assertTrue(json.contains("\"key2\":\"SERIALIZED:value2\""),
                "Expected serialized value2 but got: " + json);
    }

    @Test
    public void testTypeSerializerOnListElements() {
        final ListContainer container = new ListContainer(List.of(
                new TestImpl("value1"),
                new TestImpl("value2")
        ));

        final String json = jsonb.toJson(container);

        Assertions.assertEquals("{\"list\":[\"SERIALIZED:value1\",\"SERIALIZED:value2\"]}", json);
    }

    @Test
    public void testTypeSerializerOnArrayElements() {
        final ArrayContainer container = new ArrayContainer(new TestInterface[]{
                new TestImpl("value1"),
                new TestImpl("value2")
        });

        final String json = jsonb.toJson(container);

        Assertions.assertEquals("{\"array\":[\"SERIALIZED:value1\",\"SERIALIZED:value2\"]}", json);
    }

    @Test
    public void testTypeSerializerOnOptionalValue() {
        final OptionalContainer container = new OptionalContainer(Optional.of(new TestImpl("value1")));

        final String json = jsonb.toJson(container);

        Assertions.assertEquals("{\"optional\":\"SERIALIZED:value1\"}", json);
    }
}
