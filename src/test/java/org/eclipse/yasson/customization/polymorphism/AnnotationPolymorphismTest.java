/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization.polymorphism;

import java.time.LocalDate;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

import org.eclipse.yasson.Jsonbs;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for verification of proper polymorphism handling based on annotation.
 */
public class AnnotationPolymorphismTest {

    public static final String ARRAY_EXPECTED = "[{\"@type\":\"dog\",\"isDog\":true},{\"@type\":\"cat\",\"isCat\":true},"
            + "{\"@type\":\"dog\",\"isDog\":true}]";

    @Test
    public void testBasicSerialization() {
        Dog dog = new Dog();
        assertThat(Jsonbs.defaultJsonb.toJson(dog), is("{\"@type\":\"dog\",\"isDog\":true}"));
        Cat cat = new Cat();
        assertThat(Jsonbs.defaultJsonb.toJson(cat), is("{\"@type\":\"cat\",\"isCat\":true}"));
    }

    @Test
    public void testBasicDeserialization() {
        Animal dog = Jsonbs.defaultJsonb.fromJson("{\"@type\":\"dog\",\"isDog\":false}", Animal.class);
        assertThat(dog, instanceOf(Dog.class));
        assertThat(((Dog) dog).isDog, is(false));
        Animal cat = Jsonbs.defaultJsonb.fromJson("{\"@type\":\"cat\",\"isCat\":false}", Animal.class);
        assertThat(cat, instanceOf(Cat.class));
        assertThat(((Cat) cat).isCat, is(false));
    }

    @Test
    public void testExactTypeDeserialization() {
        Dog dog = Jsonbs.defaultJsonb.fromJson("{\"isDog\":false}", Dog.class);
        assertThat(dog.isDog, is(false));
        dog = Jsonbs.defaultJsonb.fromJson("{\"@type\":\"dog\", \"isDog\":false}", Dog.class);
        assertThat(dog.isDog, is(false));
    }

    @Test
    public void testUnknownAliasDeserialization() {
        JsonbException exception = assertThrows(JsonbException.class,
                                                () -> Jsonbs.defaultJsonb.fromJson("{\"@type\":\"rat\",\"isDog\":false}",
                                                                                   Animal.class));
        assertThat(exception.getMessage(), startsWith("Unknown alias \"rat\" of the type org.eclipse.yasson.customization."
                                                              + "polymorphism.AnnotationPolymorphismTest$Animal. Known aliases: ["));
    }

    @Test
    public void testUnknownAliasSerialization() {
        Rat rat = new Rat();
        assertThat(Jsonbs.defaultJsonb.toJson(rat), is("{\"isRat\":true}"));
    }

    @Test
    public void testCreatorDeserialization() {
        SomeDateType creator = Jsonbs.defaultJsonb
                .fromJson("{\"@dateType\":\"constructor\",\"localDate\":\"26-02-2021\"}", SomeDateType.class);
        assertThat(creator, instanceOf(DateConstructor.class));
    }

    @Test
    public void testArraySerialization() {
        Animal[] animals = new Animal[] {new Dog(), new Cat(), new Dog()};
        assertThat(Jsonbs.defaultJsonb.toJson(animals), is(ARRAY_EXPECTED));
    }

    @Test
    public void testArrayDeserialization() {
        Animal[] deserialized = Jsonbs.defaultJsonb.fromJson(ARRAY_EXPECTED, Animal[].class);
        assertThat(deserialized.length, is(3));
        assertThat(deserialized[0], instanceOf(Dog.class));
        assertThat(deserialized[1], instanceOf(Cat.class));
        assertThat(deserialized[2], instanceOf(Dog.class));
    }

    @JsonbTypeInfo({
            @JsonbSubtype(alias = "dog", type = Dog.class),
            @JsonbSubtype(alias = "cat", type = Cat.class)
    })
    public interface Animal {

    }

    public static class Dog implements Animal {

        public boolean isDog = true;

    }

    public static class Cat implements Animal {

        public boolean isCat = true;

    }

    public static class Rat implements Animal {

        public boolean isRat = true;

    }

    @JsonbTypeInfo(key = "@dateType", value = {
            @JsonbSubtype(alias = "constructor", type = DateConstructor.class)
    })
    public interface SomeDateType {

    }

    public static final class DateConstructor implements SomeDateType {

        public LocalDate localDate;

        @JsonbCreator
        public DateConstructor(@JsonbProperty("localDate") @JsonbDateFormat(value = "dd-MM-yyyy", locale = "nl-NL") LocalDate localDate) {
            this.localDate = localDate;
        }

    }

}
