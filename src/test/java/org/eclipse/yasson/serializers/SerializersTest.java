/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Sebastien Rius
 ******************************************************************************/

package org.eclipse.yasson.serializers;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import static java.util.Collections.singletonMap;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.internal.model.ReverseTreeMap;
import org.eclipse.yasson.serializers.model.AnnotatedGenericWithSerializerType;
import org.eclipse.yasson.serializers.model.AnnotatedWithSerializerType;
import org.eclipse.yasson.serializers.model.Author;
import org.eclipse.yasson.serializers.model.Box;
import org.eclipse.yasson.serializers.model.BoxWithAnnotations;
import org.eclipse.yasson.serializers.model.Crate;
import org.eclipse.yasson.serializers.model.CrateDeserializer;
import org.eclipse.yasson.serializers.model.CrateDeserializerWithConversion;
import org.eclipse.yasson.serializers.model.CrateInner;
import org.eclipse.yasson.serializers.model.CrateJsonObjectDeserializer;
import org.eclipse.yasson.serializers.model.CrateSerializer;
import org.eclipse.yasson.serializers.model.CrateSerializerWithConversion;
import org.eclipse.yasson.serializers.model.GenericPropertyPojo;
import org.eclipse.yasson.serializers.model.NumberDeserializer;
import org.eclipse.yasson.serializers.model.NumberSerializer;
import org.eclipse.yasson.serializers.model.RecursiveDeserializer;
import org.eclipse.yasson.serializers.model.RecursiveSerializer;
import org.eclipse.yasson.serializers.model.SimpleAnnotatedSerializedArrayContainer;
import org.eclipse.yasson.serializers.model.SimpleContainer;
import org.eclipse.yasson.serializers.model.StringWrapper;
import org.eclipse.yasson.serializers.model.SupertypeSerializerPojo;

/**
 * @author Roman Grigoriadi
 */
public class SerializersTest {

    @Test
    public void testClassLevelAnnotation() {
        Crate crate = new Crate();
        crate.crateBigDec = BigDecimal.TEN;
        crate.crateStr = "crateStr";

        crate.annotatedType = new AnnotatedWithSerializerType();
        crate.annotatedType.value = "abc";
        crate.annotatedGenericType = new AnnotatedGenericWithSerializerType<>();
        crate.annotatedGenericType.value = new Crate();
        crate.annotatedGenericType.value.crateStr = "inside generic";
        crate.annotatedTypeOverriddenOnProperty = new AnnotatedWithSerializerType();
        crate.annotatedTypeOverriddenOnProperty.value = "def";
        String expected = "{\"annotatedGenericType\":{\"generic\":{\"crate_str\":\"inside generic\"}},\"annotatedType\":{\"valueField\":\"replaced value\"},\"annotatedTypeOverriddenOnProperty\":{\"valueField\":\"overridden value\"},\"crateBigDec\":10,\"crate_str\":\"crateStr\"}";

        assertEquals(expected, defaultJsonb.toJson(crate));

        Crate result = defaultJsonb.fromJson(expected, Crate.class);
        assertEquals("replaced value", result.annotatedType.value);
        assertEquals("overridden value", result.annotatedTypeOverriddenOnProperty.value);
        assertEquals("inside generic", result.annotatedGenericType.value.crateStr);
    }

    @Test
    public void testClassLevelAnnotationOnRoot() {
        AnnotatedWithSerializerType annotatedType = new AnnotatedWithSerializerType();
        annotatedType.value = "abc";
        String expected = "{\"valueField\":\"replaced value\"}";

        assertEquals(expected, defaultJsonb.toJson(annotatedType));

        AnnotatedWithSerializerType result = defaultJsonb.fromJson(expected, AnnotatedWithSerializerType.class);
        assertEquals("replaced value", result.value);
    }

    @Test
    public void testClassLevelAnnotationOnGenericRoot() {
        AnnotatedGenericWithSerializerType<Crate> annotatedType = new AnnotatedGenericWithSerializerType<>();
        annotatedType.value = new Crate();
        annotatedType.value.crateStr = "inside generic";
        String expected = "{\"generic\":{\"crate_str\":\"inside generic\"}}";

        assertEquals(expected, defaultJsonb.toJson(annotatedType));

        AnnotatedGenericWithSerializerType<Crate> result = defaultJsonb.fromJson(expected, new AnnotatedGenericWithSerializerType<Crate>(){}.getClass().getGenericSuperclass());
        assertEquals("inside generic", result.value.crateStr);
    }

