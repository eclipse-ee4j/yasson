/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.generics;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.lang.reflect.Field;
import java.util.Collection;
import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.adapters.model.GenericBox;
import org.eclipse.yasson.defaultmapping.generics.model.AnotherGenericTestClass;
import org.eclipse.yasson.defaultmapping.generics.model.BoundedGenericClass;
import org.eclipse.yasson.defaultmapping.generics.model.Circle;
import org.eclipse.yasson.defaultmapping.generics.model.CollectionContainer;
import org.eclipse.yasson.defaultmapping.generics.model.CollectionElement;
import org.eclipse.yasson.defaultmapping.generics.model.CollectionWrapper;
import org.eclipse.yasson.defaultmapping.generics.model.ColoredCircle;
import org.eclipse.yasson.defaultmapping.generics.model.ConstructorContainer;
import org.eclipse.yasson.defaultmapping.generics.model.CyclicSubClass;
import org.eclipse.yasson.defaultmapping.generics.model.FinalGenericWrapper;
import org.eclipse.yasson.defaultmapping.generics.model.FinalMember;
import org.eclipse.yasson.defaultmapping.generics.model.GenericArrayClass;
import org.eclipse.yasson.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.yasson.defaultmapping.generics.model.GenericWithUnboundedWildcardClass;
import org.eclipse.yasson.defaultmapping.generics.model.MultiLevelExtendedGenericTestClass;
import org.eclipse.yasson.defaultmapping.generics.model.MultipleBoundsContainer;
import org.eclipse.yasson.defaultmapping.generics.model.MyCyclicGenericClass;
import org.eclipse.yasson.defaultmapping.generics.model.PropagatedGenericClass;
import org.eclipse.yasson.defaultmapping.generics.model.Shape;
import org.eclipse.yasson.defaultmapping.generics.model.StaticCreatorContainer;
import org.eclipse.yasson.defaultmapping.generics.model.WildCardClass;
import org.eclipse.yasson.defaultmapping.generics.model.WildcardMultipleBoundsClass;
import org.eclipse.yasson.serializers.model.Box;
import org.eclipse.yasson.serializers.model.Crate;
import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import org.eclipse.yasson.defaultmapping.generics.model.LowerBoundTypeVariableWithCollectionAttributeClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class contains JSONB default mapping generics tests.
 *
 * @author Dmitry Kornilov
 */
public class GenericsTest {

    @Test
    public void testGenericClass() {
        GenericTestClass<String, Integer> genericClass = new GenericTestClass<>();
        genericClass.field1 = "value1";
        genericClass.field2 = 3;

        String expected = "{\"field1\":\"value1\",\"field2\":3}";
        assertEquals(expected, defaultJsonb.toJson(genericClass));

        Type type = new TestTypeToken<GenericTestClass<String, Integer>>(){}.getType();
        GenericTestClass<String, Integer> result = defaultJsonb.fromJson(expected, type);
        assertEquals("value1", result.field1);
        assertEquals(Integer.valueOf(3), result.field2);
    }

    @Test
    public void testMultiLevelGenericClass() {

        GenericTestClass<String, Integer> innerMostGenericClass = new GenericTestClass<>();
        innerMostGenericClass.field1 = "innerMostValue3";
        innerMostGenericClass.field2 = 3;

        AnotherGenericTestClass<GenericTestClass<String, Integer>, Integer> another = new AnotherGenericTestClass<>();
        another.field1 = innerMostGenericClass;
        another.field2 = 2;

        GenericTestClass<String, AnotherGenericTestClass<GenericTestClass<String, Integer>, Integer>> nestedGenericField = new GenericTestClass<>();
        nestedGenericField.field1 = "outerValue1";
        nestedGenericField.field2 = another;

        String expected = "{\"field1\":\"outerValue1\",\"field2\":{\"field1\":{\"field1\":\"innerMostValue3\",\"field2\":3},\"field2\":2}}";
        assertEquals(expected, defaultJsonb.toJson(nestedGenericField));

        Type type = new TestTypeToken<GenericTestClass<String, AnotherGenericTestClass<GenericTestClass<String, Integer>, Integer>>>(){}.getType();
        GenericTestClass<String, AnotherGenericTestClass<GenericTestClass<String, Integer>, Integer>> result = defaultJsonb.fromJson(expected, type);
        assertEquals("outerValue1", result.field1);
        assertEquals(Integer.valueOf(2), result.field2.field2);
        assertEquals("innerMostValue3", result.field2.field1.field1);
        assertEquals(Integer.valueOf(3), result.field2.field1.field2);
    }

