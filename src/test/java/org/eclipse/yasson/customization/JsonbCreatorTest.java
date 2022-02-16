/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;

import org.eclipse.yasson.customization.model.CreatorConstructorPojo;
import org.eclipse.yasson.customization.model.CreatorFactoryMethodPojo;
import org.eclipse.yasson.customization.model.CreatorIncompatibleTypePojo;
import org.eclipse.yasson.customization.model.CreatorMultipleDeclarationErrorPojo;
import org.eclipse.yasson.customization.model.CreatorPackagePrivateConstructor;
import org.eclipse.yasson.customization.model.CreatorWithoutJavabeanProperty;
import org.eclipse.yasson.customization.model.CreatorWithoutJsonbProperty1;
import org.eclipse.yasson.customization.model.ParameterNameTester;
import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Roman Grigoriadi
 */
public class JsonbCreatorTest {

    @Test
    public void testRootConstructor() {
        String json = "{\"str1\":\"abc\",\"str2\":\"def\",\"bigDec\":25}";
        CreatorConstructorPojo pojo = defaultJsonb.fromJson(json, CreatorConstructorPojo.class);
        assertEquals("abc", pojo.str1);
        assertEquals("def", pojo.str2);
        assertEquals(new BigDecimal("25"), pojo.bigDec);
    }

    @Test
    public void testRootFactoryMethod() {
        String json = "{\"par1\":\"abc\",\"par2\":\"def\",\"bigDec\":25}";
        CreatorFactoryMethodPojo pojo = defaultJsonb.fromJson(json, CreatorFactoryMethodPojo.class);
        assertEquals("abc", pojo.str1);
        assertEquals("def", pojo.str2);
        assertEquals(new BigDecimal("25"), pojo.bigDec);
    }

    @Test
    public void testRootCreatorWithInnerCreator() {
        String json = "{\"str1\":\"abc\",\"str2\":\"def\",\"bigDec\":25, \"innerFactoryCreator\":{\"par1\":\"inn1\",\"par2\":\"inn2\",\"bigDec\":11}}";
        CreatorConstructorPojo pojo = defaultJsonb.fromJson(json, CreatorConstructorPojo.class);
        assertEquals("abc", pojo.str1);
        assertEquals("def", pojo.str2);
        assertEquals(new BigDecimal("25"), pojo.bigDec);

        assertEquals("inn1", pojo.innerFactoryCreator.str1);
        assertEquals("inn2", pojo.innerFactoryCreator.str2);
        assertEquals(new BigDecimal("11"), pojo.innerFactoryCreator.bigDec);
    }

    @Test
    public void testIncompatibleFactoryMethodReturnType() {
        try {
        	defaultJsonb.fromJson("{\"s1\":\"abc\"}", CreatorIncompatibleTypePojo.class);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Return type of creator"));
        }
    }

