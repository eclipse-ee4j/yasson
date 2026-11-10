/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests verifying that {@link org.eclipse.yasson.internal.serializer.MapSerializer} handles
 * maps with {@code Object} key types dynamically across repeated and concurrent invocations
 * without caching state that poisons subsequent serializations.
 */
public class DynamicMapSerializerTest {

    public static class CustomKey {
        private String name;

        public CustomKey(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MapHolder {
        private Map<Object, String> map = new LinkedHashMap<>();

        public Map<Object, String> getMap() {
            return map;
        }

        public void setMap(Map<Object, String> map) {
            this.map = map;
        }
    }

    private static final Type MAP_OBJECT_STRING_TYPE = new TestTypeToken<Map<Object, String>>() { }.getType();

    /**
     * Scenario 1: String-keyed map serialized first, followed by Object-keyed map on the same Jsonb instance.
     * The second serialization must produce an array of key-value entry objects and not fail with an exception.
     */
    @Test
    public void testStringKeyFollowedByObjectKey() throws Exception {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            // 1. Serialize map with String key (should serialize as JSON object)
            MapHolder holder1 = new MapHolder();
            holder1.getMap().put("strKey", "value1");
            String json1 = jsonb.toJson(holder1);
            assertEquals("{\"map\":{\"strKey\":\"value1\"}}", json1);

            // 2. Serialize map with CustomKey (should serialize as array of entry objects)
            MapHolder holder2 = new MapHolder();
            holder2.getMap().put(new CustomKey("custom1"), "value2");
            String json2 = jsonb.toJson(holder2);
            assertEquals("{\"map\":[{\"key\":{\"name\":\"custom1\"},\"value\":\"value2\"}]}", json2);

            // Also test root-level Map<Object, String> serialization
            Map<Object, String> rootMap1 = new LinkedHashMap<>();
            rootMap1.put("k1", "v1");
            assertEquals("{\"k1\":\"v1\"}", jsonb.toJson(rootMap1, MAP_OBJECT_STRING_TYPE));

            Map<Object, String> rootMap2 = new LinkedHashMap<>();
            rootMap2.put(new CustomKey("k2"), "v2");
            assertEquals("[{\"key\":{\"name\":\"k2\"},\"value\":\"v2\"}]", jsonb.toJson(rootMap2, MAP_OBJECT_STRING_TYPE));
        }
    }

    /**
     * Scenario 2: Object-keyed map serialized first, followed by String-keyed map on the same Jsonb instance.
     * The second serialization must produce a standard JSON object and not be poisoned into an entry array.
     */
    @Test
    public void testObjectKeyFollowedByStringKey() throws Exception {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            // 1. Serialize map with CustomKey (should serialize as array of entry objects)
            MapHolder holder1 = new MapHolder();
            holder1.getMap().put(new CustomKey("custom1"), "value1");
            String json1 = jsonb.toJson(holder1);
            assertEquals("{\"map\":[{\"key\":{\"name\":\"custom1\"},\"value\":\"value1\"}]}", json1);

            // 2. Serialize map with String key (should serialize as standard JSON object)
            MapHolder holder2 = new MapHolder();
            holder2.getMap().put("strKey", "value2");
            String json2 = jsonb.toJson(holder2);
            assertEquals("{\"map\":{\"strKey\":\"value2\"}}", json2);
        }
    }

    /**
     * Scenario 3: Null-keyed map with FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS enabled,
     * followed by a standard String-keyed map on the same Jsonb instance.
     * The second serialization must not be forced into an array representation.
     */
    @Test
    public void testNullKeyWithForceMapArrayFollowedByStringKey() throws Exception {
        JsonbConfig config = new JsonbConfig()
                                .setProperty(YassonConfig.FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS, true);

        try (Jsonb jsonb = JsonbBuilder.create(config)) {
            // 1. Serialize map with null key (forced to array of entry objects)
            MapHolder holder1 = new MapHolder();
            holder1.getMap().put(null, "value1");
            String json1 = jsonb.toJson(holder1);
            assertEquals("{\"map\":[{\"key\":null,\"value\":\"value1\"}]}", json1);

            // 2. Serialize map with valid String key (should serialize as JSON object)
            MapHolder holder2 = new MapHolder();
            holder2.getMap().put("validKey", "value2");
            String json2 = jsonb.toJson(holder2);
            assertEquals("{\"map\":{\"validKey\":\"value2\"}}", json2);
        }
    }

    /**
     * Scenario 4: Concurrent serialization of maps with String keys and Object keys
     * using the same shared Jsonb instance.
     */
    @Test
    public void testConcurrentDynamicMapSerialization() throws Exception {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            int threadCount = 8;
            int iterations = 100;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            
            AtomicReference<Throwable> errorRef = new AtomicReference<>();

            for (int i = 0; i < threadCount; i++) {
                final boolean useStringKey = (i % 2 == 0);
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        for (int j = 0; j < iterations; j++) {
                            if (useStringKey) {
                                MapHolder holder = new MapHolder();
                                holder.getMap().put("key" + j, "val" + j);
                                String json = jsonb.toJson(holder);
                                assertEquals("{\"map\":{\"key" + j + "\":\"val" + j + "\"}}", json);
                            } else {
                                MapHolder holder = new MapHolder();
                                holder.getMap().put(new CustomKey("custom" + j), "val" + j);
                                String json = jsonb.toJson(holder);
                                assertEquals("{\"map\":[{\"key\":{\"name\":\"custom" + j + "\"},\"value\":\"val" + j + "\"}]}", json);
                            }
                        }
                    } catch (Throwable t) {
                        errorRef.compareAndSet(null, t);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            assertNull(errorRef.get(), () -> "Concurrent serialization error: " + errorRef.get());
        }
    }
}
