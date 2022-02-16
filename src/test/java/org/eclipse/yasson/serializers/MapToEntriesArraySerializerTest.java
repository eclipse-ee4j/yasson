/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.serializers.model.Pokemon;
import org.eclipse.yasson.serializers.model.Trainer;

/**
 * Test various use-cases with {@code Map} serializer and de-serializer which
 * stores Map.Entries as JSON objects in 1:1 relation.
 */
public class MapToEntriesArraySerializerTest {

    /**
     * Comparator that allows to compare at least some similar Number instances.
     * Does not work for everything, but it's sufficient for used test cases.
     */
    private static final class NumberComparator implements Comparator<Number> {

        @Override
        public int compare(Number n1, Number n2) {
            if (n1 != null && n2 != null) {
                if (((n1 instanceof Float) || (n1 instanceof Double))
                    && ((n2 instanceof Float) || (n2 instanceof Double))) {
                    return Double.compare(n1.doubleValue(), n2.doubleValue());
                }
                return Long.compare(n1.longValue(), n2.longValue());
            }
            if (n1 == null) {
                return n2 == null ? 0 : -1;
            } else {
                return 1;
            }
        }
    }

    /** NumberComparator instance to be used. */
    private static final Comparator<Number> CMP_NUM = new NumberComparator();

    // Verification code for serialization: Covers only use-cases used in jUnit tests.
    // * Key JsonObject is always mapped to Trainer PoJo.
    // * Value JsonObject is always mapped to Pokemon Pojo.

    /**
     * Verify that source Map value and parsed Map entry JsonObject value are equals.
     *
     * @param jentry parsed Map entry as JsonObject
     * @param source source Map for value verification
     * @param key Map key used to retrieve value
     */
    private static final <K,V> void verifyMapValues(JsonObject jentry, Map<K,V> source, K key) {
        assertNotNull(jentry);
        assertNotNull(source);
        assertNotNull(key);
        V sourceValue = source.get(key);
        assertNotNull(sourceValue);
        switch (jentry.getValue("/value").getValueType()) {
            // Value contains JSON object: it shall be Pokemon PoJo
            case OBJECT:
                JsonObject valueObject = jentry.getJsonObject("value");
                Pokemon sourcePoJo = (Pokemon) source.get(key);
                Pokemon valuePojo = new Pokemon(
                        valueObject.getString("name"), valueObject.getString("type"), valueObject.getInt("cp"));
                assertEquals(sourcePoJo, valuePojo);
                break;
            case STRING:
                String valueString = jentry.getString("value");
                String sourceString = (String) source.get(key);
                assertEquals(sourceString, valueString);
                break;
            case NUMBER:
                Number valueNumber = jentry.getJsonNumber("value").numberValue();
                Number sourceNumber = (Number) source.get(key);
                // Number comparator shall be used here because values may not be of the same type.
                assertEquals(0, CMP_NUM.compare(sourceNumber, valueNumber));
                break;
            case TRUE:
            case FALSE:
                Boolean valueBool = jentry.getBoolean("value");
                Boolean sourceBool = (Boolean) source.get(key);
                assertEquals(sourceBool, valueBool);
                break;
            default:
                throw new IllegalStateException(jentry.getValue("/value").getValueType() + "was not expected");
        }
    }