    @Test
    public void testNestedGenericSelfClass() {
        GenericTestClass<String, Integer> inner = new GenericTestClass<>();
        inner.field1 = "innerValue1";
        inner.field2 = 5;

        GenericTestClass<String, GenericTestClass<String, Integer>> nestedGenericOnSelfClass = new GenericTestClass<>();
        nestedGenericOnSelfClass.field1 = "outerValue1";
        nestedGenericOnSelfClass.field2 = inner;

        String expected = "{\"field1\":\"outerValue1\",\"field2\":{\"field1\":\"innerValue1\",\"field2\":5}}";
        assertEquals(expected, defaultJsonb.toJson(nestedGenericOnSelfClass));

        Type type = new TestTypeToken<GenericTestClass<String, GenericTestClass<String, Integer>>>(){}.getType();
        GenericTestClass<String, GenericTestClass<String, Integer>> result = defaultJsonb.fromJson(expected, type);
        assertEquals("outerValue1", result.field1);
        assertEquals("innerValue1", result.field2.field1);
        assertEquals(Integer.valueOf(5), result.field2.field2);
    }

    @Test
    public void testCyclicGenericClass() {

        final MyCyclicGenericClass<CyclicSubClass> myCyclicGenericClass = new MyCyclicGenericClass<>();
        final CyclicSubClass cyclicSubClass = new CyclicSubClass();
        cyclicSubClass.subField = "subFieldValue";
        myCyclicGenericClass.field1 = cyclicSubClass;

        String expected = "{\"field1\":{\"subField\":\"subFieldValue\"}}";
        assertEquals(expected, defaultJsonb.toJson(myCyclicGenericClass));
        MyCyclicGenericClass<CyclicSubClass> result = defaultJsonb.fromJson(expected, new TestTypeToken<MyCyclicGenericClass<CyclicSubClass>>(){}.getType());
        assertEquals(CyclicSubClass.class, result.field1.getClass());
        assertEquals("subFieldValue", result.field1.subField);
    }

    @Test
    public void testWildCards() {

        final WildCardClass<Integer> integerWildCard = new WildCardClass<>();

        integerWildCard.number = 10;
        String expected = "{\"number\":10}";
        assertEquals(expected, defaultJsonb.toJson(integerWildCard));
        WildCardClass<Integer> result = defaultJsonb.fromJson(expected, new TestTypeToken<WildCardClass<Integer>>(){}.getType());
        assertEquals(Integer.valueOf(10), result.number);
    }

    @Test
    public void testGenericWithUnboundedWildcard() {
        //wildcardList is treated as List<Object>
        String expected = "{\"wildcardList\":[{\"k1\":\"v1\",\"k2\":\"v2\"}]}";

        GenericWithUnboundedWildcardClass genericWithUnboundedWildcardClass = new GenericWithUnboundedWildcardClass();
        List<Map<String, String>> list = new ArrayList<>();
        genericWithUnboundedWildcardClass.wildcardList = list;
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("k1", "v1");
        stringMap.put("k2", "v2");
        list.add(stringMap);
        assertEquals(expected, defaultJsonb.toJson(genericWithUnboundedWildcardClass));

        GenericWithUnboundedWildcardClass result = defaultJsonb.fromJson(expected, GenericWithUnboundedWildcardClass.class);
        assertTrue(result.wildcardList.get(0) instanceof Map);
        assertEquals("v1", ((Map) result.wildcardList.get(0)).get("k1"));
        assertEquals("v2", ((Map) result.wildcardList.get(0)).get("k2"));
    }