    /**
     * Tests JSONB deserialization of arbitrary type invoked from a Deserializer.
     */
    @Test
    public void testDeserializerDeserializationByType() {
        JsonbConfig config = new JsonbConfig().withDeserializers(new CrateDeserializer());
        Jsonb jsonb = JsonbBuilder.create(config);

        Box box = createPojoWithDates();

        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"date\":\"2015-05-14T11:10:01\"},\"secondBoxStr\":\"Second box string\"}";

        Box result = jsonb.fromJson(expected, Box.class);

        //deserialized by deserializationContext.deserialize(Class c)
        assertEquals(box.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
        assertEquals(box.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);

        assertEquals("List inner 0", result.crate.crateInnerList.get(0).crateInnerStr);
        assertEquals("List inner 1", result.crate.crateInnerList.get(1).crateInnerStr);

        //set by deserializer statically
        assertEquals(new BigDecimal("123"), result.crate.crateBigDec);
        assertEquals("abc", result.crate.crateStr);
    }

    /**
     * Tests JSONB serialization of arbitrary type invoked from a Serializer.
     */
    @Test
    public void testSerializerSerializationOfType() {
        JsonbConfig config = new JsonbConfig().withSerializers(new CrateSerializer());
        Jsonb jsonb = JsonbBuilder.create(config);
        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
        Box pojo = createPojo();

        assertEquals(expected, jsonb.toJson(pojo));

        Box result = jsonb.fromJson(expected, Box.class);
        assertEquals(new BigDecimal("54321"), result.crate.crateBigDec);
        //result.crate.crateStr is mapped to crate_str by jsonb property
        assertNull(result.crate.crateStr);
        assertEquals(pojo.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);
        assertEquals(pojo.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
    }

    /**
     * Tests jsonb type conversion, including property customization.
     */
    @Test
    public void testDeserializersUsingConversion() {
        JsonbConfig config = new JsonbConfig().withDeserializers(new CrateDeserializerWithConversion());
        Jsonb jsonb = JsonbBuilder.create(config);

        String json = "{\"boxStr\":\"Box string\",\"crate\":{\"date-converted\":\"2015-05-14T11:10:01\",\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
        Box result = jsonb.fromJson(json, Box.class);
        final Date expected = getExpectedDate();
        assertEquals(expected, result.crate.date);
        assertEquals("Box string", result.boxStr);
        assertEquals("Second box string", result.secondBoxStr);
    }

    @Test
    public void testCrateJsonObjectDeserializer() {
        JsonbConfig config = new JsonbConfig().withDeserializers(new CrateJsonObjectDeserializer());
        Jsonb jsonb = JsonbBuilder.create(config);
        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"date-converted\":\"2015-05-14T11:10:01\",\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crateInnerStr\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
        Box result = jsonb.fromJson(expected, Box.class);
        assertEquals(new BigDecimal("54321"), result.crate.crateBigDec);
        assertEquals("REPLACED crate str", result.crate.crateStr);
        assertEquals("Single inner", result.crate.crateInner.crateInnerStr);
        assertEquals(BigDecimal.TEN, result.crate.crateInner.crateInnerBigDec);
    }

    private static Date getExpectedDate() {
        return new Calendar.Builder().setDate(2015, 4, 14).setTimeOfDay(11, 10, 1).setTimeZone(TimeZone.getTimeZone("Z")).build().getTime();
    }

    @Test
    public void testSerializationUsingConversion() {
        JsonbConfig config = new JsonbConfig().withSerializers(new CrateSerializerWithConversion());
        Jsonb jsonb = JsonbBuilder.create(config);

        String json = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01Z[UTC]\"},\"secondBoxStr\":\"Second box string\"}";
        assertEquals(json, jsonb.toJson(createPojoWithDates()));
    }

    @Test
    public void testAnnotations() {
        BoxWithAnnotations box = new BoxWithAnnotations();
        box.boxStr = "Box string";
        box.secondBoxStr = "Second box string";
        box.crate = new Crate();
        box.crate.date = getExpectedDate();
        box.crate.crateInner = createCrateInner("Single inner");

        box.crate.crateInnerList = new ArrayList<>();
        box.crate.crateInnerList.add(createCrateInner("List inner 0"));
        box.crate.crateInnerList.add(createCrateInner("List inner 1"));

        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01Z[UTC]\"},\"secondBoxStr\":\"Second box string\"}";

        assertEquals(expected, defaultJsonb.toJson(box));

        BoxWithAnnotations result = defaultJsonb.fromJson(expected, BoxWithAnnotations.class);

        //deserialized by deserializationContext.deserialize(Class c)
        assertEquals(box.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
        assertEquals(box.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);

        assertEquals(2L, result.crate.crateInnerList.size());
        assertEquals("List inner 0", result.crate.crateInnerList.get(0).crateInnerStr);
        assertEquals("List inner 1", result.crate.crateInnerList.get(1).crateInnerStr);

        //set by deserializer statically
        assertEquals(new BigDecimal("123"), result.crate.crateBigDec);
        assertEquals("abc", result.crate.crateStr);
    }

    @Test
    public void testAnnotationsOverride() {
        JsonbConfig config = new JsonbConfig().withDeserializers(new CrateJsonObjectDeserializer()).withSerializers(new CrateSerializer());
        Jsonb jsonb = JsonbBuilder.create(config);

        BoxWithAnnotations box = new BoxWithAnnotations();
        box.boxStr = "Box string";
        box.secondBoxStr = "Second box string";
        box.crate = new Crate();
        box.crate.crateInner = createCrateInner("Single inner");
        box.crate.date = getExpectedDate();

        box.crate.crateInnerList = new ArrayList<>();
        box.crate.crateInnerList.add(createCrateInner("List inner 0"));
        box.crate.crateInnerList.add(createCrateInner("List inner 1"));

        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01Z[UTC]\"},\"secondBoxStr\":\"Second box string\"}";

        assertEquals(expected, jsonb.toJson(box));

        BoxWithAnnotations result = jsonb.fromJson(expected, BoxWithAnnotations.class);

        //deserialized by deserializationContext.deserialize(Class c)
        assertEquals(box.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
        assertEquals(box.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);

        assertEquals(2L, result.crate.crateInnerList.size());
        assertEquals("List inner 0", result.crate.crateInnerList.get(0).crateInnerStr);
        assertEquals("List inner 1", result.crate.crateInnerList.get(1).crateInnerStr);

        //set by deserializer statically
        assertEquals(new BigDecimal("123"), result.crate.crateBigDec);
        assertEquals("abc", result.crate.crateStr);
    }

    @Test
    public void testStringField() {
        StringWrapper pojo = new StringWrapper();
        pojo.strField = "abc";
        final String result = defaultJsonb.toJson(pojo);
        assertEquals("{\"strField\":\"   abc\"}", result);
    }

    @Test
    public void testContainerSerializer() {
        SimpleAnnotatedSerializedArrayContainer container = new SimpleAnnotatedSerializedArrayContainer();
        SimpleContainer instance1 = new SimpleContainer();
        instance1.setInstance("Test String 1");
        SimpleContainer instance2 = new SimpleContainer();
        instance2.setInstance("Test String 2");
        container.setArrayInstance(new SimpleContainer[] {instance1, instance2});

        container.setListInstance(new ArrayList<>());
        container.getListInstance().add(new SimpleContainer("Test List 1"));
        container.getListInstance().add(new SimpleContainer("Test List 2"));

        String jsonString = defaultJsonb.toJson(container);
        assertEquals("{\"arrayInstance\":[{\"instance\":\"Test String 1\"},{\"instance\":\"Test String 2\"}],\"listInstance\":[{\"instance\":\"Test List 1\"},{\"instance\":\"Test List 2\"}]}", jsonString);

        SimpleAnnotatedSerializedArrayContainer unmarshalledObject = defaultJsonb.fromJson("{\"arrayInstance\":[{\"instance\":\"Test String 1\"},{\"instance\":\"Test String 2\"}],\"listInstance\":[{\"instance\":\"Test List 1\"},{\"instance\":\"Test List 2\"}]}", SimpleAnnotatedSerializedArrayContainer.class);

        assertEquals("Test String 1", unmarshalledObject.getArrayInstance()[0].getInstance());
        assertEquals("Test String 2", unmarshalledObject.getArrayInstance()[1].getInstance());

        assertEquals("Test List 1", unmarshalledObject.getListInstance().get(0).getInstance());
        assertEquals("Test List 2", unmarshalledObject.getListInstance().get(1).getInstance());
    }

    /**
     * Tests avoiding StackOverflowError.
     */
    @Test
    public void testRecursiveSerializer() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withSerializers(new RecursiveSerializer()).withDeserializers(new RecursiveDeserializer()));

        Box box = new Box();
        box.boxStr = "Box to serialize";
        try {
            jsonb.toJson(box);
            fail();
        } catch (JsonbException ex) {
        }

        try {
            jsonb.fromJson("{\"boxStr\":\"Box to deserialize\"}", Box.class);
            fail();
        } catch (StackOverflowError error){
        }
    }

    @Test
    public void testAuthor() {
        Author author = new Author("Sarah", "Connor");
        String expected = "{\"firstName\":\"S\",\"lastName\":\"Connor\"}";
        String json = defaultJsonb.toJson(author);

        assertEquals(expected, json);

        Author result = defaultJsonb.fromJson(expected, Author.class);
        assertEquals("John", result.getFirstName());
        assertEquals("Connor", result.getLastName());
    }

    @Test
    public void testSupertypeSerializer() {
        Jsonb jsonb = JsonbBuilder.create(
                new JsonbConfig().withSerializers(new NumberSerializer())
                        .withDeserializers(new NumberDeserializer()));
        SupertypeSerializerPojo pojo = new SupertypeSerializerPojo();
        pojo.setNumberInteger(10);
        pojo.setAnotherNumberInteger(11);
        assertEquals("{\"anotherNumberInteger\":\"12\",\"numberInteger\":\"11\"}", jsonb.toJson(pojo));

        pojo = jsonb.fromJson("{\"anotherNumberInteger\":\"12\",\"numberInteger\":\"11\"}", SupertypeSerializerPojo.class);
        assertEquals(Integer.valueOf(10), pojo.getNumberInteger());
        assertEquals(Integer.valueOf(11), pojo.getAnotherNumberInteger());
    }
    
    @Test
    public void testObjectDerializerWithLexOrderStrategy() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        Object pojo = jsonb.fromJson("{\"first\":{},\"third\":{},\"second\":{\"second\":2,\"first\":1}}", Object.class);
        assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap");
        @SuppressWarnings("unchecked")
        SortedMap<String, Object> pojoAsMap = (SortedMap<String, Object>) pojo;
        assertTrue(pojoAsMap.get("second") instanceof TreeMap, "Pojo inner object is not of type TreeMap");
        assertEquals("{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}", jsonb.toJson(pojo));
    }
    
    @Test
    public void testObjectDerializerWithReverseOrderStrategy() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        Object pojo = jsonb.fromJson("{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}", Object.class);
        assertTrue(pojo instanceof ReverseTreeMap, "Pojo is not of type ReverseTreeMap");
        @SuppressWarnings("unchecked")
        SortedMap<String, Object> pojoAsMap = (SortedMap<String, Object>) pojo;
        assertTrue(pojoAsMap.get("second") instanceof TreeMap, "Pojo inner object is not of type TreeMap");
        assertEquals("{\"third\":{},\"second\":{\"second\":2,\"first\":1},\"first\":{}}", jsonb.toJson(pojo));
    }