    /**
     * Verify that source Map value and parsed Map entry JsonObject value are equals.
     *
     * @param jentry parsed Map entry as JsonObject
     * @param sourceEntry source Map entry for value verification
     */
    private static final <K,V> void verifyMapValues(JsonObject jentry, Map.Entry<K[],V> sourceEntry) {
        assertNotNull(jentry);
        assertNotNull(sourceEntry);
        switch (jentry.getValue("/value").getValueType()) {
            // Value contains JSON object: it shall be Pokemon PoJo
            case OBJECT:
                JsonObject valueObject = jentry.getJsonObject("value");
                Pokemon sourcePoJo = (Pokemon) sourceEntry.getValue();
                Pokemon valuePojo = new Pokemon(
                        valueObject.getString("name"), valueObject.getString("type"), valueObject.getInt("cp"));
                assertEquals(sourcePoJo, valuePojo);
                break;
            case ARRAY:
                JsonArray valueArray = jentry.getJsonArray("value");
                assertTrue(valueArray.size() > 0);
                verifyMapArrayValue(jentry, valueArray, sourceEntry);
                break;
            case STRING:
                String valueString = jentry.getString("value");
                String sourceString = (String) sourceEntry.getValue();
                assertEquals(sourceString, valueString);
                break;
            case NUMBER:
                Number valueNumber = jentry.getJsonNumber("value").numberValue();
                Number sourceNumber = (Number) sourceEntry.getValue();
                // Number comparator shall be used here because values may not be of the same type.
                assertEquals(0, CMP_NUM.compare(sourceNumber, valueNumber));
                break;
            case TRUE:
            case FALSE:
                Boolean valueBool = jentry.getBoolean("value");
                Boolean sourceBool = (Boolean) sourceEntry.getValue();
                assertEquals(sourceBool, valueBool);
                break;
            default:
                throw new IllegalStateException(jentry.getValue("/value").getValueType() + "was not expected");
        }
    }

