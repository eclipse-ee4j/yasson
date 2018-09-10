/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.customization;

import org.eclipse.yasson.customization.model.*;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class JsonbCreatorTest {

    @Test
    public void testRootConstructor() {
        String json = "{\"str1\":\"abc\",\"str2\":\"def\",\"bigDec\":25}";
        final Jsonb jsonb = JsonbBuilder.create();
        CreatorConstructorPojo pojo = jsonb.fromJson(json, CreatorConstructorPojo.class);
        assertEquals("abc", pojo.str1);
        assertEquals("def", pojo.str2);
        assertEquals(new BigDecimal("25"), pojo.bigDec);
    }

    @Test
    public void testRootFactoryMethod() {
        String json = "{\"par1\":\"abc\",\"par2\":\"def\",\"bigDec\":25}";
        final Jsonb jsonb = JsonbBuilder.create();
        CreatorFactoryMethodPojo pojo = jsonb.fromJson(json, CreatorFactoryMethodPojo.class);
        assertEquals("abc", pojo.str1);
        assertEquals("def", pojo.str2);
        assertEquals(new BigDecimal("25"), pojo.bigDec);
    }

    @Test
    public void testRootCreatorWithInnerCreator() {
        String json = "{\"str1\":\"abc\",\"str2\":\"def\",\"bigDec\":25, \"innerFactoryCreator\":{\"par1\":\"inn1\",\"par2\":\"inn2\",\"bigDec\":11}}";
        final Jsonb jsonb = JsonbBuilder.create();
        CreatorConstructorPojo pojo = jsonb.fromJson(json, CreatorConstructorPojo.class);
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
            JsonbBuilder.create().fromJson("{\"s1\":\"abc\"}", CreatorIncompatibleTypePojo.class);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Return type of creator"));
        }
    }

    @Test
    public void testMultipleCreatorsError() {
        try {
            JsonbBuilder.create().fromJson("{\"s1\":\"abc\"}", CreatorMultipleDeclarationErrorPojo.class);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("More than one @JsonbCreator"));
        }
    }

    @Test(expected = JsonbException.class)
    public void testCreatorWithoutJsonbParameters1() {
        //arg2 is missing in json document
        JsonbBuilder.create().fromJson("{\"arg0\":\"abc\", \"s2\":\"def\"}", CreatorWithoutJsonbProperty1.class);
    }

    @Test
    public void testCreatorWithoutJavabeanProperty() {
        final CreatorWithoutJavabeanProperty result = JsonbBuilder.create().fromJson("{\"s1\":\"abc\", \"s2\":\"def\"}", CreatorWithoutJavabeanProperty.class);
        Assert.assertEquals("abcdef", result.getStrField());

    }

    @Test(expected = JsonbException.class)
    public void testPackagePrivateCreator() {
        final CreatorPackagePrivateConstructor result = JsonbBuilder.create().fromJson(
                "{\"strVal\":\"abc\", \"intVal\":5}", CreatorPackagePrivateConstructor.class);
    }

    @Test
    public void testLocalizedConstructor() {
        String json = "{\"localDate\":\"05-09-2017\"}";
        DateConstructor result = JsonbBuilder.create().fromJson(json, DateConstructor.class);
        Assert.assertEquals(LocalDate.of(2017, 9, 5), result.localDate);
    }

    @Test
    public void testLocalizedConstructorMergedWithProperty() {
        String json = "{\"localDate\":\"05-09-2017\"}";
        DateConstructorMergedWithProperty result = JsonbBuilder.create().fromJson(json, DateConstructorMergedWithProperty.class);
        Assert.assertEquals(LocalDate.of(2017, 9, 5), result.localDate);
    }

    @Test
    public void testLocalizedFactoryParameter() {
        String json = "{\"number\":\"10.000\"}";
        FactoryNumberParam result = JsonbBuilder.create().fromJson(json, FactoryNumberParam.class);
        Assert.assertEquals(BigDecimal.TEN, result.number);
    }

    @Test
    public void testLocalizedFactoryParameterMergedWithProperty() {
        String json = "{\"number\":\"10.000\"}";
        FactoryNumberParamMergedWithProperty result = JsonbBuilder.create().fromJson(json, FactoryNumberParamMergedWithProperty.class);
        Assert.assertEquals(BigDecimal.TEN, result.number);
    }

    @Test
    public void testCorrectCreatorParameterNames() {
        String json = "{\"string\":\"someText\", \"someParam\":null }";
        ParameterNameTester result = JsonbBuilder.create().fromJson(json, ParameterNameTester.class);
        Assert.assertEquals("someText", result.name);
        Assert.assertNull(result.secondParam);
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
