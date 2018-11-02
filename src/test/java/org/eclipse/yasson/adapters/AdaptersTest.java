/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.adapters;


import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.adapters.model.AdaptedPojo;
import org.eclipse.yasson.adapters.model.Author;
import org.eclipse.yasson.adapters.model.Box;
import org.eclipse.yasson.adapters.model.BoxToCrateCompatibleGenericsAdapter;
import org.eclipse.yasson.adapters.model.BoxToCratePropagatedIntegerStringAdapter;
import org.eclipse.yasson.adapters.model.Crate;
import org.eclipse.yasson.adapters.model.GenericBox;
import org.eclipse.yasson.adapters.model.IntegerListToStringAdapter;
import org.eclipse.yasson.adapters.model.JsonObjectPojo;
import org.eclipse.yasson.adapters.model.SupertypeAdapterPojo;
import org.eclipse.yasson.adapters.model.UUIDContainer;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Tests adapters to behave correctly.
 *
 * @author Roman Grigoriadi
 */
public class AdaptersTest {

    private Jsonb jsonb;

    public static class NonGenericPojo {
        public String strValues;
        public Box box;
    }

    @Test
    public void testBoxToCrateNoGenerics() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {
                new JsonbAdapter<Box, Crate>() {
                    @Override
                    public Crate adaptToJson(Box box) {
                        final Crate crate = new Crate();
                        crate.setCrateStrField("crateAdapted" + box.getBoxStrField());
                        crate.setCrateIntField(box.getBoxIntegerField() + 1);
                        return crate;
                    }

                    @Override
                    public Box adaptFromJson(Crate crate) {
                        Box box = new Box();
                        box.setBoxStrField("boxAdapted" + crate.getCrateStrField());
                        box.setBoxIntegerField(crate.getCrateIntField() + 1);
                        return box;
                    }
                }
        };
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo pojo = new AdaptedPojo();
        Box box = new Box();
        box.setBoxStrField("BoxStr");
        box.setBoxIntegerField(10);
        pojo.box = box;
        String json = jsonb.toJson(pojo);
        assertEquals("{\"box\":{\"crateIntField\":11,\"crateStrField\":\"crateAdaptedBoxStr\"}}", json);

