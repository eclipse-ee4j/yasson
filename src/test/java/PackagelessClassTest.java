/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com) - initial implementation
 ******************************************************************************/
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Tests the serialization/deserialization of a class that has no package
 *
 * Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class PackagelessClassTest {
    private Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testSerialization() throws Exception {
        PackagelessModel packagelessClass = new PackagelessModel(12, "Hello World!");

        String expected = "{\"intValue\":12,\"stringValue\":\"Hello World!\"}";
        assertEquals(expected, jsonb.toJson(packagelessClass));
    }

    @Test
    public void testDeSerialization() throws Exception {
        PackagelessModel packagelessClass = new PackagelessModel(12, "Hello World!");

        String input = "{\"intValue\":12,\"stringValue\":\"Hello World!\"}";
        PackagelessModel packagelessModel = jsonb.fromJson(input, PackagelessModel.class);
        assertEquals(packagelessModel.getIntValue(), 12);
        assertEquals(packagelessModel.getStringValue(), "Hello World!");
    }

}
