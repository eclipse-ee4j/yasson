/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Tomas Kraus
 ******************************************************************************/
package org.eclipse.yasson.serializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.eclipse.yasson.serializers.model.Pokemon;
import org.eclipse.yasson.serializers.model.Trainer;
import org.junit.Test;

/**
 * Test various use-cases with {@code Map} serializer and de-serializer which
 * stores Map.Entries 1:1 as JSON objects.
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
                if (
                    ((n1 instanceof Byte) || (n1 instanceof Short) || (n1 instanceof Integer) || (n1 instanceof Long))
                    && ((n2 instanceof Byte) || (n2 instanceof Short) || (n2 instanceof Integer) || (n2 instanceof Long))
                ) {
                    return Long.compare(n1.longValue(), n2.longValue());
                }
                if (
                    ((n1 instanceof Float) || (n1 instanceof Double))
                    && ((n2 instanceof Float) || (n2 instanceof Double))
                ) {
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
     * Verify that serialized {@code Map} provided as {@code JsonArray} matches source {@code Map}.
     *
     * @param source source {@code Map}
     * @param array  serialized {@code Map} parsed and provided as {@code JsonArray}
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
    }


    /**
     * Test serialization of Map with Number keys and String values.
     */
    @Test
    public void testSerializeNumberStringMapToEntriesArray() {
        Map<Number, String> map = new TreeMap<>(CMP_NUM);
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        map.put(Integer.valueOf(12), "twelve");
        map.put(Short.valueOf((short)48), "forty eight");
        map.put(Long.valueOf(256), "two hundred fifty-six");
        String json = jsonb.toJson(map);
        JsonArray jarr = Json.createReader(new StringReader(json)).read().asJsonArray();
        verifySerialization(map, jarr);
    }

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
        Map<Object, Object> map = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        map.put("Name", "John Smith");
        map.put(Integer.valueOf(10), Long.valueOf(24l));
        map.put(Boolean.FALSE, Boolean.TRUE);
        String json = jsonb.toJson(map);
        JsonArray jarr = Json.createReader(new StringReader(json)).read().asJsonArray();
        verifySerialization(map, jarr);
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
        System.out.println(json);
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
            if ((entry.getKey() instanceof String) && "first".equals((String) entry.getKey())) {
                assertEquals("Peter Parker", entry.getValue());
                valueCheck |= 0x01;
            }
            if ((entry.getKey() instanceof Number) && ((Number) entry.getKey()).equals(new BigDecimal(42))) {
                assertEquals(true, entry.getValue());
                valueCheck |= 0x02;
            }
            if ((entry.getKey() instanceof Boolean) && ((Boolean) entry.getKey()).equals(false)) {
                assertEquals(new BigDecimal(21), entry.getValue());
                valueCheck |= 0x04;
            }
        }
        assertEquals("Some of Map keys did not match expected values", 0x07, valueCheck);
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
        assertEquals("Some of Map keys did not match expected values", 0x07, valueCheck);
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
        assertEquals("Some of Map keys did not match expected values", 0x07, valueCheck);
    }

}