    @Test
    public void testWildCardMultipleBounds() {

        WildcardMultipleBoundsClass<BigDecimal> multipleBoundsClass = new WildcardMultipleBoundsClass<>();
        multipleBoundsClass.wildcardField = BigDecimal.ONE;

        GenericTestClass<String, BigDecimal> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "genericTestClassField1";
        genericTestClass.field2 = BigDecimal.TEN;
        multipleBoundsClass.genericTestClassPropagatedWildCard = genericTestClass;

        List<ExtendsBigDecimal> extendsBigDecimalList = new ArrayList<>();
        extendsBigDecimalList.add(new ExtendsBigDecimal("11"));
        multipleBoundsClass.propagatedWildcardList = extendsBigDecimalList;

        String expected = "{\"genericTestClassPropagatedWildCard\":{\"field1\":\"genericTestClassField1\",\"field2\":10},\"propagatedWildcardList\":[11],\"wildcardField\":1}";
        assertEquals(expected, defaultJsonb.toJson(multipleBoundsClass, new WildcardMultipleBoundsClass<BigDecimal>(){}.getClass()));


        WildcardMultipleBoundsClass<BigDecimal> result = defaultJsonb.fromJson(expected, new TestTypeToken<WildcardMultipleBoundsClass<BigDecimal>>(){}.getType());
        assertEquals(BigDecimal.ONE, result.wildcardField);
        assertEquals("genericTestClassField1", result.genericTestClassPropagatedWildCard.field1);
        assertEquals(BigDecimal.TEN, result.genericTestClassPropagatedWildCard.field2);
        assertEquals(new BigDecimal("11"), result.propagatedWildcardList.get(0));

    }

    @Test
    public void testWithType() {
        List<Optional<String>> expected = Arrays.asList(Optional.empty(), Optional.of("first"), Optional.of("second"));
        //String json = jsonb.toJson(expected, DefaultMappingGenericsTest.class.getField("listOfOptionalStringField").getGenericType());

        String json = defaultJsonb.toJson(expected);
        assertEquals("[null,\"first\",\"second\"]", json);
    }

