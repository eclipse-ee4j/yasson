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

import org.eclipse.yasson.defaultmapping.basic.model.BooleanModel;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Tests serialization and deserialization of boolean values
 * <p>
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class BooleanTest {
    private Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testBooleanSerialization() throws Exception {
        BooleanModel booleanModel = new BooleanModel(true, false);

        String expected = "{\"field1\":true,\"field2\":false}";
        assertEquals(expected, jsonb.toJson(booleanModel));
    }

    @Test
    public void testBooleanDeserializationFromBooleanAsStringValue() throws Exception {
        BooleanModel booleanModel = jsonb.fromJson("{\"field1\":\"true\",\"field2\":\"true\"}", BooleanModel.class);
        assertEquals(booleanModel.field1, true);
        assertEquals(booleanModel.field2, true);
    }

    @Test
    public void testBooleanDeserializationFromBooleanRawValue() throws Exception {
        BooleanModel booleanModel = jsonb.fromJson("{\"field1\":false,\"field2\":false}", BooleanModel.class);
        assertEquals(booleanModel.field1, false);
        assertEquals(booleanModel.field2, false);
    }
}
