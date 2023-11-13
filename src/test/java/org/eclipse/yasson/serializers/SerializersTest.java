/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyOrderStrategy;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonConfig;
import org.eclipse.yasson.internal.model.ReverseTreeMap;
import org.eclipse.yasson.serializers.model.AnnotatedGenericWithSerializerType;
import org.eclipse.yasson.serializers.model.AnnotatedWithSerializerType;
import org.eclipse.yasson.serializers.model.Author;
import org.eclipse.yasson.serializers.model.Box;
import org.eclipse.yasson.serializers.model.BoxWithAnnotations;
import org.eclipse.yasson.serializers.model.Cars;
import org.eclipse.yasson.serializers.model.Colors;
import org.eclipse.yasson.serializers.model.Containee;
import org.eclipse.yasson.serializers.model.Container;
import org.eclipse.yasson.serializers.model.Crate;
import org.eclipse.yasson.serializers.model.CrateDeserializer;
import org.eclipse.yasson.serializers.model.CrateDeserializerWithConversion;
import org.eclipse.yasson.serializers.model.CrateInner;
import org.eclipse.yasson.serializers.model.CrateJsonObjectDeserializer;
import org.eclipse.yasson.serializers.model.CrateSerializer;
import org.eclipse.yasson.serializers.model.CrateSerializerWithConversion;
import org.eclipse.yasson.serializers.model.ExplicitJsonbSerializer;
import org.eclipse.yasson.serializers.model.GenericPropertyPojo;
import org.eclipse.yasson.serializers.model.ImplicitJsonbSerializer;
import org.eclipse.yasson.serializers.model.JsonParserTestDeserializers;
import org.eclipse.yasson.serializers.model.JsonParserTestPojo;
import org.eclipse.yasson.serializers.model.NumberDeserializer;
import org.eclipse.yasson.serializers.model.NumberSerializer;
import org.eclipse.yasson.serializers.model.TwoObjectsComparer;
import org.eclipse.yasson.serializers.model.RecursiveDeserializer;
import org.eclipse.yasson.serializers.model.RecursiveSerializer;
import org.eclipse.yasson.serializers.model.SimpleAnnotatedSerializedArrayContainer;
import org.eclipse.yasson.serializers.model.SimpleContainer;
import org.eclipse.yasson.serializers.model.StringWrapper;
import org.eclipse.yasson.serializers.model.SupertypeSerializerPojo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonMap;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.eclipse.yasson.Jsonbs.nullableJsonb;
import static org.eclipse.yasson.Jsonbs.testWithJsonbBuilderCreate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Roman Grigoriadi
 */
public class SerializersTest {

    private static final JsonbSerializer<Box> BOX_ARRAY_SERIALIZER_CHAINED = new JsonbSerializer<>() {
        public void serialize(Box box, JsonGenerator out, SerializationContext ctx) {
            out.writeStartArray()
                    .write(box.boxStr)
                    .write(box.secondBoxStr)
                    .writeEnd();
        }
    };

    private static final JsonbSerializer<Box> BOX_ARRAY_SERIALIZER = new JsonbSerializer<>() {
        public void serialize(Box box, JsonGenerator out, SerializationContext ctx) {
            out.writeStartArray();
            out.write(box.boxStr);
            out.write(box.secondBoxStr);
            out.writeEnd();
        }
	};

    private static final JsonbSerializer<Box> BOX_ARRAY_SERIALIZER_CHAINED_AS_LAMBDA = (box, out, ctx) -> out.writeStartArray()
            .write(box.boxStr)
            .write(box.secondBoxStr)
            .writeEnd();

