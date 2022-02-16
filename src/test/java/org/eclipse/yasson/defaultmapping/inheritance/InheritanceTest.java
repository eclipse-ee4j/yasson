/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.inheritance;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.yasson.defaultmapping.generics.model.PropagatedGenericClass;
import org.eclipse.yasson.defaultmapping.inheritance.model.PartialOverride;
import org.eclipse.yasson.defaultmapping.inheritance.model.PropertyOrderSecond;
import org.eclipse.yasson.defaultmapping.inheritance.model.SecondLevel;
import org.eclipse.yasson.defaultmapping.inheritance.model.generics.ExtendsExtendsPropagatedGenericClass;
import org.eclipse.yasson.defaultmapping.inheritance.model.generics.ExtendsPropagatedGenericClass;
import org.eclipse.yasson.defaultmapping.inheritance.model.generics.ImplementsGenericInterfaces;
import org.eclipse.yasson.defaultmapping.inheritance.model.generics.SecondLevelGeneric;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests inheritance model marshalling / unmarshalling
 *
 * Tests property order, method overloading, generic type ({@link java.lang.reflect.TypeVariable}) resolving for unmarshalling.
 *
 * @author Roman Grigoriadi
 */
public class InheritanceTest {

    @Test
    public void testBasicInheritance() throws Exception {
        SecondLevel secondLevel = new SecondLevel();
        secondLevel.setInFirstLevel("IN_FIRST_LEVEL");
        secondLevel.setInSecondLevel("IN_SECOND_LEVEL");
        secondLevel.setInZeroOverriddenInFirst("IN_ZERO_OVERRIDDEN_IN_FIRST");

        String json = "{\"inZeroOverriddenInFirst\":\"IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inFirstLevel\":\"IN_FIRST_LEVEL\",\"inSecondLevel\":\"IN_SECOND_LEVEL\"}";
        assertEquals(json, defaultJsonb.toJson(secondLevel));

        SecondLevel result = defaultJsonb.fromJson(json, SecondLevel.class);
        assertEquals("IN_FIRST_LEVEL", result.getInFirstLevel());
        assertEquals("IN_SECOND_LEVEL", result.getInSecondLevel());
        assertEquals("IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());

    }

    @Test
    public void testBasicGenericInheritance() {
        SecondLevelGeneric<Number, Short, String> secondLevelGeneric = new SecondLevelGeneric<>();
        secondLevelGeneric.setInSecondLevel(BigDecimal.TEN);
        secondLevelGeneric.setInFirstLevel((short) 255);
        secondLevelGeneric.setInZeroOverriddenInFirst("IN_ZERO_OVERRIDDEN_IN_FIRST");
        secondLevelGeneric.setInZero("IN_ZERO");

        String json = "{\"inZero\":\"IN_ZERO\",\"inFirstLevel\":255,\"inZeroOverriddenInFirst\":\"IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inSecondLevel\":10}";
        assertEquals(json, defaultJsonb.toJson(secondLevelGeneric));

        SecondLevelGeneric<Number, Short, String> result = defaultJsonb.fromJson(json, new TestTypeToken<SecondLevelGeneric<Number, Short, String>>(){}.getType());
        assertEquals(BigDecimal.TEN, result.getInSecondLevel());
        assertEquals(Short.valueOf("255"), result.getInFirstLevel());
        assertEquals("IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());
        assertEquals("IN_ZERO", result.getInZero());
    }

    @Test
    public void testPropagatedGenericInheritance() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");

        GenericTestClass<List<String>, BigDecimal> genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.TEN;

        List<GenericTestClass<List<String>, BigDecimal>> listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        GenericTestClass<String, BigDecimal> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "GENERIC_STRING";
        genericTestClass.field2 = BigDecimal.ONE;

        ExtendsExtendsPropagatedGenericClass underTest = new ExtendsExtendsPropagatedGenericClass();
        underTest.genericTestClass = genericTestClass;
        underTest.genericList = listWithGenerics;

        String json = "{\"genericList\":[{\"field1\":[\"first\",\"second\"],\"field2\":10}],\"genericTestClass\":{\"field1\":\"GENERIC_STRING\",\"field2\":1}}";
        assertEquals(json, defaultJsonb.toJson(underTest));

        ExtendsExtendsPropagatedGenericClass result = defaultJsonb.fromJson(json, ExtendsExtendsPropagatedGenericClass.class);
        assertEquals(GenericTestClass.class, result.genericList.get(0).getClass());
        assertEquals("first", result.genericList.get(0).field1.get(0));
        assertEquals("second", result.genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.TEN, result.genericList.get(0).field2);
        assertEquals(GenericTestClass.class, result.genericTestClass.getClass());
        assertEquals("GENERIC_STRING", result.genericTestClass.field1);
        assertEquals(BigDecimal.ONE, result.genericTestClass.field2);
    }

    @Test
    public void testPropagatedGenericInheritance1() throws Exception {
        List<String> stringList = new ArrayList<>();
        stringList.add("first");
        stringList.add("second");

        GenericTestClass<List<String>, BigDecimal> genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.TEN;

        List<GenericTestClass<List<String>, BigDecimal>> listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        GenericTestClass<String, BigDecimal> genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "SECOND_LEVEL_GENERIC_STRING";
        genericTestClass.field2 = BigDecimal.ONE;

        PropagatedGenericClass<String, BigDecimal> propagatedGenericClass = new PropagatedGenericClass<>();
        propagatedGenericClass.genericList = listWithGenerics;
        propagatedGenericClass.genericTestClass = genericTestClass;


        stringList = new ArrayList<>();
        stringList.add("third");
        stringList.add("fourth");

        genericInList = new GenericTestClass<>();
        genericInList.field1 = stringList;
        genericInList.field2 = BigDecimal.ZERO;

        listWithGenerics = new ArrayList<>();
        listWithGenerics.add(genericInList);

        genericTestClass = new GenericTestClass<>();
        genericTestClass.field1 = "FIRST_LEVEL_GENERIC_STRING";
        genericTestClass.field2 = new BigDecimal("11");

        ExtendsPropagatedGenericClass<String, BigDecimal> extendsPropagatedGenericClass = new ExtendsPropagatedGenericClass<>();
        extendsPropagatedGenericClass.genericList = listWithGenerics;
        extendsPropagatedGenericClass.genericTestClass = genericTestClass;

        SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String> secondLevelGeneric = new SecondLevelGeneric<>();
        secondLevelGeneric.setInSecondLevel(propagatedGenericClass);
        secondLevelGeneric.setInFirstLevel(extendsPropagatedGenericClass);
        secondLevelGeneric.setInZeroOverriddenInFirst("STRING_IN_ZERO_OVERRIDDEN_IN_FIRST");
        secondLevelGeneric.setInZero("IN_ZERO");

        String json = "{\"inZero\":\"IN_ZERO\",\"inFirstLevel\":{\"genericList\":[{\"field1\":[\"third\",\"fourth\"],\"field2\":0}],\"genericTestClass\":{\"field1\":\"FIRST_LEVEL_GENERIC_STRING\",\"field2\":11}},\"inZeroOverriddenInFirst\":\"STRING_IN_ZERO_OVERRIDDEN_IN_FIRST\",\"inSecondLevel\":{\"genericList\":[{\"field1\":[\"first\",\"second\"],\"field2\":10}],\"genericTestClass\":{\"field1\":\"SECOND_LEVEL_GENERIC_STRING\",\"field2\":1}}}";

        final Type runtimeType = new TestTypeToken<SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String>>(){}.getType();
        assertEquals(json, defaultJsonb.toJson(secondLevelGeneric, runtimeType));
        SecondLevelGeneric<PropagatedGenericClass<String, BigDecimal>, ExtendsPropagatedGenericClass<String, BigDecimal>, String> result =
        		defaultJsonb.fromJson(json, runtimeType);

        assertEquals("first", result.getInSecondLevel().genericList.get(0).field1.get(0));
        assertEquals("second", result.getInSecondLevel().genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.TEN, result.getInSecondLevel().genericList.get(0).field2);
        assertEquals("SECOND_LEVEL_GENERIC_STRING", result.getInSecondLevel().genericTestClass.field1);
        assertEquals(BigDecimal.ONE, result.getInSecondLevel().genericTestClass.field2);

        assertEquals("third", result.getInFirstLevel().genericList.get(0).field1.get(0));
        assertEquals("fourth", result.getInFirstLevel().genericList.get(0).field1.get(1));
        assertEquals(BigDecimal.ZERO, result.getInFirstLevel().genericList.get(0).field2);
        assertEquals("FIRST_LEVEL_GENERIC_STRING", result.getInFirstLevel().genericTestClass.field1);
        assertEquals(new BigDecimal("11"), result.getInFirstLevel().genericTestClass.field2);

        assertEquals("STRING_IN_ZERO_OVERRIDDEN_IN_FIRST", result.getInZeroOverriddenInFirst());
        assertEquals("IN_ZERO", result.getInZero());
    }

    @Test
    public void testInterfaceGenericInheritance() throws Exception {
        ImplementsGenericInterfaces<String, Integer> implementsGenericInterfaces = new ImplementsGenericInterfaces<>();

        implementsGenericInterfaces.setGenericValue("GENERIC_VALUE");
        implementsGenericInterfaces.setAnotherGenericValue(255);

        String json = "{\"anotherGenericValue\":255,\"genericValue\":\"GENERIC_VALUE\"}";
        assertEquals(json, defaultJsonb.toJson(implementsGenericInterfaces));

        ImplementsGenericInterfaces<String, Integer> result = defaultJsonb.fromJson(json, new TestTypeToken<ImplementsGenericInterfaces<String, Integer>>(){}.getType());
        assertEquals("GENERIC_VALUE", result.getGenericValue());
        assertEquals(Integer.valueOf(255), result.getAnotherGenericValue());
    }

    @Test
    public void testPartialOverride() {
        PartialOverride partialOverride = new PartialOverride();
        partialOverride.setIntValue(5);
        partialOverride.setStrValue("abc");
        String json = defaultJsonb.toJson(partialOverride);
        assertEquals("{\"intValue\":5,\"strValue\":\"abc\"}", json);

        PartialOverride result = defaultJsonb.fromJson("{\"intValue\":5,\"strValue\":\"abc\"}", PartialOverride.class);
        assertEquals(5, result.getIntValue());
        assertEquals("abc", result.getStrValue());
    }

    @Test
    public void testPropOrderPartiallyOverriddenProperty() {
        PropertyOrderSecond pojo = new PropertyOrderSecond();
        pojo.setZero("ZERO");
        pojo.setZeroPartiallyOverriddenInFirst("ZERO_PARTIALLY_OVERRIDDEN_IN_FIRST");
        pojo.setZeroOverriddenInSecond("ZERO_OVERRIDDEN_IN_SECOND");
        pojo.setFirst("FIRST");
        pojo.setSecond("SECOND");

        String result = defaultJsonb.toJson(pojo);
        assertEquals("{\"zero\":\"ZERO\",\"zeroPartiallyOverriddenInFirst\":\"ZERO_PARTIALLY_OVERRIDDEN_IN_FIRST\",\"first\":\"FIRST\",\"second\":\"SECOND\",\"zeroOverriddenInSecond\":\"ZERO_OVERRIDDEN_IN_SECOND\"}",
                result);
    }

    @Test
    public void testInheritanceSerialization() {
        AnimalWrapper animalWrapper = new AnimalWrapper();
        animalWrapper.animal = new Dog();
        //Just initialize serializer cache for Animal and Dog
        defaultJsonb.toJson(animalWrapper);

        //Check if the Dog instance is dynamically resolved even though Dog serializer has been created before
        DogWrapper dogWrapper = new DogWrapper();
        dogWrapper.dog = new Dog();
        assertEquals("{\"dog\":{\"isDog\":true}}", defaultJsonb.toJson(dogWrapper));
        dogWrapper.dog = new SmallDog();
        assertEquals("{\"dog\":{\"isDog\":true,\"isSmallDog\":true}}", defaultJsonb.toJson(dogWrapper));

    }

    public static class AnimalWrapper {

        public Animal animal;

    }

    public static class DogWrapper {

        public Dog dog;

    }

    public static class Animal {

    }

    public static class Dog extends Animal {
        public boolean isDog = true;
    }

    public static class SmallDog extends Dog {
        public boolean isSmallDog = true;
    }
}