    /**
     * Retrieve Map.Entry with matching array key from source Map.
     *
     * @param source source Map
     * @param key array key to search for
     * @param cmp optional comparator to use for search
     * @param keys source map key Set used to check whether all keys were processed. Key will be removed from set on successful match
     * @return Map.Entry matching provided key
     */
    private static final <K,V> Map.Entry<K[], V> getMapEntryForArrayKey(Map<K[], V> source, K[] key, Comparator<K> cmp, Set<K> keys) {
        for (Map.Entry<K[], V> entry : source.entrySet()) {
            K[] sourceKey = entry.getKey();
            boolean match = key.length == sourceKey.length;
            if (match) {
                for (int i = 0; i < key.length && match; i++) {
                    if (cmp != null) {
                        match = (cmp.compare(key[i], sourceKey[i]) == 0);
                    } else {
                        match = key[i].equals(sourceKey[i]);
                    }
                }
                // Matching key is removed from Set for key processing check
                if (match) {
                    keys.remove(entry.getKey());
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * Verify that source Map array value and parsed map array value are the same.
     *
     * @param value parsed value
     * @param sourceValue source Map value
     * @param cmp optional comparator to use for verification
     */
    private static final <V> void verifyMapArrayValues(V[] value, V[] sourceValue, Comparator<V> cmp) {
        assertEquals(sourceValue.length, value.length);
        for (int i = 0; i < sourceValue.length; i++) {
            if (cmp != null) {
                assertTrue(cmp.compare(sourceValue[i], value[i]) == 0);
            } else {
                assertEquals(sourceValue[i], value[i]);
            }
        }
    }

    /**
     * Build Map key as an array. Get corresponding key from source Map.
     *
     * @param jentry Map key parsed as JsonArray
     * @param sourceEntry source Map
     */
    @SuppressWarnings("unchecked")
    private static final <K,V> void verifyMapArrayValue(JsonObject jentry, final JsonArray valueArray, Map.Entry<K[],V> sourceEntry) {
        int size = valueArray.size();
        // All array elements in the tests are of the same type.
        switch (valueArray.get(0).getValueType()) {
            case OBJECT:
                V[] keyPoJo = (V[]) new Pokemon[size];
                for (int i = 0; i < size; i++) {
                    JsonObject keyItem = valueArray.getJsonObject(i);
                    keyPoJo[i] = (V) new Pokemon(keyItem.getString("name"), keyItem.getString("type"), keyItem.getInt("cp"));
                }
                verifyMapArrayValues(keyPoJo, (V[]) sourceEntry.getValue(), null);
                break;
            case STRING:
                V[] keyString = (V[]) new String[size];
                for (int i = 0; i < size; i++) {
                    keyString[i] = (V) valueArray.getString(i);
                }
                verifyMapArrayValues(keyString, (V[]) sourceEntry.getValue(), null);
                break;
            case NUMBER:
                V[] keyNumber = (V[]) new Number[size];
                for (int i = 0; i < size; i++) {
                    keyNumber[i] = (V) valueArray.getJsonNumber(i).numberValue();
                }
                verifyMapArrayValues(keyNumber, (V[]) sourceEntry.getValue(), (Comparator<V>) CMP_NUM);
                break;
            case TRUE:
            case FALSE:
                V[] keyBool = (V[]) new Boolean[size];
                for (int i = 0; i < size; i++) {
                    keyBool[i] = (V) Boolean.valueOf(valueArray.getBoolean(i));
                }
                verifyMapArrayValues(keyBool, (V[]) sourceEntry.getValue(), null);
                break;
            default:
                throw new IllegalStateException(valueArray.getValueType() + "was not expected");
        }
    }

    /**
     * Build Map key as an array. Get corresponding key from source Map.
     *
     * @param keyArray Map key parsed as JsonArray
     * @param source source Map
     */
    @SuppressWarnings("unchecked")
    private static final <K,V> void verifyMapArrayKey(JsonObject jentry, final JsonArray keyArray, Map<K,V> source, Set<K> keys) {
        int size = keyArray.size();
        // All array elements in the tests are of the same type.
        switch (keyArray.get(0).getValueType()) {
            case OBJECT:
                K[] keyPoJo = (K[]) new Trainer[size];
                for (int i = 0; i < size; i++) {
                    JsonObject keyItem = keyArray.getJsonObject(i);
                    keyPoJo[i] = (K) new Trainer(keyItem.getString("name"), keyItem.getInt("age"));
                }
                Map.Entry<K[],V> entryObject = getMapEntryForArrayKey((Map<K[],V>) source, keyPoJo, null, keys);
                verifyMapValues(jentry, entryObject);
                break;
            case STRING:
                K[] keyString = (K[]) new String[size];
                for (int i = 0; i < size; i++) {
                    keyString[i] = (K) keyArray.getString(i);
                }
                Map.Entry<K[],V> entryString = getMapEntryForArrayKey((Map<K[],V>) source, keyString, null, keys);
                verifyMapValues(jentry, entryString);
                break;
            case NUMBER:
                K[] keyNumber = (K[]) new Number[size];
                for (int i = 0; i < size; i++) {
                    keyNumber[i] = (K) keyArray.getJsonNumber(i).numberValue();
                }
                Map.Entry<K[],V> entryNumber = getMapEntryForArrayKey((Map<K[],V>) source, keyNumber, (Comparator<K>) CMP_NUM, keys);
                verifyMapValues(jentry, entryNumber);
                break;
            case TRUE:
            case FALSE:
                K[] keyBool = (K[]) new Boolean[size];
                for (int i = 0; i < size; i++) {
                    keyBool[i] = (K) Boolean.valueOf(keyArray.getBoolean(i));
                }
                Map.Entry<K[],V> entryBool = getMapEntryForArrayKey((Map<K[],V>) source, keyBool, null, keys);
                verifyMapValues(jentry, entryBool);
                break;
            default:
                throw new IllegalStateException(keyArray.getValueType() + "was not expected");
        }
    }

    /**
     * Verify that serialized Map provided as JsonArray matches source Map.
     *
     * @param source source Map
     * @param array  serialized Map parsed and provided as JsonArray
     */
    @SuppressWarnings("unchecked")
    private static final <K,V> void verifySerialization(Map<K, V> source, JsonArray array) {
        assertEquals(source.size(), array.size());
        Set<K> keys = source.keySet();
        array.forEach(entry -> {
            JsonObject jentry = entry.asJsonObject();
            //JsonValue key = jentry.getValue("/key");
            switch (jentry.getValue("/key").getValueType()) {
                // Key contains JSON object: it shall be Trainer PoJo
                case OBJECT: {
                    JsonObject keyValue = jentry.getJsonObject("key");
                    Trainer keyPoJo = new Trainer(keyValue.getString("name"), keyValue.getInt("age"));
                    verifyMapValues(jentry, source, (K) keyPoJo);
                    keys.remove((K) keyPoJo);
                }
                    break;
                case ARRAY: {
                    JsonArray keyArray = jentry.getJsonArray("key");
                    assertTrue(keyArray.size() > 0);
                    verifyMapArrayKey(jentry, keyArray, source, keys);
                }
                    break;
                case STRING: {
                    String keyValue = jentry.getString("key");
                    verifyMapValues(jentry, source, (K) keyValue);
                    keys.remove((K) keyValue);
                }
                    break;
                case NUMBER: {
                    Number keyValue = jentry.getJsonNumber("key").numberValue();
                    verifyMapValues(jentry, source, (K) keyValue);
                    keys.remove((K) keyValue);
                }
                    break;
                case TRUE:
                case FALSE: {
                    Boolean keyValue = jentry.getBoolean("key");
                    verifyMapValues(jentry, source, (K) keyValue);
                    keys.remove((K) keyValue);
                }
                    break;
                default:
                    throw new IllegalStateException(jentry.getValue("/value").getValueType() + "was not expected");
            }
        });
        // Verify that all keys were processed.
        assertTrue(keys.isEmpty());
    }

//No longer valid test
//    /**
//     * Test serialization of Map with Number keys and String values.
//     */
//    @Test
//    public void testSerializeNumberStringMapToEntriesArray() {
//        Map<Number, String> map = new TreeMap<>(CMP_NUM);
//        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
//        map.put(Integer.valueOf(12), "twelve");
//        map.put(Short.valueOf((short)48), "forty eight");
//        map.put(Long.valueOf(256), "two hundred fifty-six");
//        String json = jsonb.toJson(map);
//        JsonArray jarr = Json.createReader(new StringReader(json)).read().asJsonArray();
//        verifySerialization(map, jarr);
//    }

    /**
     * Test serialization of Map with PoJo keys and PoJo values.
     */
    @Test
    public void testSerializePoJoPoJoMapToEntriesArray() {
        Map<Trainer, Pokemon> map = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        map.put(new Trainer("John Smith", 35), new Pokemon("Charmander", "fire", 980));
        map.put(new Trainer("Tom Jones", 24), new Pokemon("Caterpie", "bug", 437));
        map.put(new Trainer("Peter Wright", 27), new Pokemon("Houndour", "dark", 1234));
        map.put(new Trainer("Bob Parker", 19), new Pokemon("Sneasel", "ice", 2051));
        String json = jsonb.toJson(map);
        JsonArray jarr = Json.createReader(new StringReader(json)).read().asJsonArray();
        verifySerialization(map, jarr);
    }

    /**
     * Test serialization of Map with mixed simple keys and mixed simple values.
     */
    @Test
    public void testSerializeSimpleSimpleMapToEntriesArray() {
        String expected = "{\"false\":true,\"10\":24,\"Name\":\"John Smith\"}";
        Map<Object, Object> map = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        map.put("Name", "John Smith");
        map.put(Integer.valueOf(10), Long.valueOf(24l));
        map.put(Boolean.FALSE, Boolean.TRUE);
        String json = jsonb.toJson(map);
        assertEquals(expected, json);
    }

    /**
     * Test serialization of Map with arrays of simple values.
     */
    @Test
    public void testSerializeSimpleArraySimpleArrayMapToEntriesArray() {
        Map<Object, Object> map = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        map.put(new String[] {"John", "Smith"}, new String[] {"first name", "second name"});
        map.put(new String[] {"Pikachu", "electric"}, new String[] {"pokemon name", "pokemon type"});
        map.put(
                new Trainer[] {new Trainer("Bob", 15), new Trainer("Ash", 12)},
                new Pokemon[] {new Pokemon("Charmander", "fire", 1245), new Pokemon("Kyogre", "water", 3056)});
        String json = jsonb.toJson(map);
        JsonArray jarr = Json.createReader(new StringReader(json)).read().asJsonArray();
        verifySerialization(map, jarr);
    }

    /**
     * Test de-serialization of Map<?, ?> with various simple values as keys and
     * values. Map is stored as an JsonArray of map entries represented as
     * JsonObjects.
     */
    @Test
    public void testDeSerializePrimitivesMapToEntriesArray() {
        String jsonString = "[" +
            "    {" +
            "        \"key\": \"first\"," +
            "        \"value\": \"Peter Parker\""  +
            "    }," +
            "    {" +
            "        \"key\": 42," +
            "        \"value\": true" +
            "    }," +
            "    {" +
            "        \"key\": false," +
            "        \"value\": 21" +
            "    }" +
            "]";
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        Map<?, ?> map = jsonb.fromJson(jsonString, Map.class);
        assertEquals(3, map.size());
        // Make sure that all 3 pokemons were checked.
        int valueCheck = 0x00;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if ((entry.getKey() instanceof String) && "first".equals(entry.getKey())) {
                assertEquals("Peter Parker", entry.getValue());
                valueCheck |= 0x01;
            }
            if ((entry.getKey() instanceof Number) && entry.getKey().equals(new BigDecimal(42))) {
                assertEquals(true, entry.getValue());
                valueCheck |= 0x02;
            }
            if ((entry.getKey() instanceof Boolean) && entry.getKey().equals(false)) {
                assertEquals(new BigDecimal(21), entry.getValue());
                valueCheck |= 0x04;
            }
        }
        if ((valueCheck & 0x01) == 0) {
            fail("Did not find key \"first\" in the Map");
        }
        if ((valueCheck & 0x02) == 0) {
            fail("Did not find key 42 in the Map");
        }
        if ((valueCheck & 0x04) == 0) {
            fail("Did not find key false in the Map");
        }
    }

    /**
     * Test de-serialization of Map<String, Pokemon> with various classes instances
     * as keys. Map is stored as an JsonArray of map entries represented as
     * JsonObjects.
     */
    @Test
    public void testDeSerializeStringPoJoMapToEntriesArray() {
        String jsonString = "[" +
            "    {" +
            "        \"key\": \"Pikachu\"," +
            "        \"value\": {" +
            "            \"name\": \"Pikachu\"," +
            "            \"type\": \"electric\"," +
            "            \"cp\": 456" + "         }" +
            "    }," +
            "    {" +
            "        \"key\": \"Squirtle\"," +
            "        \"value\": {" +
            "            \"name\": \"Squirtle\"," +
            "            \"type\": \"water\"," +
            "            \"cp\": 124" +
            "         }" +
            "    }," +
            "    {" +
            "        \"key\": \"Rayquaza\"," +
            "        \"value\": {" +
            "            \"name\": \"Rayquaza\"," +
            "            \"type\": \"dragon\"," +
            "            \"cp\": 3273" +
            "        }" +
            "    }" +
            "]";
        ParameterizedType pt = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class, Pokemon.class };
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        Map<String, Pokemon> map = jsonb.fromJson(jsonString, pt);
        assertEquals(3, map.size());
        // Make sure that all 3 pokemons were checked.
        int valueCheck = 0x00;
        for (Map.Entry<String, Pokemon> entry : map.entrySet()) {
            Pokemon pokemon = entry.getValue();
            if ("Pikachu".equals(entry.getKey())) {
                assertEquals("Pikachu", pokemon.name);
                assertEquals("electric", pokemon.type);
                assertEquals(456, pokemon.cp);
                valueCheck |= 0x01;
            }
            if ("Squirtle".equals(entry.getKey())) {
                assertEquals("Squirtle", pokemon.name);
                assertEquals("water", pokemon.type);
                assertEquals(124, pokemon.cp);
                valueCheck |= 0x02;
            }
            if ("Rayquaza".equals(entry.getKey())) {
                assertEquals("Rayquaza", pokemon.name);
                assertEquals("dragon", pokemon.type);
                assertEquals(3273, pokemon.cp);
                valueCheck |= 0x04;
            }
        }
        if ((valueCheck & 0x01) == 0) {
            fail("Did not find key \"Pikachu\" in the Map");
        }
        if ((valueCheck & 0x02) == 0) {
            fail("Did not find key \"Squirtle\" in the Map");
        }
        if ((valueCheck & 0x04) == 0) {
            fail("Did not find key \"Rayquaza\" in the Map");
        }
    }

    /**
     * Test de-serialization of Map<Trainer, Pokemon> with various classes instances
     * as keys. Map is stored as an JsonArray of map entries represented as
     * JsonObjects.
     */
    @Test
    public void testDeSerializePoJoPoJoMapToEntriesArray() {
        String jsonString = "[" +
            "    {" +
            "        \"key\": {" +
            "            \"name\": \"Bob\"," +
            "            \"age\": 12" + "        }," +
            "        \"value\": {" +
            "            \"name\": \"Pikachu\"," +
            "            \"type\": \"electric\"," +
            "            \"cp\": 456" + "         }" +
            "    }," +
            "    {" +
            "        \"key\": {" +
            "            \"name\": \"Ash\"," +
            "            \"age\": 10" +
            "        }," +
            "        \"value\": {" +
            "            \"name\": \"Squirtle\"," +
            "            \"type\": \"water\"," +
            "            \"cp\": 124" +
            "         }" +
            "    }," +
            "    {" +
            "        \"key\": {" +
            "            \"name\": \"Joe\"," +
            "            \"age\": 15" +
            "        }," +
            "        \"value\": {" +
            "            \"name\": \"Rayquaza\"," +
            "            \"type\": \"dragon\"," +
            "            \"cp\": 3273" +
            "        }" +
            "    }" +
            "]";
        ParameterizedType pt = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { Trainer.class, Pokemon.class };
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        Map<Trainer, Pokemon> map = jsonb.fromJson(jsonString, pt);
        assertEquals(3, map.size());
        // Make sure that all 3 pokemons were checked.
        int valueCheck = 0x00;
        for (Map.Entry<Trainer, Pokemon> entry : map.entrySet()) {
            Trainer trainer = entry.getKey();
            Pokemon pokemon = entry.getValue();
            if ("Bob".equals(trainer.name)) {
                assertEquals(12, trainer.age);
                assertEquals("Pikachu", pokemon.name);
                assertEquals("electric", pokemon.type);
                assertEquals(456, pokemon.cp);
                valueCheck |= 0x01;
            }
            if ("Ash".equals(trainer.name)) {
                assertEquals(10, trainer.age);
                assertEquals("Squirtle", pokemon.name);
                assertEquals("water", pokemon.type);
                assertEquals(124, pokemon.cp);
                valueCheck |= 0x02;
            }
            if ("Joe".equals(trainer.name)) {
                assertEquals(15, trainer.age);
                assertEquals("Rayquaza", pokemon.name);
                assertEquals("dragon", pokemon.type);
                assertEquals(3273, pokemon.cp);
                valueCheck |= 0x04;
            }
        }
        if ((valueCheck & 0x01) == 0) {
            fail("Did not find key \"Bob\" in the Map");
        }
        if ((valueCheck & 0x02) == 0) {
            fail("Did not find key \"Ash\" in the Map");
        }
        if ((valueCheck & 0x04) == 0) {
            fail("Did not find key \"Joe\" in the Map");
        }
    }

    /**
     * Test de-serialization of Map<Integer[], String[]>.
     * Map is stored as an JsonArray of map entries represented as JsonObjects.
     */
    @Test
    public void testDeSerializeIntegerArrayStringArrayMapToEntriesArray() {
        String jsonString = "[" +
            "    {" +
            "        \"key\": [1,2]," +
            "        \"value\": [" +
            "            \"Bob\"," +
            "            \"Tom\"" +
            "        ]" +
            "    }," +
            "    {" +
            "        \"key\": [3,4]," +
            "        \"value\": [" +
            "            \"John\"," +
            "            \"Greg\"" +
            "        ]" +
            "    }" +
           "]";
        ParameterizedType pt = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { Integer[].class, String[].class };
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        Map<Integer[], String[]> map = jsonb.fromJson(jsonString, pt);
        assertEquals(2, map.size());
        // Make sure that all map entries were checked.
        int valueCheck = 0x00;
        for (Map.Entry<Integer[], String[]> entry : map.entrySet()) {
            Integer[] key = entry.getKey();
            String[] value = entry.getValue();
            if (key[0] == 1 && key[1] == 2) {
                assertEquals("Bob" ,value[0]);
                assertEquals("Tom" ,value[1]);
                valueCheck |= 0x01;
            }
            if (key[0] == 3 && key[1] == 4) {
                assertEquals("John" ,value[0]);
                assertEquals("Greg" ,value[1]);
                valueCheck |= 0x02;
            }
        }
        if ((valueCheck & 0x01) == 0) {
            fail("Did not find key [1,2] in the Map");
        }
        if ((valueCheck & 0x02) == 0) {
            fail("Did not find key [3,4] in the Map");
        }
    }

    /**
     * Test de-serialization of Map<Trainer[], Pokemon[]>.
     * Map is stored as an JsonArray of map entries represented as JsonObjects.
     */
    @Test
    public void testDeSerializePoJoArrayPoJoArrayMapToEntriesArray() {
        String jsonString = "[" +
                "    {" +
                "        \"key\": [" +
                "            {" +
                "                \"name\": \"Ash\"," +
                "                \"age\": 12" +
                "            },{" +
                "                \"name\": \"Joe\"," +
                "                \"age\": 14" +
                "            }" +
                "        ]," +
                "        \"value\": [" +
                "            {" +
                "                \"name\": \"Rayquaza\"," +
                "                \"type\": \"dragon\"," +
                "                \"cp\": 3273" +
                "            },{" +
                "                \"name\": \"Tyranitar\"," +
                "                \"type\": \"dark\"," +
                "                \"cp\": 3181" +
                "            }" +
                "        ]" +
                "    },{" +
                "        \"key\": [" +
                "            {" +
                "                \"name\": \"Bob\"," +
                "                \"age\": 13" +
                "            },{" +
                "                \"name\": \"Maggie\"," +
                "                \"age\": 15" +
                "            }" +
                "        ]," +
                "        \"value\": [" +
                "            {" +
                "                \"name\": \"Raikou\"," +
                "                \"type\": \"electric\"," +
                "                \"cp\": 3095" +
                "            },{" +
                "                \"name\": \"Mamoswine\"," +
                "                \"type\": \"ice\"," +
                "                \"cp\": 3055" +
                "            }" +
                "        ]" +
                "    }" +
                "]";
        ParameterizedType pt = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { Trainer[].class, Pokemon[].class };
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        Map<Trainer[], Pokemon[]> map = jsonb.fromJson(jsonString, pt);
        assertEquals(2, map.size());
        int valueCheck = 0x00;
        for (Map.Entry<Trainer[], Pokemon[]> entry : map.entrySet()) {
            Trainer[] key = entry.getKey();
            Pokemon[] value = entry.getValue();
            if (key[0].name.equals("Ash") && key[1].name.equals("Joe")) {
                assertEquals(12, key[0].age);
                assertEquals(14, key[1].age);
                assertEquals("Rayquaza", value[0].name);
                assertEquals("dragon", value[0].type);
                assertEquals(3273, value[0].cp);
                assertEquals("Tyranitar", value[1].name);
                assertEquals("dark", value[1].type);
                assertEquals(3181, value[1].cp);
                valueCheck |= 0x01;
            }
            if (key[0].name.equals("Bob") && key[1].name.equals("Maggie")) {
                assertEquals(13, key[0].age);
                assertEquals(15, key[1].age);
                assertEquals("Raikou", value[0].name);
                assertEquals("electric", value[0].type);
                assertEquals(3095, value[0].cp);
                assertEquals("Mamoswine", value[1].name);
                assertEquals("ice", value[1].type);
                assertEquals(3055, value[1].cp);
                valueCheck |= 0x02;
            }
        }
        if ((valueCheck & 0x01) == 0) {
            fail("Did not find key with \"Ash\" and \"Joe\" in the Map");
        }
        if ((valueCheck & 0x02) == 0) {
            fail("Did not find key with \"Bob\" and \"Maggie\" in the Map");
        }
    }

    public static class LocaleSerializer implements JsonbSerializer<Locale> {

        @Override
        public void serialize(Locale obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj.toLanguageTag());
        }
    }

    public static class LocaleDeserializer implements JsonbDeserializer<Locale> {

        @Override
        public Locale deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return Locale.forLanguageTag(parser.getString());
        }
    }

