/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

/**
 * Tests the serialization/deserialization of a class that has no package.
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class PackagelessClassTest {

    @Test
    public void testSerialization() throws Exception {
        PackagelessModel packagelessClass = new PackagelessModel(12, "Hello World!");

        String expected = "{\"intValue\":12,\"stringValue\":\"Hello World!\"}";
        assertEquals(expected, defaultJsonb.toJson(packagelessClass));
    }

    @Test
    public void testDeSerialization() throws Exception {
        PackagelessModel packagelessClass = new PackagelessModel(12, "Hello World!");

        String input = "{\"intValue\":12,\"stringValue\":\"Hello World!\"}";
        PackagelessModel packagelessModel = defaultJsonb.fromJson(input, PackagelessModel.class);
        assertEquals(packagelessModel.getIntValue(), 12);
        assertEquals(packagelessModel.getStringValue(), "Hello World!");
    }
}