        AdaptedPojo<?> result = jsonb.fromJson("{\"box\":{\"crateIntField\":10,\"crateStrField\":\"CrateStr\"}}", AdaptedPojo.class);
        assertEquals(Integer.valueOf(11), result.box.getBoxIntegerField());
        assertEquals("boxAdaptedCrateStr", result.box.getBoxStrField());
    }

    @Test
    public void testValueFieldAdapter() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {
                new JsonbAdapter<Integer, String>() {
                    @Override
                    public String adaptToJson(Integer integer) {
                        return String.valueOf(integer);
                    }

                    @Override
                    public Integer adaptFromJson(String s) {
                        return Integer.parseInt(s);
                    }
                }
        };
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo pojo = new AdaptedPojo();
        pojo.intField = 11;
        String json = jsonb.toJson(pojo);
        assertEquals("{\"intField\":\"11\"}", json);

        AdaptedPojo<?> result = jsonb.fromJson("{\"intField\":\"10\"}", AdaptedPojo.class);
        assertEquals(Integer.valueOf(10), result.intField);
    }

    @Test
    public void testGenericAdapter() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new BoxToCrateCompatibleGenericsAdapter<Integer>() {
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<Integer> pojo = new AdaptedPojo<>();
        pojo.strField = "POJO_STRING";
        pojo.intBox = new GenericBox<>("INT_BOX_STR", 11);
        pojo.tBox = new GenericBox<>("T_BOX_STR", 110);

        String marshalledJson = jsonb.toJson(pojo, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("{\"intBox\":{\"adaptedT\":11,\"crateStrField\":\"INT_BOX_STR\"}," +
                "\"strField\":\"POJO_STRING\"," +
                "\"tBox\":{\"adaptedT\":110,\"crateStrField\":\"T_BOX_STR\"}}", marshalledJson);

        String toUnmarshall = "{\"intBox\":{\"crateStrField\":\"Box3\",\"adaptedT\":33}," +
                "\"tBox\":{\"crateStrField\":\"tGenBoxCrateStr\",\"adaptedT\":22}," +
                "\"strField\":\"POJO_STRING\"," +
                "\"strBox\":{\"strField\":\"strBoxStr\",\"x\":\"44\"}}";
        AdaptedPojo result = jsonb.fromJson(toUnmarshall, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("POJO_STRING", result.strField);
        assertEquals("Box3", result.intBox.getStrField());
        assertEquals(33, result.intBox.getX());
        assertEquals("tGenBoxCrateStr", result.tBox.getStrField());
        assertEquals(22, result.tBox.getX());
        assertEquals("strBoxStr", result.strBox.getStrField());
        assertEquals("44", result.strBox.getX());

    }

    @Test
    public void testPropagatedTypeArgs() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new BoxToCratePropagatedIntegerStringAdapter()};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<Integer> pojo = new AdaptedPojo<>();
        pojo.intBox = new GenericBox<>("INT_BOX_STR", 110);
        pojo.tBox = new GenericBox<>("T_BOX_STR", 111);
        pojo.strBox = new GenericBox<>("STR_BOX_STR", "101");

        String marshalledJson = jsonb.toJson(pojo, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("{\"intBox\":{\"adaptedT\":{\"x\":[\"110\"]},\"crateStrField\":\"INT_BOX_STR\"}," +
                        "\"strBox\":{\"strField\":\"STR_BOX_STR\",\"x\":\"101\"}," +
                        "\"tBox\":{\"adaptedT\":{\"x\":[\"111\"]},\"crateStrField\":\"T_BOX_STR\"}}",
                marshalledJson);

        String toUnmarshall = "{\"intBox\":{\"crateStrField\":\"strCrateStr\",\"adaptedT\":{\"strField\":\"crateBoxStrField\",\"x\":[\"77\"]}}," +
                "\"tBox\":{\"crateStrField\":\"tStrCrateStr\",\"adaptedT\":{\"strField\":\"crateBoxStrField\",\"x\":[\"88\"]}}," +
                "\"strField\":\"POJO_STRING\"," +
                "\"strBox\":{\"strField\":\"strBoxStr\",\"x\":\"44\"}}";

        AdaptedPojo result = jsonb.fromJson(toUnmarshall, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("POJO_STRING", result.strField);
        assertEquals("strCrateStr", result.intBox.getStrField());
        assertEquals(77, result.intBox.getX());
        assertEquals("tStrCrateStr", result.tBox.getStrField());
        assertEquals(88, result.tBox.getX());
        assertEquals("strBoxStr", result.strBox.getStrField());
        assertEquals("44", result.strBox.getX());
    }

    @Test
    public void testStringToGenericCollectionAdapter() throws Exception {

        JsonbAdapter<?, ?>[] adapters = {new IntegerListToStringAdapter()};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<List<Integer>> pojo = new AdaptedPojo<>();
        pojo.tVar = Arrays.asList(11, 22, 33);
        pojo.integerList = Arrays.asList(110, 111, 101);
        String marshalledJson = jsonb.toJson(pojo, new TestTypeToken<AdaptedPojo<List<Integer>>>(){}.getType());
        assertEquals("{\"integerList\":\"110#111#101\"," +
                "\"tVar\":\"11#22#33\"}", marshalledJson);

        String toUnmarshall = "{\"integerList\":\"11#22#33#44\",\"stringList\":[\"first\",\"second\"]," +
                "\"tVar\":\"110#111#101\"}";

        AdaptedPojo result = jsonb.fromJson(toUnmarshall, new TestTypeToken<AdaptedPojo<List<Integer>>>(){}.getType());
        List<Integer> expectedIntegerList = Arrays.asList(11, 22, 33, 44);
        List<String> expectedStringList = Arrays.asList("first", "second");
        List<Integer> expectedTList = Arrays.asList(110, 111, 101);

        assertEquals(expectedIntegerList, result.integerList);
        assertEquals(expectedStringList, result.stringList);
        assertEquals(expectedTList, result.tVar);
    }

    @Test
    public void testAdaptObjectInCollection() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new BoxToCrateCompatibleGenericsAdapter<Integer>() {
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<Integer> pojo = new AdaptedPojo<>();

        pojo.tGenericBoxList = new ArrayList<>();
        pojo.tGenericBoxList.add(new GenericBox<>("GEN_BOX_STR_1", 110));
        pojo.tGenericBoxList.add(new GenericBox<>("GEN_BOX_STR_2", 101));

        String marshalledJson = jsonb.toJson(pojo, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("{\"tGenericBoxList\":[{\"adaptedT\":110,\"crateStrField\":\"GEN_BOX_STR_1\"},{\"adaptedT\":101,\"crateStrField\":\"GEN_BOX_STR_2\"}]}", marshalledJson);

        String toUnmarshall = "{\"integerList\":[11,22,33,44],\"stringList\":[\"first\",\"second\"]," +
                "\"tGenericBoxList\":[{\"crateStrField\":\"FirstCrate\",\"adaptedT\":11},{\"crateStrField\":\"SecondCrate\",\"adaptedT\":22}]}";

        AdaptedPojo<Integer> result = jsonb.fromJson(toUnmarshall, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("FirstCrate", result.tGenericBoxList.get(0).getStrField());
        assertEquals("SecondCrate", result.tGenericBoxList.get(1).getStrField());
        assertEquals(Integer.valueOf(11), result.tGenericBoxList.get(0).getX());
        assertEquals(Integer.valueOf(22), result.tGenericBoxList.get(1).getX());
    }

    @Test
    public void testAdaptTypeIntoCollection() throws Exception {

        JsonbAdapter<?, ?>[] adapters = {new JsonbAdapter<String, List<Integer>>() {
            @Override
            public List<Integer> adaptToJson(String s) {
                List<Integer> result = new ArrayList<>();
                for (String str : s.split(",")) {
                    result.add(Integer.parseInt(str));
                }
                return result;
            }

            @Override
            public String adaptFromJson(List<Integer> ints) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : ints) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(",");
                    }
                    sb.append(i);
                }
                return sb.toString();
            }
        }
        };
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        String json = "{\"strValues\":[11,22,33]}";
        final NonGenericPojo object = new NonGenericPojo();
        object.strValues = "11,22,33";
        assertEquals(json, jsonb.toJson(object));
        NonGenericPojo pojo = jsonb.fromJson(json, NonGenericPojo.class);
        assertEquals("11,22,33", pojo.strValues);
    }

    @Test
    public void testMarshallGenericField() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new BoxToCratePropagatedIntegerStringAdapter()};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<Integer> adaptedPojo = new AdaptedPojo<>();
        adaptedPojo.tBox = new GenericBox<>("tGenBoxStrField", 22);
        adaptedPojo.intBox = new GenericBox<>("genBoxStrField", 11);
        String json = jsonb.toJson(adaptedPojo, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("{\"intBox\":{\"adaptedT\":{\"x\":[\"11\"]},\"crateStrField\":\"genBoxStrField\"},\"tBox\":{\"adaptedT\":{\"x\":[\"22\"]},\"crateStrField\":\"tGenBoxStrField\"}}", json);

        AdaptedPojo<Integer> unmarshalledAdaptedPojo = jsonb.fromJson(json, new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals("genBoxStrField", unmarshalledAdaptedPojo.intBox.getStrField());
        assertEquals(Integer.valueOf(11), unmarshalledAdaptedPojo.intBox.getX());
    }

    @Test
    public void testTypeVariable() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new JsonbAdapter<List<GenericBox<Double>>, BigDecimal>() {
            @Override
            public BigDecimal adaptToJson(List<GenericBox<Double>> genericBoxes) {
                return BigDecimal.valueOf(genericBoxes.get(0).getX());
            }

            @Override
            public List<GenericBox<Double>> adaptFromJson(BigDecimal bigDecimal) {
                List<GenericBox<Double>> list = new ArrayList<>();
                list.add(new GenericBox<>("", bigDecimal.doubleValue()));
                return list;
            }
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<List<GenericBox<Double>>> intBoxPojo = new AdaptedPojo<>();
        List<GenericBox<Double>> intBoxList = new ArrayList<>();
        intBoxList.add(new GenericBox<>("", 11d));
        intBoxPojo.tVar = intBoxList;

        String json = jsonb.toJson(intBoxPojo, new TestTypeToken<AdaptedPojo<List<GenericBox<Double>>>>(){}.getType());
        assertEquals("{\"tVar\":11.0}", json);

        AdaptedPojo<List<GenericBox<Double>>> result = jsonb.fromJson(json, new TestTypeToken<AdaptedPojo<List<GenericBox<Double>>>>(){}.getType());
        assertEquals(Double.valueOf(11), result.tVar.get(0).getX());
    }

    @Test
    public void testAdaptRoot() throws Exception {

        JsonbAdapter<?, ?>[] adapters = {new JsonbAdapter<Box, Crate>() {
            @Override
            public Crate adaptToJson(Box box) {
                return new Crate(box.getBoxStrField(), box.getBoxIntegerField());
            }

            @Override
            public Box adaptFromJson(Crate crate) {
                return new Box(crate.getCrateStrField(), crate.getCrateIntField());
            }
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        Box pojo = new Box("BOX_STR", 101);
        String marshalledJson = jsonb.toJson(pojo);
        assertEquals("{\"crateIntField\":101,\"crateStrField\":\"BOX_STR\"}", marshalledJson);

        Box result = jsonb.fromJson("{\"crateIntField\":110,\"crateStrField\":\"CRATE_STR\"}", Box.class);
        assertEquals("CRATE_STR", result.getBoxStrField());
        assertEquals(Integer.valueOf(110), result.getBoxIntegerField());
    }

    @Test
    public void testAdaptMapString() throws Exception {

        JsonbAdapter<?, ?>[] adapters = {new JsonbAdapter<Map<String, Integer>, String>() {
            @Override
            public Map<String, Integer> adaptFromJson(String obj) throws Exception {
                final HashMap<String, Integer> result = new HashMap<>();
                result.put("fake", 101);
                return result;
            }

            @Override
            public String adaptToJson(Map<String, Integer> obj) throws Exception {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Integer> entry : obj.entrySet()) {
                    if (sb.length() > 0) {
                        sb.append("#");
                    }
                    sb.append(entry.getKey()).append("-").append(entry.getValue());
                }
                return sb.toString();
            }
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<Integer> pojo = new AdaptedPojo<>();
        pojo.stringIntegerMap = new HashMap<>();
        pojo.stringIntegerMap.put("first", 11);
        pojo.stringIntegerMap.put("second", 22);
        pojo.tMap = new HashMap<>(pojo.stringIntegerMap);
        String marshalledJson = jsonb.toJson(pojo, new AdaptedPojo<Integer>(){}.getClass());
        assertEquals("{\"stringIntegerMap\":\"first-11#second-22\",\"tMap\":\"first-11#second-22\"}", marshalledJson);

        AdaptedPojo<Integer> result = jsonb.fromJson("{\"stringIntegerMap\":\"fake-value\",\"tMap\":\"fake-value\"}", new TestTypeToken<AdaptedPojo<Integer>>(){}.getType());
        assertEquals(Integer.valueOf(101), result.stringIntegerMap.get("fake"));
        assertEquals(Integer.valueOf(101), result.tMap.get("fake"));
    }

    @Test
    public void testAdaptMapToObject() throws Exception {
        JsonbAdapter<?, ?>[] adapters = {new JsonbAdapter<Map<String, String>, Crate>() {
            @Override
            public Map<String, String> adaptFromJson(Crate obj) throws Exception {
                final HashMap<String, String> fake = new HashMap<>();
                fake.put("fake", "11");
                return fake;
            }

            @Override
            public Crate adaptToJson(Map<String, String> obj) throws Exception {
                final Map.Entry<String, String> next = obj.entrySet().iterator().next();
                return new Crate(next.getKey(), Integer.parseInt(next.getValue()));
            }
        }};
        jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.ADAPTERS, adapters));

        AdaptedPojo<String> pojo = new AdaptedPojo<>();
        pojo.tMap = new HashMap<>();
        pojo.tMap.put("first", "101");

        TestTypeToken<AdaptedPojo<String>> typeToken = new TestTypeToken<AdaptedPojo<String>>() {};

        String marshalledJson = jsonb.toJson(pojo, typeToken.getType());
        assertEquals("{\"tMap\":{\"crateIntField\":101,\"crateStrField\":\"first\"}}", marshalledJson);

        AdaptedPojo<String> result = jsonb.fromJson("{\"tMap\":{\"crateIntField\":101,\"crateStrField\":\"first\"}}", typeToken.getType());
        assertEquals("11", result.tMap.get("fake"));
    }

    @Test
    public void testAdaptJsonObject() {
        jsonb = JsonbBuilder.create();
        JsonObjectPojo pojo = new JsonObjectPojo();
        pojo.box = new Box("strFieldValue", 110);

        String json = jsonb.toJson(pojo);
        assertEquals("{\"box\":{\"boxStrField\":\"strFieldValue\",\"boxIntegerField\":110}}", json);

        JsonObjectPojo result = jsonb.fromJson(json, JsonObjectPojo.class);
        assertEquals("strFieldValue", result.box.getBoxStrField());
        assertEquals(Integer.valueOf(110), result.box.getBoxIntegerField());
    }

    @Test
    public void testAdaptAuthor() {
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Connor");

        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(author);
        Assert.assertEquals("{\"firstName\":\"J\",\"lastName\":\"Connor\"}", json);

        Author result = jsonb.fromJson("{\"firstName\":\"J\",\"lastName\":\"Connor\"}", Author.class);
        Assert.assertEquals("\"J\"", result.getFirstName());
        Assert.assertEquals("Connor", result.getLastName());
    }

    @Test
    public void testAdaptUUID() {
        jsonb = JsonbBuilder.create();
        UUIDContainer pojo = new UUIDContainer();
        UUID uuid = UUID.fromString("b329da91-0d96-44b6-b466-56c2458b2877");
        pojo.setUuidClsBased(uuid);
        pojo.setUuidIfcBased(uuid);

        String result = jsonb.toJson(pojo);
        Assert.assertEquals("{\"uuidClsBased\":\"b329da91-0d96-44b6-b466-56c2458b2877\",\"uuidIfcBased\":\"b329da91-0d96-44b6-b466-56c2458b2877\"}", result);

        UUIDContainer uuidContainer = jsonb.fromJson(result, UUIDContainer.class);
        Assert.assertEquals(uuid, uuidContainer.getUuidClsBased());
        Assert.assertEquals(uuid, uuidContainer.getUuidIfcBased());
    }

    @Test
    public void testSupertypeAdapter() {
        jsonb = JsonbBuilder.create();
        SupertypeAdapterPojo pojo = new SupertypeAdapterPojo();
        pojo.setNumberInteger(10);
        pojo.setSerializableInteger(11);
        Assert.assertEquals("{\"numberInteger\":\"11\",\"serializableInteger\":12}", jsonb.toJson(pojo));
        pojo = jsonb.fromJson("{\"numberInteger\":\"11\",\"serializableInteger\":12}", SupertypeAdapterPojo.class);
        Assert.assertEquals(Integer.valueOf(10), pojo.getNumberInteger());
        Assert.assertEquals(Integer.valueOf(11), pojo.getSerializableInteger());
    }
}