    @Test
    public void testObjectDerializerWithAnyOrNoneOrderStrategy() {
        String json = "{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}";
        // ANY
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.ANY));
        Object pojo = jsonb.fromJson(json, Object.class);
        assertTrue(pojo instanceof HashMap, "Pojo is not of type HashMap with \"ANY\" strategy");
        // none
        pojo = defaultJsonb.fromJson(json, Object.class);
        assertTrue(pojo instanceof HashMap, "Pojo is not of type HashMap with no strategy");
    }

    @Test
    public void testSortedMapDerializer() {
        String json = "{\"first\":1,\"third\":3,\"second\":2}";

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.ANY));
        SortedMap<?, ?> pojo = jsonb.fromJson(json, SortedMap.class);
        assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with \"ANY\" strategy");

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        pojo = jsonb.fromJson(json, SortedMap.class);
        assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with no strategy");
        assertEquals("{\"first\":1,\"second\":2,\"third\":3}", jsonb.toJson(pojo));

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        pojo = jsonb.fromJson(json, SortedMap.class);
        assertTrue(pojo instanceof ReverseTreeMap, "Pojo is not of type ReverseTreeMap with no strategy");
        assertEquals("{\"third\":3,\"second\":2,\"first\":1}", jsonb.toJson(pojo));

        pojo = defaultJsonb.fromJson(json, SortedMap.class);
        assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with no strategy");
        assertEquals("{\"first\":1,\"second\":2,\"third\":3}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testGenericPropertyPojoSerializer() {
        GenericPropertyPojo<Number> numberPojo = new GenericPropertyPojo<>();
        numberPojo.setProperty(10L);
        GenericPropertyPojo<String> stringPojo = new GenericPropertyPojo<>();
        stringPojo.setProperty("String property");

        String numResult = defaultJsonb.toJson(numberPojo, new TestTypeToken<GenericPropertyPojo<Number>>(){}.getType());
        assertEquals("{\"propertyByUserSerializer\":\"Number value [10]\"}", numResult);

        String strResult = defaultJsonb.toJson(stringPojo, new TestTypeToken<GenericPropertyPojo<String>>(){}.getType());
        // because GenericPropertyPojo is annotated to use GenericPropertyPojoSerializer, it will always be
        // used, despite the fact that the runtime type supplied does not match the serializer type
        assertEquals("{\"propertyByUserSerializer\":\"Number value [String property]\"}", strResult);
    }

    @Test
    public void testSerializeMapWithNulls() {
        assertEquals("{\"null\":null}", nullableJsonb.toJson(singletonMap(null, null)));
        assertEquals("{\"key\":null}", nullableJsonb.toJson(singletonMap("key", null)));
        assertEquals("{\"null\":\"value\"}", nullableJsonb.toJson(singletonMap(null, "value")));
    }

    /**
     * Test serialization of Map with String keys only.
     * Map shall be stored as a single JsonObject with keys as object properties names.
     */
    @Test
    public void testSerializeMapToJsonObject() {
        Map<String, Object> map = new HashMap<>();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());
        map.put("name", "John SMith");
        map.put("age", 35);
        map.put("married", true);
        String json = jsonb.toJson(map);
        JsonObject jobj = Json.createReader(new StringReader(json)).read().asJsonObject();
        assertEquals("John SMith", jobj.getString("name"));
        assertEquals(35, jobj.getInt("age"));
        assertEquals(true, jobj.getBoolean("married"));
    }

    @Test
    public void testDeserializeArrayWithAdvancingParserAfterObjectEnd() {
        String json = "[{\"stringProperty\":\"Property 1 value\"},{\"stringProperty\":\"Property 2 value\"}]";
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withDeserializers(new SimplePojoDeserializer()));
        SimplePojo[] result = jsonb.fromJson(json, SimplePojo[].class);
        assertEquals(2, result.length);
    }

    public class SimplePojoDeserializer implements JsonbDeserializer<SimplePojo> {
        @Override
        public SimplePojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            //parser.getObject advances the parser to END_OBJECT.
            JsonObject json = parser.getObject();
            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setStringProperty(json.getString("stringProperty"));
            return simplePojo;
        }
    }

    public class SimplePojo {
        private String stringProperty;

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }

    private static Box createPojoWithDates() {
        Date date = getExpectedDate();
        Box box = createPojo();
        box.crate.date = date;
        box.crate.crateInner.date = date;
        return box;
    }

    private static Box createPojo() {
        Box box = new Box();
        box.boxStr = "Box string";
        box.crate = new Crate();
        box.secondBoxStr = "Second box string";


        box.crate.crateInner = createCrateInner("Single inner");

        box.crate.crateInnerList = new ArrayList<>();
        box.crate.crateInnerList.add(createCrateInner("List inner 0"));
        box.crate.crateInnerList.add(createCrateInner("List inner 1"));

        return box;
    }

    private static CrateInner createCrateInner(String name) {
        final CrateInner crateInner = new CrateInner();
        crateInner.crateInnerStr = name;
        crateInner.crateInnerBigDec = BigDecimal.TEN;
        return crateInner;
    }
}