    @Test
    public void testMultipleCreatorsError() {
        try {
        	defaultJsonb.fromJson("{\"s1\":\"abc\"}", CreatorMultipleDeclarationErrorPojo.class);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("More than one @JsonbCreator"));
        }
    }

    @Test
    public void testCreatorWithoutJsonbParameters1() {
        //arg2 is missing in json document
        CreatorWithoutJsonbProperty1 object = defaultJsonb.fromJson("{\"arg0\":\"abc\", \"s2\":\"def\"}",
                                                                    CreatorWithoutJsonbProperty1.class);
        assertThat(object.getPar1(), is("abc"));
        assertThat(object.getPar2(), is("def"));
        assertThat(object.getPar3(), is((byte) 0));
    }

    @Test
    public void testCreatorWithoutJavabeanProperty() {
        final CreatorWithoutJavabeanProperty result = defaultJsonb.fromJson("{\"s1\":\"abc\", \"s2\":\"def\"}", CreatorWithoutJavabeanProperty.class);
        assertEquals("abcdef", result.getStrField());

    }

    @Test
    public void testPackagePrivateCreator() {
    	assertThrows(JsonbException.class, () -> defaultJsonb.fromJson("{\"strVal\":\"abc\", \"intVal\":5}", CreatorPackagePrivateConstructor.class));
    }

    @Test
    public void testLocalizedConstructor() {
        String json = "{\"localDate\":\"05-09-2017\"}";
        DateConstructor result = defaultJsonb.fromJson(json, DateConstructor.class);
        assertEquals(LocalDate.of(2017, 9, 5), result.localDate);
    }

    @Test
    public void testLocalizedConstructorMergedWithProperty() {
        String json = "{\"localDate\":\"05-09-2017\"}";
        DateConstructorMergedWithProperty result = defaultJsonb.fromJson(json, DateConstructorMergedWithProperty.class);
        assertEquals(LocalDate.of(2017, 9, 5), result.localDate);
    }

    @Test
    public void testLocalizedFactoryParameter() {
        String json = "{\"number\":\"10.000\"}";
        FactoryNumberParam result = defaultJsonb.fromJson(json, FactoryNumberParam.class);
        assertEquals(BigDecimal.TEN, result.number);
    }

    @Test
    public void testLocalizedFactoryParameterMergedWithProperty() {
        String json = "{\"number\":\"10.000\"}";
        FactoryNumberParamMergedWithProperty result = defaultJsonb.fromJson(json, FactoryNumberParamMergedWithProperty.class);
        assertEquals(BigDecimal.TEN, result.number);
    }

    @Test
    public void testCorrectCreatorParameterNames() {
        String json = "{\"string\":\"someText\", \"someParam\":null }";
        ParameterNameTester result = defaultJsonb.fromJson(json, ParameterNameTester.class);
        assertEquals("someText", result.name);
        assertNull(result.secondParam);
    }

    @Test
    public void testGenericCreatorParameter() throws Exception {
        final String json = "{\"persons\": [{\"name\": \"name1\"}]}";
        Persons persons = defaultJsonb.fromJson(json, Persons.class);
        assertEquals(1, persons.hiddenPersons.size());
        assertEquals("name1", persons.hiddenPersons.iterator().next().getName());
    }

    public static final class Persons {

        Set<Person> hiddenPersons;

        private Persons(Set<Person> persons) {
            this.hiddenPersons = persons;
        }

        @JsonbCreator
        public static Persons wrap(@JsonbProperty("persons") Set<Person> persons) {
            return new Persons(persons);
        }

        public Set<Person> getPersons() {
            return null;
        }
    }

    public static final class Person {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static final class DateConstructor {
        public LocalDate localDate;

        @JsonbCreator
        public DateConstructor(@JsonbProperty("localDate") @JsonbDateFormat(value = "dd-MM-yyyy", locale = "nl-NL") LocalDate localDate) {
            this.localDate = localDate;
        }

    }

    public static final class DateConstructorMergedWithProperty {
        @JsonbDateFormat(value = "dd-MM-yyyy", locale = "cs-CZ")
        public LocalDate localDate;

        @JsonbCreator
        public DateConstructorMergedWithProperty(@JsonbProperty("localDate") LocalDate localDate) {
            this.localDate = localDate;
        }

    }

    public static final class FactoryNumberParam {
        public BigDecimal number;

        private FactoryNumberParam(BigDecimal number) {
            this.number = number;
        }

        @JsonbCreator
        public static FactoryNumberParam createInstance(
                @JsonbProperty("number") @JsonbNumberFormat(value = "000.000", locale = "en-us")
                        BigDecimal number) {
            return new FactoryNumberParam(number);
        }

    }

    public static final class FactoryNumberParamMergedWithProperty {

        @JsonbNumberFormat(value = "000.000", locale = "en-us")
        public BigDecimal number;

        private FactoryNumberParamMergedWithProperty(BigDecimal number) {
            this.number = number;
        }

        @JsonbCreator
        public static FactoryNumberParamMergedWithProperty createInstance(@JsonbProperty("number") BigDecimal number) {
            return new FactoryNumberParamMergedWithProperty(number);
        }

    }


}
