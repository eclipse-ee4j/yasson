/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.specific;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Specific standard Java SE types tests: {@link BigDecimal}, {@link BigInteger}, {@link URL}, {@link URI}.
 *
 * @author Dmitry Kornilov
 */
public class SpecificTest {
    @Test
    public void testMarshallBigDecimal() {
        assertEquals("{\"value\":100}", bindingJsonb.toJson(new ScalarValueWrapper<>(BigDecimal.valueOf(100L))));
        assertEquals("{\"value\":100.1}", bindingJsonb.toJson(new ScalarValueWrapper<>(BigDecimal.valueOf(100.1D))));
    }

    @Test
    public void testMarshallBigInteger() {
        assertEquals("{\"value\":100}", bindingJsonb.toJson(new ScalarValueWrapper<>(BigInteger.valueOf(100))));
    }

    @Test
    public void testMarshallUri() throws URISyntaxException {
        assertEquals("{\"value\":\"http://www.oracle.com\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(new URI("http://www.oracle.com"))));
    }

    @Test
    public void testMarshallUrl() throws MalformedURLException {
        assertEquals("{\"value\":\"http://www.oracle.com\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(new URL("http://www.oracle.com"))));
    }
}
