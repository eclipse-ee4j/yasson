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
 *     Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.basic;

import org.junit.jupiter.api.*;
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
        assertEquals(false, booleanModel.field1);
        assertEquals(false, booleanModel.field2);
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
}
