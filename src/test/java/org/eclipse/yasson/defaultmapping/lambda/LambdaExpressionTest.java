/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.lambda;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

/**
 * Test marshalling objects generated using lambda expressions.
 *
 * Scenarios test lambda-generated objects serialization against concrete class objects serialization. It is thus
 * assumed that concrete class behaviour is tested successfully elsewhere.
 */
public class LambdaExpressionTest {

    @Test
    public void testMarshallFunctionalInterface() {
        String name = "WALL-E";
        Addressable control = new Robot(name);
        Addressable lambda = () -> name;
        assertEquals(defaultJsonb.toJson(control), defaultJsonb.toJson(lambda));
    }

    @Test
    public void testMarshallFunctionalInterfaceWithDefaultProperties() {
        String name = "Cheshire";
        Pet control = new Cat(name);
        Pet lambda = () -> name;
        assertEquals(defaultJsonb.toJson(control), defaultJsonb.toJson(lambda));
    }
}
