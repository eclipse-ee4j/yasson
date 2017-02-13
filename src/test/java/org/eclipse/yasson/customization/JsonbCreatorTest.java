/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.customization.model.CreatorConstructorPojo;
import org.eclipse.yasson.customization.model.CreatorFactoryMethodPojo;
import org.eclipse.yasson.customization.model.CreatorIncompatibleTypePojo;
import org.eclipse.yasson.customization.model.CreatorMultipleDeclarationErrorPojo;
import org.eclipse.yasson.customization.model.CreatorWithoutJsonbProperty;
import org.eclipse.yasson.customization.model.CreatorWithoutJsonbProperty1;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import java.math.BigDecimal;

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

    @Test
    public void testCreatorWithoutJsonbParameters() {
        final CreatorWithoutJsonbProperty result = JsonbBuilder.create().fromJson("{\"s1\":\"abc\", \"arg1\":\"def\"}", CreatorWithoutJsonbProperty.class);
        Assert.assertEquals("abc", result.getPar1());
        Assert.assertEquals("def", result.getPar2());
    }

    @Test
    public void testCreatorWithoutJsonbParameters1() {
        final CreatorWithoutJsonbProperty1 result = JsonbBuilder.create().fromJson("{\"arg0\":\"abc\", \"s2\":\"def\"}", CreatorWithoutJsonbProperty1.class);
        Assert.assertEquals("abc", result.getPar1());
        Assert.assertEquals("def", result.getPar2());
    }
}