    private static final JsonbSerializer<Box> BOX_ARRAY_SERIALIZER_AS_LAMBDA = (box, out, ctx) -> {
        out.writeStartArray();
        out.write(box.boxStr);
        out.write(box.secondBoxStr);
        out.writeEnd();
    };

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
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new CrateDeserializer()), jsonb -> {

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
        });
    }

    /**
     * Tests JSONB serialization of arbitrary type invoked from a Serializer.
     */
    @Test
    public void testSerializerSerializationOfType() {
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new CrateSerializer()), jsonb -> {
                String expected =
                        "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
            Box pojo = createPojo();

            assertEquals(expected, jsonb.toJson(pojo));

            Box result = jsonb.fromJson(expected, Box.class);
            assertEquals(new BigDecimal("54321"), result.crate.crateBigDec);
            //result.crate.crateStr is mapped to crate_str by jsonb property
            assertNull(result.crate.crateStr);
            assertEquals(pojo.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);
            assertEquals(pojo.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
        });
    }

    @Test
    public void testSerializerSerializationOfTypeWithExplicitType() {
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new CrateSerializer()), jsonb -> {
            String expected =
                    "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
            Box pojo = createPojo();

            assertEquals(expected, jsonb.toJson(pojo, Box.class));

            Box result = jsonb.fromJson(expected, Box.class);
            assertEquals(new BigDecimal("54321"), result.crate.crateBigDec);
            //result.crate.crateStr is mapped to crate_str by jsonb property
            assertNull(result.crate.crateStr);
            assertEquals(pojo.crate.crateInner.crateInnerStr, result.crate.crateInner.crateInnerStr);
            assertEquals(pojo.crate.crateInner.crateInnerBigDec, result.crate.crateInner.crateInnerBigDec);
        });
    }

    /**
     * Tests jsonb type conversion, including property customization.
     */
    @Test
    public void testDeserializersUsingConversion() {
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new CrateDeserializerWithConversion()), jsonb -> {

            String json =
                    "{\"boxStr\":\"Box string\",\"crate\":{\"date-converted\":\"2015-05-14T11:10:01\",\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
            Box result = jsonb.fromJson(json, Box.class);
            final Date expected = getExpectedDate();
            assertEquals(expected, result.crate.date);
            assertEquals("Box string", result.boxStr);
            assertEquals("Second box string", result.secondBoxStr);
        });
    }

    @Test
    public void testCrateJsonObjectDeserializer() {
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new CrateJsonObjectDeserializer()), jsonb -> {
            String expected =
                    "{\"boxStr\":\"Box string\",\"crate\":{\"date-converted\":\"2015-05-14T11:10:01\",\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crateInnerStr\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321},\"secondBoxStr\":\"Second box string\"}";
            Box result = jsonb.fromJson(expected, Box.class);
            assertEquals(new BigDecimal("54321"), result.crate.crateBigDec);
            assertEquals("REPLACED crate str", result.crate.crateStr);
            assertEquals("Single inner", result.crate.crateInner.crateInnerStr);
            assertEquals(BigDecimal.TEN, result.crate.crateInner.crateInnerBigDec);
        });
    }

    private static Date getExpectedDate() {
        return new Calendar.Builder().setDate(2015, 4, 14).setTimeOfDay(11, 10, 1).setTimeZone(TimeZone.getTimeZone("Z")).build().getTime();
    }

    @Test
    public void testSerializationUsingConversion() {
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new CrateSerializerWithConversion()), jsonb -> {

            String json =
                    "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01Z[UTC]\"},\"secondBoxStr\":\"Second box string\"}";
            assertEquals(json, jsonb.toJson(createPojoWithDates()));
        });
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
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new CrateJsonObjectDeserializer()).withSerializers(new CrateSerializer()), jsonb -> {

            BoxWithAnnotations box = new BoxWithAnnotations();
            box.boxStr = "Box string";
            box.secondBoxStr = "Second box string";
            box.crate = new Crate();
            box.crate.crateInner = createCrateInner("Single inner");
            box.crate.date = getExpectedDate();

            box.crate.crateInnerList = new ArrayList<>();
            box.crate.crateInnerList.add(createCrateInner("List inner 0"));
            box.crate.crateInnerList.add(createCrateInner("List inner 1"));

            String expected =
                    "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01Z[UTC]\"},\"secondBoxStr\":\"Second box string\"}";

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
        });
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
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new RecursiveSerializer()).withDeserializers(new RecursiveDeserializer()), jsonb -> {

            Box box = new Box();
            box.boxStr = "Box to serialize";
            try {
                jsonb.toJson(box);
                fail();
            } catch (JsonbException ignored) {
            }

            try {
                jsonb.fromJson("{\"boxStr\":\"Box to deserialize\"}", Box.class);
                fail();
            } catch (StackOverflowError ignored) {
            }
        });
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
    public void testSupertypeSerializer_withConfiguration() {
        NumberSerializer.getCounter().resetCount();
        NumberDeserializer.getCounter().resetCount();
        testWithJsonbBuilderCreate(new JsonbConfig()
                .withSerializers(new NumberSerializer())
                .withDeserializers(new NumberDeserializer()), jsonb -> {
            SupertypeSerializerPojo pojo = new SupertypeSerializerPojo();
            pojo.setNumberInteger(10);
            pojo.setAnotherNumberInteger(11);
            assertEquals("{\"anotherNumberInteger\":\"12\",\"numberInteger\":\"11\"}", jsonb.toJson(pojo));

            pojo = jsonb.fromJson("{\"anotherNumberInteger\":\"12\",\"numberInteger\":\"11\"}", SupertypeSerializerPojo.class);
            assertEquals(Integer.valueOf(10), pojo.getNumberInteger());
            assertEquals(Integer.valueOf(11), pojo.getAnotherNumberInteger());
            //assert that deserializer and serializer were reused
            assertEquals(1, NumberSerializer.getCounter().getCount());
            assertEquals(1, NumberDeserializer.getCounter().getCount());
        });
    }

    @Test
    public void testSupertypeSerializer() {
        NumberSerializer.getCounter().resetCount();
        NumberDeserializer.getCounter().resetCount();
        SupertypeSerializerPojo pojo = new SupertypeSerializerPojo();
        pojo.setNumberInteger(9);
        pojo.setAnotherNumberInteger(11);
        assertEquals("{\"anotherNumberInteger\":11,\"numberInteger\":\"10\"}", defaultJsonb.toJson(pojo));

        pojo = defaultJsonb.fromJson("{\"anotherNumberInteger\":11,\"numberInteger\":\"10\"}", SupertypeSerializerPojo.class);
        assertEquals(Integer.valueOf(9), pojo.getNumberInteger());
        assertEquals(Integer.valueOf(11), pojo.getAnotherNumberInteger());
        //assert that deserializer and serializer were used just once
        assertEquals(1, NumberSerializer.getCounter().getCount());
        assertEquals(1, NumberDeserializer.getCounter().getCount());
    }

    @Test
    public void testObjectDeserializerWithLexOrderStrategy() {
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL), jsonb -> {
            Object pojo = jsonb.fromJson("{\"first\":{},\"third\":{},\"second\":{\"second\":2,\"first\":1}}", Object.class);
            assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap");
            @SuppressWarnings("unchecked")
            SortedMap<String, Object> pojoAsMap = (SortedMap<String, Object>) pojo;
            assertTrue(pojoAsMap.get("second") instanceof TreeMap, "Pojo inner object is not of type TreeMap");
            assertEquals("{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}", jsonb.toJson(pojo));
        });
    }
    
    @Test
    public void testObjectDeserializerWithReverseOrderStrategy() {
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE), jsonb -> {
            Object pojo = jsonb.fromJson("{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}", Object.class);
            assertTrue(pojo instanceof ReverseTreeMap, "Pojo is not of type ReverseTreeMap");
            @SuppressWarnings("unchecked")
            SortedMap<String, Object> pojoAsMap = (SortedMap<String, Object>) pojo;
            assertTrue(pojoAsMap.get("second") instanceof TreeMap, "Pojo inner object is not of type TreeMap");
            assertEquals("{\"third\":{},\"second\":{\"second\":2,\"first\":1},\"first\":{}}", jsonb.toJson(pojo));
        });
    }

    @Test
    public void testObjectDeserializerWithAnyOrNoneOrderStrategy() {
        String json = "{\"first\":{},\"second\":{\"first\":1,\"second\":2},\"third\":{}}";
        // ANY
        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.ANY), jsonb -> {
            Object pojo = jsonb.fromJson(json, Object.class);
            assertTrue(pojo instanceof HashMap, "Pojo is not of type HashMap with \"ANY\" strategy");
            // none
            pojo = defaultJsonb.fromJson(json, Object.class);
            assertTrue(pojo instanceof HashMap, "Pojo is not of type HashMap with no strategy");
        });
    }

    @Test
    public void testSortedMapDeserializer() {
        String json = "{\"first\":1,\"third\":3,\"second\":2}";

        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.ANY), jsonb -> {
            SortedMap<?, ?> pojo = jsonb.fromJson(json, SortedMap.class);
            assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with \"ANY\" strategy");
        });

        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL), jsonb -> {
            SortedMap<?, ?> pojo = jsonb.fromJson(json, SortedMap.class);
            assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with no strategy");
            assertEquals("{\"first\":1,\"second\":2,\"third\":3}", jsonb.toJson(pojo));
        });

        testWithJsonbBuilderCreate(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE), jsonb -> {
            SortedMap<?, ?> pojo = jsonb.fromJson(json, SortedMap.class);
            assertTrue(pojo instanceof ReverseTreeMap, "Pojo is not of type ReverseTreeMap with no strategy");
            assertEquals("{\"third\":3,\"second\":2,\"first\":1}", jsonb.toJson(pojo));

            pojo = defaultJsonb.fromJson(json, SortedMap.class);
            assertTrue(pojo instanceof TreeMap, "Pojo is not of type TreeMap with no strategy");
            assertEquals("{\"first\":1,\"second\":2,\"third\":3}", defaultJsonb.toJson(pojo));
        });
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

    @Test
    public void testSerializeMapWithNullsForceArraySerializer() {
        testWithJsonbBuilderCreate(new YassonConfig()
                .withForceMapArraySerializerForNullKeys(Boolean.TRUE)
                .withNullValues(Boolean.TRUE), jsonb -> {
            assertEquals("[{\"key\":null,\"value\":null}]", jsonb.toJson(singletonMap(null, null)));
            assertEquals("{\"key\":null}", jsonb.toJson(singletonMap("key", null)));
            assertEquals("[{\"key\":null,\"value\":\"value\"}]", jsonb.toJson(singletonMap(null, "value")));
        });
    }

    /**
     * Test serialization of Map with String keys only.
     * Map shall be stored as a single JsonObject with keys as object properties names.
     */
    @Test
    public void testSerializeMapToJsonObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John SMith");
        map.put("age", 35);
        map.put("married", true);
        String json = defaultJsonb.toJson(map);
        JsonObject jobj = Json.createReader(new StringReader(json)).read().asJsonObject();
        assertEquals("John SMith", jobj.getString("name"));
        assertEquals(35, jobj.getInt("age"));
        assertTrue(jobj.getBoolean("married"));
    }

    @Test
    public void testDeserializeArrayWithAdvancingParserAfterObjectEnd() {
        String json = "[{\"stringProperty\":\"Property 1 value\"},{\"stringProperty\":\"Property 2 value\"}]";
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new SimplePojoDeserializer()), jsonb -> {
            SimplePojo[] result = jsonb.fromJson(json, SimplePojo[].class);
            assertEquals(2, result.length);
            assertEquals("Property 1 value", result[0].getStringProperty());
        });
    }

    public static class SimplePojoDeserializer implements JsonbDeserializer<SimplePojo> {
        @Override
        public SimplePojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            //parser.getObject advances the parser to END_OBJECT.
            JsonObject json = parser.getObject();
            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setStringProperty(json.getString("stringProperty"));
            return simplePojo;
        }
    }

    @Test
    public void testDeserializeArrayWithAdvancingParserAfterObjectEndUsingValue() {
        String json = "[{\"stringProperty\":\"Property 1 value\"},{\"stringProperty\":\"Property 2 value\"}]";
        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(new SimplePojoValueDeserializer()), jsonb -> {
            SimplePojo[] result = jsonb.fromJson(json, SimplePojo[].class);
            assertEquals(2, result.length);
            assertEquals("Property 1 value", result[0].getStringProperty());
        });
    }

    public static class SimplePojoValueDeserializer implements JsonbDeserializer<SimplePojo> {
        @Override
        public SimplePojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            //parser.getValue advances the parser to END_OBJECT in case of object.
            JsonObject json = parser.getValue().asJsonObject();
            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setStringProperty(json.getString("stringProperty"));
            return simplePojo;
        }
    }

    public static class SimplePojo {
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
    
    public static class Foo { }

    public static class Bar extends Foo { }
    
    public static class Baz extends Bar { }

    public static class FooSerializer implements JsonbSerializer<Foo> {
      public void serialize(Foo obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("foo");
      }
    }

    public static class BazSerializer implements JsonbSerializer<Baz> {
      public void serialize(Baz obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("baz");
      }
    }
    
    /**
     * Test for issue: <a href="https://github.com/quarkusio/quarkus/issues/8925">issue 8925</a>
     * Ensure that if multiple customizations (serializer, deserializer, or adapter) are applied 
     * for different types in the same class hierarchy, we use the customization for the most 
     * specific type in the class hierarchy.
     */
    @Test
    public void testSerializerMatching() {
      testWithJsonbBuilderCreate(new JsonbConfig()
          .withSerializers(new FooSerializer(), new BazSerializer()), jsonb -> {
          assertEquals("\"foo\"", jsonb.toJson(new Foo()));
          // Since 'Bar' does not have its own serializer, it should use
          // the next serializer in the tree (FooSerializer)
          assertEquals("\"foo\"", jsonb.toJson(new Bar()));
          assertEquals("\"baz\"", jsonb.toJson(new Baz()));
      });
    }
    
    public interface One { }
    public interface Two { }

    public interface Three { }
    
    public static class OneTwo implements One, Two { }
    public static class OneTwoThree implements One, Two, Three { }
    
    public static class OneSerializer implements JsonbSerializer<One> {
      public void serialize(One obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("one");
      }
    }
    
    public static class TwoSerializer implements JsonbSerializer<Two> {
      public void serialize(Two obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("two");
      }
    }
    
    public static class ThreeSerializer implements JsonbSerializer<Three> {
      public void serialize(Three obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write("three");
      }
    }
    
    @Test
    public void testSerializerMatchingInterfaces01() {
      testWithJsonbBuilderCreate(new JsonbConfig()
          .withSerializers(new OneSerializer(), new TwoSerializer(), new ThreeSerializer()), jsonb -> {
          assertEquals("\"one\"", jsonb.toJson(new OneTwo()));
          assertEquals("\"one\"", jsonb.toJson(new OneTwoThree()));
      });
    }
    
    @Test
    public void testSerializerMatchingInterfaces02() {
      testWithJsonbBuilderCreate(new JsonbConfig()
          .withSerializers(new ThreeSerializer(), new TwoSerializer()), jsonb -> {
          assertEquals("\"two\"", jsonb.toJson(new OneTwo()));
          assertEquals("\"two\"", jsonb.toJson(new OneTwoThree()));
      });
    }

    public static class GenericBean<T> {

        public T value;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GenericBean){
                return Objects.equals(GenericBean.class.cast(obj).value, this.value);
            }
            return Boolean.FALSE;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class GenericBeanSerializer implements JsonbSerializer<GenericBean> {

        private Boolean called = Boolean.FALSE;

        @Override
        public void serialize(GenericBean t, JsonGenerator jg, SerializationContext sc) {
            called = Boolean.TRUE;
            jg.writeStartObject();
            sc.serialize("value", t.value, jg);
            jg.writeEnd();
        }
    }

    public static class GenericBeanDeserializer implements JsonbDeserializer<GenericBean> {

        private Boolean called = Boolean.FALSE;

        @Override
        public GenericBean deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            called = Boolean.TRUE;
            JsonObject json = parser.getObject();
            GenericBean<String> bean = new GenericBean<>();
            bean.value = json.getString("value");
            return bean;
        }
    }

    @Test
    public void testCustomDeserializerWithParameterizedType() {

        GenericBeanSerializer genericBeanSerializer = new GenericBeanSerializer();
        GenericBeanDeserializer genericBeanDeserializer = new GenericBeanDeserializer();

        testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(genericBeanDeserializer).withSerializers(genericBeanSerializer), jsonb -> {

            GenericBean<String> bean1 = new GenericBean<>();
            bean1.value = "test1";
            GenericBean<String> bean2 = new GenericBean<>();
            bean2.value = "test2";
            GenericBean<String> bean3 = new GenericBean<>();
            bean3.value = "test3";

            Collection<GenericBean<String>> asList = Arrays.asList(bean1, bean2, bean3);

            String toJson = jsonb.toJson(asList);

            assertEquals(toJson, "[{\"value\":\"test1\"},{\"value\":\"test2\"},{\"value\":\"test3\"}]");
            assertTrue(genericBeanSerializer.called);

            List<GenericBean<String>> fromJson = jsonb.fromJson(
                    toJson,
                    new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            return new Type[] {GenericBean.class};
                        }

                        @Override
                        public Type getRawType() {
                            return Collection.class;
                        }

                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    }
            );

            assertEquals(asList, fromJson);
            assertTrue(genericBeanDeserializer.called);
        });
    }

    @Test
    public void testImplicitJsonbSerializers() {
        String expected = "{\"value\":\"123\"}";
        Box box = new Box();
        box.boxStr = "Box";

        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new ExplicitJsonbSerializer()), jsonb -> assertEquals(expected, jsonb.toJson(box)));

        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(new ImplicitJsonbSerializer()), jsonb -> assertEquals(expected, jsonb.toJson(box)));
    }

    @Test
    public void testBoxToArrayChained() {
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(BOX_ARRAY_SERIALIZER_CHAINED), jsonb -> {
            Box box = new Box();
            box.boxStr = "str1";
            box.secondBoxStr = "str2";
            String expected = "[\"str1\",\"str2\"]";

            assertThat(jsonb.toJson(box), is(expected));
        });
    }

    @Test
    public void testBoxToArray() {
        testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(BOX_ARRAY_SERIALIZER), jsonb -> {
            Box box = new Box();
            box.boxStr = "str1";
            box.secondBoxStr = "str2";
            String expected = "[\"str1\",\"str2\"]";

            assertThat(jsonb.toJson(box), is(expected));
        });
    }

    @Test
    public void testBoxToArrayChainedWithLambda() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(BOX_ARRAY_SERIALIZER_CHAINED_AS_LAMBDA), jsonb -> {
            Box box = new Box();
            box.boxStr = "str1";
            box.secondBoxStr = "str2";
            String expected = "[\"str1\",\"str2\"]";

            assertThat(jsonb.toJson(box), is(expected));
        }));

        assertInstanceOf(JsonbException.class, runtimeException.getCause());
    }

    @Test
    public void testBoxToArrayWithLambda() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> testWithJsonbBuilderCreate(new JsonbConfig().withSerializers(BOX_ARRAY_SERIALIZER_AS_LAMBDA), jsonb -> {
            Box box = new Box();
            box.boxStr = "str1";
            box.secondBoxStr = "str2";
            String expected = "[\"str1\",\"str2\"]";

            assertThat(jsonb.toJson(box), is(expected));
        }));

        assertInstanceOf(JsonbException.class, runtimeException.getCause());
    }

    @Test
    public void testCustomSerializersInContainer() {
        Container expected = new Container(List.of(new Containee("k", "v")));

        String expectedJson = defaultJsonb.toJson(expected);
        System.out.println(expectedJson);

        assertEquals(expected, defaultJsonb.fromJson(expectedJson, Container.class));
    }

    @Test
    public void testCustomSerializersAndDeserializersInEnum() {
        Colors expected = Colors.GREEN;

        String expectedJson = defaultJsonb.toJson(expected);

        assertEquals(expected, defaultJsonb.fromJson(expectedJson, Colors.class));
    }

    @Test
    public void testJsonbPropertyInEnum() {
        Cars expected = Cars.FORD;

        String expectedJson = defaultJsonb.toJson(expected);

        assertEquals(expected, defaultJsonb.fromJson(expectedJson, Cars.class));
    }

    @Test
    public void testNoJsonbPropertyInEnum() {
        Cars expected = Cars.FIAT;

        String expectedJson = defaultJsonb.toJson(expected);

        assertEquals(expected, defaultJsonb.fromJson(expectedJson, Cars.class));
    }

    @Nested
    class YassonParserTests{
        @Test
        public void testJsonParserFunctions() {
            JsonParserTestPojo expected = new JsonParserTestPojo().init();

            JsonParserTestDeserializers.JsonParserTestObjectDeserializer
                    deserializer = new JsonParserTestDeserializers.JsonParserTestObjectDeserializer();
            testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(deserializer), jsonb -> {
                String expectedJson = new StringBuilder(jsonb.toJson(expected))
                        .insert(1, "\"stringList_skip\":[\"string7\",\"string8\"],\"subPojo_skip\":{\"name\":\"subPojo_skip\"},").toString();

                assertTrue(TwoObjectsComparer.getDifferentFieldInTwoObjects(expected, jsonb.fromJson(expectedJson, JsonParserTestPojo.class))
                        .isEmpty());
                assertEquals(List.of("stringList_skip", "subPojo_skip", "bigDecimal", "integer", "longValue", "string", "stringList",
                                "stringList_getStream", "stringList_getValue", "string_getValue", "subPojo", "subPojo_getStream", "subPojo_getValue"),
                        deserializer.getKeyNames());

                List<JsonParser.Event> expectedListOfEvents = List.of(JsonParser.Event.START_OBJECT, JsonParser.Event.END_ARRAY,
                        JsonParser.Event.END_OBJECT, JsonParser.Event.VALUE_NUMBER, JsonParser.Event.VALUE_NUMBER, JsonParser.Event.VALUE_NUMBER,
                        JsonParser.Event.VALUE_STRING, JsonParser.Event.END_ARRAY, JsonParser.Event.END_ARRAY, JsonParser.Event.END_ARRAY,
                        JsonParser.Event.VALUE_STRING, JsonParser.Event.END_OBJECT, JsonParser.Event.END_OBJECT);

                assertEquals(expectedListOfEvents, deserializer.getContextEvents());
                assertEquals("(line no=1, column no=96, offset=95)", deserializer.getLocation());
                assertTrue(deserializer.isIntegralNumber());
            });
        }

        @Test
        public void testJsonParserValueStream() {
            JsonParserTestPojo expected = new JsonParserTestPojo().init();

            JsonParserTestDeserializers.JsonParserTestValueStreamDeserializer
                    deserializer = new JsonParserTestDeserializers.JsonParserTestValueStreamDeserializer();
            testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(deserializer), jsonb -> {
                String expectedJson = jsonb.toJson(expected);

                assertTrue(TwoObjectsComparer.getDifferentFieldInTwoObjects(expected, jsonb.fromJson(expectedJson, JsonParserTestPojo.class))
                        .isEmpty());
            });
        }

        @Test
        public void testJsonParser_NoSuchElementException() {
            JsonParserTestDeserializers.JsonParserTestNoSuchElementExceptionDeserializer
                    deserializer = new JsonParserTestDeserializers.JsonParserTestNoSuchElementExceptionDeserializer();
            testWithJsonbBuilderCreate(new JsonbConfig().withDeserializers(deserializer), jsonb -> {
                Throwable throwable = null;
                try {
                    jsonb.fromJson("5", JsonParserTestPojo.class);
                    fail("NoSuchElementException should be thrown");
                } catch (JsonbException jbe) {
                    throwable = jbe.getCause();
                }

                assertInstanceOf(NoSuchElementException.class, throwable);
            });
        }
    }
}