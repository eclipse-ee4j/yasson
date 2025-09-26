/*
 * Copyright (c) 2016, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for @JsonbTypeAdapter consistency issues with collections and maps.
 * These tests verify the fix for the bug where class-level @JsonbTypeAdapter
 * worked for single objects but was ignored for collection elements.
 * <a href="https://github.com/eclipse-ee4j/yasson/issues/652">Issue#652</a> &
 * <a href="https://github.com/eclipse-ee4j/yasson/issues/603">Issue#603</a>
 */
public class JsonbTypeAdapterCollectionTest {

    @Test
    public void classLevelAdapterShouldApplyToSingleObjects() throws Exception {
        ClassLevelAdapterType obj = new ClassLevelAdapterType();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(obj);
            assertEquals("\"adapted_value\"", json);
        }
    }

    @Test
    public void classLevelAdapterShouldApplyToCollectionElements() throws Exception {
        ClassLevelAdapterType obj = new ClassLevelAdapterType();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(Set.of(obj));
            assertEquals("[\"adapted_value\"]", json);
        }
    }

    @Test
    public void classLevelAdapterShouldApplyToListElements() throws Exception {
        ClassLevelAdapterType obj1 = new ClassLevelAdapterType();
        ClassLevelAdapterType obj2 = new ClassLevelAdapterType();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(List.of(obj1, obj2));
            assertEquals("[\"adapted_value\",\"adapted_value\"]", json);
        }
    }

    @Test
    public void classLevelAdapterShouldApplyToArrayElements() throws Exception {
        ClassLevelAdapterType obj1 = new ClassLevelAdapterType();
        ClassLevelAdapterType obj2 = new ClassLevelAdapterType();
        ClassLevelAdapterType[] array = {obj1, obj2};

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(array);
            assertEquals("[\"adapted_value\",\"adapted_value\"]", json);
        }
    }

    @Test
    public void classLevelAdapterShouldApplyConsistentlyRegardlessOfOrder() throws Exception {
        ClassLevelAdapterType obj = new ClassLevelAdapterType();

        // Test collection first, then single - should be consistent
        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String collectionJson = jsonb.toJson(Set.of(obj));
            String singleJson = jsonb.toJson(obj);

            assertEquals("[\"adapted_value\"]", collectionJson);
            assertEquals("\"adapted_value\"", singleJson);
        }

        // Test single first, then collection - should be consistent  
        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String singleJson = jsonb.toJson(obj);
            String collectionJson = jsonb.toJson(Set.of(obj));

            assertEquals("\"adapted_value\"", singleJson);
            assertEquals("[\"adapted_value\"]", collectionJson);
        }
    }

    @Test
    public void configAdapterWorksCorrectlyForCollectionElements() throws Exception {
        ClassLevelAdapterType obj = new ClassLevelAdapterType();
        var config = new JsonbConfig().withAdapters(new ClassLevelAdapter());

        try (var jsonb = JsonbBuilder.newBuilder().withConfig(config).build()) {
            String single = jsonb.toJson(obj);
            String set = jsonb.toJson(Set.of(obj));

            assertEquals("\"adapted_value\"", single);
            assertEquals("[\"adapted_value\"]", set);
        }
    }

    @Test
    public void fieldLevelAdapterShouldApplyToFieldsInObjects() throws Exception {
        ContainerWithFieldAdapter container = new ContainerWithFieldAdapter();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(container);
            assertEquals("{\"item\":\"field_adapted\"}", json);
        }
    }

    @Test
    public void fieldLevelAdapterShouldApplyToFieldsInCollections() throws Exception {
        ContainerWithFieldAdapter container1 = new ContainerWithFieldAdapter();
        ContainerWithFieldAdapter container2 = new ContainerWithFieldAdapter();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String listJson = jsonb.toJson(List.of(container1, container2));
            assertEquals("[{\"item\":\"field_adapted\"},{\"item\":\"field_adapted\"}]", listJson);
        }
    }

    @Test
    public void fieldLevelAdapterShouldApplyToFieldsInArrays() throws Exception {
        ContainerWithFieldAdapter container1 = new ContainerWithFieldAdapter();
        ContainerWithFieldAdapter container2 = new ContainerWithFieldAdapter();
        ContainerWithFieldAdapter[] array = {container1, container2};

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String arrayJson = jsonb.toJson(array);
            assertEquals("[{\"item\":\"field_adapted\"},{\"item\":\"field_adapted\"}]", arrayJson);
        }
    }

    @Test
    public void nestedCollectionsWithAdaptersShouldWork() throws Exception {
        ClassLevelAdapterType obj1 = new ClassLevelAdapterType();
        ClassLevelAdapterType obj2 = new ClassLevelAdapterType();
        List<List<ClassLevelAdapterType>> nested = List.of(
                List.of(obj1, obj2),
                List.of(obj1)
        );

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String json = jsonb.toJson(nested);
            assertEquals("[[\"adapted_value\",\"adapted_value\"],[\"adapted_value\"]]", json);
        }
    }

    @Test
    public void mixedCollectionTypesWithAdaptersShouldWork() throws Exception {
        ClassLevelAdapterType obj1 = new ClassLevelAdapterType();
        ClassLevelAdapterType obj2 = new ClassLevelAdapterType();

        try (Jsonb jsonb = JsonbBuilder.create()) {
            // Test List
            String listJson = jsonb.toJson(List.of(obj1, obj2));
            assertEquals("[\"adapted_value\",\"adapted_value\"]", listJson);

            // Test Set (reusing the same jsonb instance to test caching)
            String setJson = jsonb.toJson(Set.of(obj1, obj2));
            assertEquals("[\"adapted_value\",\"adapted_value\"]", setJson);

            // Test Array (reusing the same jsonb instance to test caching)
            ClassLevelAdapterType[] array = {obj1, obj2};
            String arrayJson = jsonb.toJson(array);
            assertEquals("[\"adapted_value\",\"adapted_value\"]", arrayJson);
        }
    }

    @Test
    public void adapterShouldApplyToMapValues() throws Exception {
        String key1 = "key1";
        String key2 = "key2";
        ClassLevelAdapterType value1 = new ClassLevelAdapterType();
        ClassLevelAdapterType value2 = new ClassLevelAdapterType();
        Map<String, ClassLevelAdapterType> map = Map.of(key1, value1, key2, value2);

        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String json = jsonb.toJson(map);
            // Values should use class-level adapter
            // Map with string keys serializes as a plain JSON object
            boolean isCorrect = json.equals("{\"key1\":\"adapted_value\",\"key2\":\"adapted_value\"}") ||
                    json.equals("{\"key2\":\"adapted_value\",\"key1\":\"adapted_value\"}");
            assertTrue(isCorrect, "Expected map values to use adapter, got: " + json);
        }
    }

    @Test
    public void adapterShouldApplyToBothMapKeysAndValues() throws Exception {
        ClassLevelAdapterType key = new ClassLevelAdapterType();
        ClassLevelAdapterType value = new ClassLevelAdapterType();
        Map<ClassLevelAdapterType, ClassLevelAdapterType> map = Map.of(key, value);

        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String json = jsonb.toJson(map);
            // Both key and value should use class-level adapter
            assertEquals("[{\"key\":\"adapted_value\",\"value\":\"adapted_value\"}]", json);
        }
    }

    @Test
    public void adapterShouldApplyToMapKeysWithMixedValueTypes() throws Exception {
        ClassLevelAdapterType key = new ClassLevelAdapterType();
        ContainerWithFieldAdapter value = new ContainerWithFieldAdapter();
        Map<ClassLevelAdapterType, ContainerWithFieldAdapter> map = Map.of(key, value);

        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String json = jsonb.toJson(map);
            // Key should use class-level adapter, value should use field-level adapter for its field
            assertEquals("[{\"key\":\"adapted_value\",\"value\":{\"item\":\"field_adapted\"}}]", json);
        }
    }

    @Test
    public void mapKeysWithoutAdapterShouldSerializeNormally() throws Exception {
        SimpleType key = new SimpleType();
        SimpleType value = new SimpleType();
        Map<SimpleType, SimpleType> map = Map.of(key, value);

        try (var jsonb = JsonbBuilder.newBuilder().build()) {
            String json = jsonb.toJson(map);
            assertEquals("[{\"key\":{\"data\":\"simple\"},\"value\":{\"data\":\"simple\"}}]", json);
        }
    }

    // Helper classes for testing
    @JsonbTypeAdapter(ClassLevelAdapter.class)
    public static class ClassLevelAdapterType {
        public String wrong = "should_not_appear";
    }

    public static class ClassLevelAdapter implements JsonbAdapter<ClassLevelAdapterType, String> {
        @Override
        public String adaptToJson(ClassLevelAdapterType obj) throws Exception {
            return "adapted_value";
        }

        @Override
        public ClassLevelAdapterType adaptFromJson(String obj) throws Exception {
            throw new UnsupportedOperationException("Deserialization not implemented for testing purposes");
        }
    }

    public static class ContainerWithFieldAdapter {
        @JsonbTypeAdapter(FieldLevelAdapter.class)
        public final SimpleType item = new SimpleType();
    }

    public static class FieldLevelAdapter implements JsonbAdapter<SimpleType, String> {
        @Override
        public String adaptToJson(SimpleType obj) throws Exception {
            return "field_adapted";
        }

        @Override
        public SimpleType adaptFromJson(String obj) throws Exception {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    public static class SimpleType {
        public String data = "simple";

        @Override
        public String toString() {
            return "SimpleType";
        }
    }
}