    public static class MapObject<K, V> {

        private Map<K, V> values;

        public MapObject() {
            this.values = new HashMap<>();
        }

        public Map<K, V> getValues() {
            return values;
        }

        public void setValues(Map<K, V> values) {
            if (values == null) {
                throw new IllegalArgumentException("values cannot be null");
            }
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MapObject) {
                MapObject<?,?> to = (MapObject<?,?>) o;
                return values.equals(to.values);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.values);
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    public static class MapObjectLocaleString extends MapObject<Locale, String> {};

    private void verifyMapObjectLocaleStringSerialization(JsonObject jsonObject, MapObjectLocaleString mapObject) {
        // Expected serialization is: {"values":[{"key":"lang-tag","value":"string"},...]}
        assertEquals(1, jsonObject.size());
        assertNotNull(jsonObject.get("values"));
        assertEquals(JsonValue.ValueType.ARRAY, jsonObject.get("values").getValueType());
        JsonArray jsonArray = jsonObject.getJsonArray("values");
        assertEquals(mapObject.getValues().size(), jsonArray.size());
        MapObjectLocaleString resObject = new MapObjectLocaleString();
        for (JsonValue jsonValue : jsonArray) {
            assertEquals(JsonValue.ValueType.OBJECT, jsonValue.getValueType());
            JsonObject entry = jsonValue.asJsonObject();
            assertEquals(2, entry.size());
            assertNotNull(entry.get("key"));
            assertEquals(JsonValue.ValueType.STRING, entry.get("key").getValueType());
            assertNotNull(entry.get("value"));
            assertEquals(JsonValue.ValueType.STRING, entry.get("value").getValueType());
            resObject.getValues().put(Locale.forLanguageTag(entry.getString("key")), entry.getString("value"));
        }
        assertEquals(mapObject, resObject);
    }

    /**
     * Test a Locale/String map with custom Locale serializer and deserializer.
     */
    @Test
    public void testMapLocaleString() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
                .withSerializers(new LocaleSerializer())
                .withDeserializers(new LocaleDeserializer()));

        MapObjectLocaleString mapObject = new MapObjectLocaleString();
        mapObject.getValues().put(Locale.US, "us");
        mapObject.getValues().put(Locale.ENGLISH, "en");
        mapObject.getValues().put(Locale.JAPAN, "jp");

        String json = jsonb.toJson(mapObject);
        JsonObject jsonObject = Json.createReader(new StringReader(json)).read().asJsonObject();
        verifyMapObjectLocaleStringSerialization(jsonObject, mapObject);

        MapObjectLocaleString resObject = jsonb.fromJson(json, MapObjectLocaleString.class);
        assertEquals(mapObject, resObject);
    }
}