    @Test
    public void testPropagatedGenerics() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);

        GenericTestClass<List<Integer>, String> genericInList = new GenericTestClass<>();
        genericInList.field1 = integerList;
        genericInList.field2 = "GenericsInListF2";

        List<GenericTestClass<List<Integer>, String>> listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        GenericTestClass<Integer, String> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = 1;
        genericTestClass.field2 = "first";

        PropagatedGenericClass<Integer, String> propagatedGenericClass = new PropagatedGenericClass<>();
        propagatedGenericClass.genericTestClass = genericTestClass;
        propagatedGenericClass.genericList = listWithGenerics;

        String expected = "{\"genericList\":[{\"field1\":[1,2],\"field2\":\"GenericsInListF2\"}],\"genericTestClass\":{\"field1\":1,\"field2\":\"first\"}}";

        assertEquals(expected, defaultJsonb.toJson(propagatedGenericClass, new TestTypeToken<PropagatedGenericClass<Integer, String>>(){}.getType()));
        PropagatedGenericClass<Integer, String> result = defaultJsonb.fromJson(expected, new TestTypeToken<PropagatedGenericClass<Integer, String>>(){}.getType());
        assertEquals(GenericTestClass.class, result.genericList.get(0).getClass());
        assertEquals(Integer.valueOf(1), result.genericList.get(0).field1.get(0));
        assertEquals(Integer.valueOf(2), result.genericList.get(0).field1.get(1));
        assertEquals("GenericsInListF2", result.genericList.get(0).field2);
        assertEquals(GenericTestClass.class, result.genericTestClass.getClass());
        assertEquals(Integer.valueOf(1), result.genericTestClass.field1);
        assertEquals("first", result.genericTestClass.field2);

    }

    @Test
    public void testMarshallPropagatedGenericsRaw() {

        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);

        GenericBox<List<Integer>> box = new GenericBox<>();
        box.setX(integerList);
        box.setStrField("IntegerListBox");

        GenericTestClass<GenericBox<List<Integer>>, String> pojo = new GenericTestClass<>();
        pojo.field1 = box;
        pojo.field2 = "GenericTestClass";

        assertEquals("{\"field1\":{\"strField\":\"IntegerListBox\",\"x\":[1,2]},\"field2\":\"GenericTestClass\"}",
                	defaultJsonb.toJson(pojo));
    }



    @Test
    public void testFunctional() {
        FunctionalInterface myFunction = new FunctionalInterface<String>() {

            private String value = "initValue";

            @Override
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        };

        assertEquals("{\"value\":\"initValue\"}", defaultJsonb.toJson(myFunction));
    }

    @Test
    public void testBoundedGenerics() {
        //bounded generics
        BoundedGenericClass<HashSet<Integer>, Circle> boundedGenericClass = new BoundedGenericClass<>();
        List<Shape> shapeList = new ArrayList<>();
        Circle circle = new Circle();
        circle.setRadius(2.5);
        shapeList.add(circle);
        boundedGenericClass.lowerBoundedList = shapeList;

        List<ColoredCircle> coloredCircles = new ArrayList<>();
        ColoredCircle coloredCircle = new ColoredCircle();
        coloredCircle.radius = 3.5;
        coloredCircle.color = "0,0,255";
        coloredCircles.add(coloredCircle);
        boundedGenericClass.upperBoundedList = coloredCircles;

        HashSet<Integer> intSet = new HashSet<>();
        intSet.add(3);
        boundedGenericClass.boundedSet = intSet;

        String expected = "{\"boundedSet\":[3],\"lowerBoundedList\":[{\"radius\":2.5}],\"upperBoundedList\":[{\"radius\":3.5,\"color\":\"0,0,255\"}]}";
        assertEquals(expected, defaultJsonb.toJson(boundedGenericClass));

        Jsonb localJsonb = JsonbBuilder.create(new JsonbConfig());
        BoundedGenericClass<HashSet<Integer>, Circle> result = localJsonb.fromJson(expected,
                new TestTypeToken<BoundedGenericClass<HashSet<Integer>, Circle>>(){}.getType());
        assertEquals(Circle.class, result.lowerBoundedList.get(0).getClass());
        assertEquals(Double.valueOf(2.5), ((Circle) result.lowerBoundedList.get(0)).getRadius());

        //There is no way of identifying precise class (ColoredCircle) during json unmarshalling.
        //Fields that are missing in upper bounds are skipped.
        assertEquals(Circle.class, result.upperBoundedList.get(0).getClass());
        assertEquals(Double.valueOf(3.5), result.upperBoundedList.get(0).getRadius());
        //If it was possible we could assert following, but it is not.
        //assertEquals("0,0,255", ((ColoredCircle) result.upperBoundedList.get(0)).color);
    }

    @Test
    public void testIncompatibleTypes() {
        //exception incompatible types
    	assertThrows(ClassCastException.class, () -> {
	        BoundedGenericClass<HashSet<Integer>, Circle> otherGeneric = defaultJsonb.fromJson("{\"boundedSet\":[3],\"lowerBoundedList\":[{\"radius\":2.5}]}",
	                new TestTypeToken<BoundedGenericClass<HashSet<Double>, Circle>>(){}.getType());
	        HashSet<Integer> otherIntSet = otherGeneric.boundedSet;
	        Integer intValue = otherIntSet.iterator().next();
    	});
    }

    @Test
    public void testMultiLevelGenericExtension() {
        MultiLevelExtendedGenericTestClass extended = new MultiLevelExtendedGenericTestClass();
        extended.field1 = "first";
        extended.field2 = 1;

        String expected = "{\"field1\":\"first\",\"field2\":1}";
        assertEquals(expected, defaultJsonb.toJson(extended));
        MultiLevelExtendedGenericTestClass result = defaultJsonb.fromJson(expected, MultiLevelExtendedGenericTestClass.class);
        assertEquals("first", result.field1);
        assertEquals(Integer.valueOf(1), result.field2);
    }

    @Test
    public void testGenericArray() {
        GenericArrayClass<Number, Integer> genericArrayClass = new GenericArrayClass<>();
        Number[] numbers = new Number[2];
        numbers[0] = 1;
        numbers[1] = BigDecimal.TEN;
        genericArrayClass.genericArray = numbers;

        Integer[] integers = new Integer[2];
        integers[0] = 1;
        integers[1] = 10;
        genericArrayClass.anotherGenericArray = integers;

        GenericTestClass<Number[], Integer[]> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = Arrays.copyOf(numbers, numbers.length);
        genericTestClass.field2 = Arrays.copyOf(integers, numbers.length);
        genericArrayClass.propagatedGenericArray = genericTestClass;

        String expected = "{\"anotherGenericArray\":[1,10],\"genericArray\":[1,10],\"propagatedGenericArray\":{\"field1\":[1,10],\"field2\":[1,10]}}";
        assertEquals(expected, defaultJsonb.toJson(genericArrayClass, new TestTypeToken<GenericArrayClass<Number,Integer>>(){}.getType()));
        GenericArrayClass<Number, Integer> result = defaultJsonb.fromJson(expected, new TestTypeToken<GenericArrayClass<Number, Integer>>(){}.getType());
        assertEquals(BigDecimal.ONE, result.genericArray[0]);
        assertEquals(BigDecimal.TEN, result.genericArray[1]);
        assertEquals(Integer.valueOf(1), result.anotherGenericArray[0]);
        assertEquals(Integer.valueOf(10), result.anotherGenericArray[1]);
        assertEquals(BigDecimal.ONE, result.propagatedGenericArray.field1[0]);
        assertEquals(BigDecimal.TEN, result.propagatedGenericArray.field1[1]);
        assertEquals(Integer.valueOf(1), result.propagatedGenericArray.field2[0]);
        assertEquals(Integer.valueOf(10), result.propagatedGenericArray.field2[1]);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallRawList() throws ParseException {
        List rawList = new ArrayList();
        final SimpleDateFormat ddMMyyyy = new SimpleDateFormat("ddMMyyyy");
        ddMMyyyy.setTimeZone(TimeZone.getTimeZone("Z"));
        rawList.add(ddMMyyyy.parse("24031981"));
        Box box = new Box();
        box.boxStr = "box string";
        box.crate = new Crate();
        box.crate.crateStr = "crate str";
        rawList.add(box);

        String result = defaultJsonb.toJson(rawList);
        assertEquals("[\"1981-03-24T00:00:00Z[UTC]\",{\"boxStr\":\"box string\",\"crate\":{\"crate_str\":\"crate str\"}}]", result);
    }

    @Test
    public void testMultipleBounds() {
        final LinkedList<String> list = new LinkedList<>(Arrays.asList("Test 1", "Test 2"));
        MultipleBoundsContainer<LinkedList<String>> container = new MultipleBoundsContainer<>();
        container.setInstance(new ArrayList<>());
        container.getInstance().add(list);

        final Type type = new TestTypeToken<MultipleBoundsContainer<LinkedList<String>>>() {
        }.getType();

        String jsonString = defaultJsonb.toJson(container, type);
        assertEquals("{\"instance\":[[\"Test 1\",\"Test 2\"]]}", jsonString);

        MultipleBoundsContainer<LinkedList<String>> result = defaultJsonb.fromJson(jsonString, type);

        assertEquals(container.getInstance(), result.getInstance());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeserializeIntoRaw() {

        GenericTestClass result = defaultJsonb.fromJson("{\"field1\":{\"val1\":\"abc\"},\"field2\":{\"val1\":\"def\"}}", GenericTestClass.class);
        assertEquals(((HashMap<String, ?>) result.getField1()).get("val1"), "abc");
        assertEquals(((HashMap<String, ?>) result.getField2()).get("val1"), "def");
    }

    @Test
    public void collectionWrapperTest() {
        CollectionWrapper<String> collectionWrapper = new CollectionWrapper<>();
        collectionWrapper.setCollection(new ArrayList<>());

        collectionWrapper.setWrappedCollection(new ArrayList<>());
        collectionWrapper.setWrappedMap(new HashMap<>());
        String s = defaultJsonb.toJson(collectionWrapper);
    }

    @Test
    public void multipleGenericLevels() {
        FinalMember member = new FinalMember();
        member.setName("Jason");
        FinalGenericWrapper concreteContainer = new FinalGenericWrapper();
        concreteContainer.setMember(member);

        String expected = "{\"member\":{\"name\":\"Jason\"}}";
        Jsonb jsonb = JsonbBuilder.create();
        assertEquals(expected, jsonb.toJson(concreteContainer));
        FinalGenericWrapper finalGenericWrapper = jsonb.fromJson(expected, FinalGenericWrapper.class);
        assertEquals(concreteContainer, finalGenericWrapper);
    }

    @Test
    public void lowerBoundTypeVariableInCollectionAttribute() throws Exception {
        
        Shape shape = new Shape();
        shape.setArea(5D);
        
        AnotherGenericTestClass<Integer, Shape> anotherGenericTestClass = new AnotherGenericTestClass<>();
        anotherGenericTestClass.field1 = 6;
        anotherGenericTestClass.field2 = shape;
        
        List<AnotherGenericTestClass<Integer, Shape>> asList = Arrays.asList(anotherGenericTestClass);
        
        Jsonb jsonb = JsonbBuilder.create();
        String toJson = jsonb.toJson(asList);
        
        Field field = LowerBoundTypeVariableWithCollectionAttributeClass.class.getDeclaredField("value");
        
        Type genericType = field.getGenericType();
        
        List<AnotherGenericTestClass<Integer, Shape>> fromJson = jsonb.fromJson(toJson, genericType);

        assertEquals(5, fromJson.get(0).field2.getArea());
        assertEquals(6, fromJson.get(0).field1);
        
    }

    @Test
    public void genericConstructorCreator() {
        final String expectedJson = "{\"value\":\"Test\"}";
        final ConstructorContainer<String> container = new ConstructorContainer<>("Test");

        assertEquals(expectedJson, defaultJsonb.toJson(container));
        assertEquals(container, defaultJsonb.fromJson(expectedJson, ConstructorContainer.class));
    }

    @Test
    public void genericStaticCreator() {
        final String expectedJson = "{\"value\":\"static\"}";
        final StaticCreatorContainer<String> container = StaticCreatorContainer.create("static");

        assertEquals(expectedJson, defaultJsonb.toJson(container));
        assertEquals(container, defaultJsonb.fromJson(expectedJson, StaticCreatorContainer.class));
    }

    @Test
    public void wildcardCollectionContainer() {
        final String expectedJson = "{\"collection\":{\"collection\":[{\"wrapped\":\"wrappedElement\"}]}}";
        final CollectionContainer collectionContainer = new CollectionContainer();
        final CollectionWrapper<CollectionElement<?>> collectionWrapper = new CollectionWrapper<>();
        final CollectionElement<String> wildcardType = new CollectionElement<>();
        wildcardType.setWrapped("wrappedElement");
        final Collection<CollectionElement<?>> list = List.of(wildcardType);
        collectionWrapper.setCollection(list);
        collectionContainer.setCollection(collectionWrapper);

        assertEquals(expectedJson, defaultJsonb.toJson(collectionContainer));
        final CollectionContainer result = defaultJsonb.fromJson(expectedJson, CollectionContainer.class);
        assertEquals(collectionContainer, result);
    }
    
    public interface FunctionalInterface<T> {
        T getValue();
    }

    public static class ExtendsBigDecimal extends BigDecimal {

        public ExtendsBigDecimal(String val) {
            super(val);
        }
    }

}

