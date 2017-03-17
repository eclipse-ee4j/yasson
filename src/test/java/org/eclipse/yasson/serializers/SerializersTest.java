/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.serializers;

import org.eclipse.yasson.adapters.PolymorphismAdapterTest;
import org.eclipse.yasson.serializers.model.AnnotatedWithSerializerType;
import org.eclipse.yasson.serializers.model.Box;
import org.eclipse.yasson.serializers.model.BoxWithAnnotations;
import org.eclipse.yasson.serializers.model.Crate;
import org.eclipse.yasson.serializers.model.CrateDeserializer;
import org.eclipse.yasson.serializers.model.CrateDeserializerWithConversion;
import org.eclipse.yasson.serializers.model.CrateInner;
import org.eclipse.yasson.serializers.model.CrateJsonObjectDeserializer;
import org.eclipse.yasson.serializers.model.CrateSerializer;
import org.eclipse.yasson.serializers.model.CrateSerializerWithConversion;
import org.eclipse.yasson.serializers.model.PolymorphicDeserializer;
import org.eclipse.yasson.serializers.model.SimpleAnnotatedSerializedArrayContainer;
import org.eclipse.yasson.serializers.model.SimpleContainer;
import org.eclipse.yasson.serializers.model.StringWrapper;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        crate.annotatedTypeOverridenOnProperty = new AnnotatedWithSerializerType();
        crate.annotatedTypeOverridenOnProperty.value = "def";
        final Jsonb jsonb = JsonbBuilder.create();
        String expected = "{\"annotatedType\":{\"valueField\":\"replaced value\"},\"annotatedTypeOverridenOnProperty\":{\"valueField\":\"overridden value\"},\"crateBigDec\":10,\"crate_str\":\"crateStr\"}";

        assertEquals(expected, jsonb.toJson(crate));

        Crate result = jsonb.fromJson(expected, Crate.class);
        assertEquals("replaced value", result.annotatedType.value);
        assertEquals("overridden value", result.annotatedTypeOverridenOnProperty.value);

    }

    @Test
    public void testPolymorphicDeserializer() {
        JsonbConfig config = new JsonbConfig().withDeserializers(new PolymorphicDeserializer());
        Jsonb jsonb = JsonbBuilder.create(config);

        String json = "{\"className\":\"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Dog\",\"pojo\":{\"name\":\"Uberdog\",\"dogProperty\":\"dog property\"}}";
        PolymorphismAdapterTest.Animal animal = jsonb.fromJson(json, PolymorphismAdapterTest.Animal.class);
        assertTrue(animal instanceof PolymorphismAdapterTest.Dog);
        assertEquals("Uberdog", animal.name);
        assertEquals("dog property", ((PolymorphismAdapterTest.Dog) animal).dogProperty);
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
        JsonbConfig config = new JsonbConfig().withSerializers(new CrateSerializer()).withFailOnUnknownProperties(false);
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

    private Date getExpectedDate() {
        return new Calendar.Builder().setDate(2015, 4, 14).setTimeOfDay(11, 10, 1).build().getTime();
    }

    @Test
    public void testSerializationUsingConversion() {
        JsonbConfig config = new JsonbConfig().withSerializers(new CrateSerializerWithConversion());
        Jsonb jsonb = JsonbBuilder.create(config);

        //TODO fix / uncomment after keyname argument will be added to JsonbSerializer
        String json = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\",\"date\":\"14.05.2015 || 11:10:01\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01\"},\"secondBoxStr\":\"Second box string\"}";
        assertEquals(json, jsonb.toJson(createPojoWithDates()));
    }

    @Test
    public void testAnnotations() {
        final Jsonb jsonb = JsonbBuilder.create();
        BoxWithAnnotations box = new BoxWithAnnotations();
        box.boxStr = "Box string";
        box.secondBoxStr = "Second box string";
        box.crate = new Crate();
        box.crate.date = getExpectedDate();
        box.crate.crateInner = createCrateInner("Single inner");

        box.crate.crateInnerList = new ArrayList<>();
        box.crate.crateInnerList.add(createCrateInner("List inner 0"));
        box.crate.crateInnerList.add(createCrateInner("List inner 1"));

        //TODO fix / uncomment after keyname argument will be added to JsonbSerializer
//        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01\"},\"secondBoxStr\":\"Second box string\"}";
        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01\"},\"secondBoxStr\":\"Second box string\"}";

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

        //TODO fix / uncomment after keyname argument will be added to JsonbSerializer
        String expected = "{\"boxStr\":\"Box string\",\"crate\":{\"crateStr\":\"REPLACED crate str\",\"crateInner\":{\"crateInnerBigDec\":10,\"crate_inner_str\":\"Single inner\"},\"crateInnerList\":[{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 0\"},{\"crateInnerBigDec\":10,\"crate_inner_str\":\"List inner 1\"}],\"crateBigDec\":54321,\"date-converted\":\"2015-05-14T11:10:01\"},\"secondBoxStr\":\"Second box string\"}";

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
        Jsonb jsonb = JsonbBuilder.create();
        StringWrapper pojo = new StringWrapper();
        pojo.strField = "abc";
        final String result = jsonb.toJson(pojo);
        System.out.println("result = " + result);
    }

    @Test
    public void testContainerSerializer() {
        Jsonb jsonb = JsonbBuilder.create();

        SimpleAnnotatedSerializedArrayContainer container = new SimpleAnnotatedSerializedArrayContainer();
        SimpleContainer instance1 = new SimpleContainer();
        instance1.setInstance("Test String 1");
        SimpleContainer instance2 = new SimpleContainer();
        instance2.setInstance("Test String 2");
        container.setArrayInstance(new SimpleContainer[] {instance1, instance2});

        container.setListInstance(new ArrayList<>());
        container.getListInstance().add(new SimpleContainer("Test List 1"));
        container.getListInstance().add(new SimpleContainer("Test List 2"));

        String jsonString = jsonb.toJson(container);
        Assert.assertEquals("{\"arrayInstance\":[{\"instance\":\"Test String 1\"},{\"instance\":\"Test String 2\"}],\"listInstance\":[{\"instance\":\"Test List 1\"},{\"instance\":\"Test List 2\"}]}", jsonString);

        SimpleAnnotatedSerializedArrayContainer unmarshalledObject = jsonb.fromJson("{\"arrayInstance\":[{\"instance\":\"Test String 1\"},{\"instance\":\"Test String 2\"}],\"listInstance\":[{\"instance\":\"Test List 1\"},{\"instance\":\"Test List 2\"}]}", SimpleAnnotatedSerializedArrayContainer.class);

        Assert.assertEquals("Test String 1", unmarshalledObject.getArrayInstance()[0].getInstance());
        Assert.assertEquals("Test String 2", unmarshalledObject.getArrayInstance()[1].getInstance());

        Assert.assertEquals("Test List 1", unmarshalledObject.getListInstance().get(0).getInstance());
        Assert.assertEquals("Test List 2", unmarshalledObject.getListInstance().get(1).getInstance());
    }

    private Box createPojoWithDates() {
        Date date = getExpectedDate();
        Box box = createPojo();
        box.crate.date = date;
        box.crate.crateInner.date = date;
        return box;
    }

    private Box createPojo() {
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

    private CrateInner createCrateInner(String name) {
        final CrateInner crateInner = new CrateInner();
        crateInner.crateInnerStr = name;
        crateInner.crateInnerBigDec = BigDecimal.TEN;
        return crateInner;
    }


}
