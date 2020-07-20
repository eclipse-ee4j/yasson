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

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import java.util.Arrays;

/**
 * Tests pretty print to JSONP propagation
 *
 * @author Roman Grigoriadi
 */
public class PrettyPrintTest {

    @Test
    public void testPrettyPrint() {
        assertEquals("[\n    \"first\",\n    \"second\"\n]", formattingJsonb.toJson(Arrays.asList("first", "second")));
    }

    @Test
    public void testPrettyPrintFalse() {
        assertEquals("[\"first\",\"second\"]", defaultJsonb.toJson(Arrays.asList("first", "second")));
    }
}
