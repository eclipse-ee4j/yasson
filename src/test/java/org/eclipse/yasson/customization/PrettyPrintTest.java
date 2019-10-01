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
        assertEquals("\n[\n    \"first\",\n    \"second\"\n]", formattingJsonb.toJson(Arrays.asList("first", "second")));
    }

    @Test
    public void testPrettyPrintFalse() {
        assertEquals("[\"first\",\"second\"]", defaultJsonb.toJson(Arrays.asList("first", "second")));
    }
}
