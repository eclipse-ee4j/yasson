/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.eclipse.yasson.YassonProperties.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.eclipse.yasson.YassonProperties.NULL_ROOT_SERIALIZER;
import static org.eclipse.yasson.YassonProperties.USER_TYPE_MAPPING;
import static org.eclipse.yasson.YassonProperties.ZERO_TIME_PARSE_DEFAULTING;

/**
 * Tests that the names of configuration fields in {@link YassonProperties} do not change.
 *
 * @author Simulant (nfaupel.dev@gmail.com)
 */
public class YassonPropertiesTest {

    @Test
    public void testFailOnUnknownProperties() {
        assertEquals("jsonb.fail-on-unknown-properties", FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void testUserTypeMapping() {
        assertEquals("jsonb.user-type-mapping", USER_TYPE_MAPPING);
    }

    @Test
    public void testZeroTimeDefaulting() {
        assertEquals("jsonb.zero-time-defaulting", ZERO_TIME_PARSE_DEFAULTING);
    }

    @Test
    public void testNullRootSerializer() {
        assertEquals("yasson.null-root-serializer", NULL_ROOT_SERIALIZER);
    }
}
