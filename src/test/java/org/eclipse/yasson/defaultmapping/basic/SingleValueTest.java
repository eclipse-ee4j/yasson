/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author David Kral
 */
public class SingleValueTest {

    @Test
    public void testMarshallPrimitives() {
        // String
        assertEquals("\"some_string\"", bindingJsonb.toJson("some_string"));

        // Character
        assertEquals("\"\uFFFF\"", bindingJsonb.toJson('\uFFFF'));

        // Byte
        assertEquals("1", bindingJsonb.toJson((byte)1));

        // Short
        assertEquals("1", bindingJsonb.toJson((short)1));

        // Integer
        assertEquals("1", bindingJsonb.toJson(1));

        // Long
        assertEquals("5", bindingJsonb.toJson(5L));

        // Float
        assertEquals("1.2", bindingJsonb.toJson(1.2f));

        // Double
        assertEquals("1.2", bindingJsonb.toJson(1.2));

        // BigInteger
        assertEquals("1", bindingJsonb.toJson(new BigInteger("1")));

        // BigDecimal
        assertEquals("1.2", bindingJsonb.toJson(new BigDecimal("1.2")));

        // Number
        assertEquals("1.2", bindingJsonb.toJson(1.2));

        // Boolean true
        assertEquals("true", bindingJsonb.toJson(true));

        // Boolean false
        assertEquals("false", bindingJsonb.toJson(false));

        assertEquals("1", bindingJsonb.toJson(1));

        // null
        assertEquals("null", bindingJsonb.toJson(null));
    }

    @Test
    public void testSingleValue() {
        assertEquals("5", bindingJsonb.toJson(5));

        Jsonb jsonb = new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true)).build();
        try {
            jsonb.toJson(5);
            fail();
        } catch (JsonbException exception){
            assertEquals(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE), exception.getMessage());
        }
    }
}
