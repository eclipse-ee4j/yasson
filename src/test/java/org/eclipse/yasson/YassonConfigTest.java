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

package org.eclipse.yasson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Test;

/**
 * Tests that the names of configuration fields in {@link YassonConfig} do not change.
 */
public class YassonConfigTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testFailOnUnknownPropertiesUnchanged() {
        assertEquals("jsonb.fail-on-unknown-properties", YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES);
        assertEquals("jsonb.fail-on-unknown-properties", YassonProperties.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testUserTypeMappingUnchanged() {
        assertEquals("jsonb.user-type-mapping", YassonConfig.USER_TYPE_MAPPING);
        assertEquals("jsonb.user-type-mapping", YassonProperties.USER_TYPE_MAPPING);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testZeroTimeDefaultingUnchanged() {
        assertEquals("jsonb.zero-time-defaulting", YassonConfig.ZERO_TIME_PARSE_DEFAULTING);
        assertEquals("jsonb.zero-time-defaulting", YassonProperties.ZERO_TIME_PARSE_DEFAULTING);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testNullRootSerializerUnchanged() {
        assertEquals("yasson.null-root-serializer", YassonConfig.NULL_ROOT_SERIALIZER);
        assertEquals("yasson.null-root-serializer", YassonProperties.NULL_ROOT_SERIALIZER);
    }
    
    @Test
    public void testEagerInitClassesUnchanged() {
        assertEquals("yasson.eager-parse-classes", YassonConfig.EAGER_PARSE_CLASSES);
    }
    
}
