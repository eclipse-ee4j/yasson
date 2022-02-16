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

package org.eclipse.yasson.defaultmapping.basic;

import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.defaultmapping.basic.model.BooleanModel;

/**
 * Tests serialization and deserialization of boolean values.
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class BooleanTest {

    @Test
    public void testBooleanSerialization() throws Exception {
        BooleanModel booleanModel = new BooleanModel(true, false);

        String expected = "{\"field1\":true,\"field2\":false}";
        assertEquals(expected, defaultJsonb.toJson(booleanModel));
    }

    @Test
    public void testBooleanDeserializationFromBooleanAsStringValue() throws Exception {
        BooleanModel booleanModel = defaultJsonb.fromJson("{\"field1\":\"true\",\"field2\":\"true\"}", BooleanModel.class);
        assertEquals(true, booleanModel.field1);
        assertEquals(true, booleanModel.field2);
    }

    @Test
    public void testBooleanDeserializationFromBooleanRawValue() throws Exception {
        BooleanModel booleanModel = defaultJsonb.fromJson("{\"field1\":false,\"field2\":false}", BooleanModel.class);
        assertThat(booleanModel.field1, is(false));
        assertThat(booleanModel.field2, is(false));
    }

    @Test
    public void testNakedBooleans() {
        assertEquals(true, defaultJsonb.fromJson("true", boolean.class));
        assertEquals(true, defaultJsonb.fromJson("true", Boolean.class));
        assertEquals(false, defaultJsonb.fromJson("false", boolean.class));
        assertEquals(false, defaultJsonb.fromJson("false", Boolean.class));
        
        assertEquals("true", defaultJsonb.toJson(true, boolean.class));
        assertEquals("false", defaultJsonb.toJson(Boolean.FALSE, Boolean.class));
    }
    
    //Fix for issue #390
    @Test
    public void testBooleanArrays() {
        assertArrayEquals(new boolean[] {true, false}, defaultJsonb.fromJson("[true, false]", boolean[].class));
        assertArrayEquals(new Boolean[] {true, false}, defaultJsonb.fromJson("[true, false]", Boolean[].class));
        
        assertEquals("[true,false]", defaultJsonb.toJson(new boolean[] {true, false}));
        assertEquals("[true,false]", defaultJsonb.toJson(new Boolean[] {true, false}));
    }
}
