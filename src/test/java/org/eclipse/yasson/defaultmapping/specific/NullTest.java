/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2020 Payara Foundation and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.defaultmapping.specific.model.StreetWithPrimitives;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.specific.model.Street;

import java.util.List;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class NullTest {

    @Test
    public void testSetsNullIntoFields() {
        String json = "{\"name\":null,\"number\":null}";

        Street result = defaultJsonb.fromJson(json, Street.class);
        //these have default initialization value
        assertNull(result.getName());
        assertNull(result.getNumber());
    }
//
//    @Test
//    public void testSetsNullToPrimitives() {
//        String json = "{\"name\":null,\"number\":null}";
//
//        StreetWithPrimitives result = defaultJsonb.fromJson(json, StreetWithPrimitives.class);
//        //these have default initialization value
//        assertNull(result.getName());
//        assertNull(result.getNumber());
//    }

    @Test
    public void testDeserializeNull() {
        assertNull(defaultJsonb.fromJson("null", Object.class));
    }

    @Test
    public void testDeserializeNullPojo() {
        assertNull(defaultJsonb.fromJson("null", Street.class));
    }

    @Test
    public void testDeserializeNullList() {
        assertNull(defaultJsonb.fromJson("null", new TestTypeToken<List<Integer>>() {}.getType()));
    }

    @Test
    public void testDeserializeNullMap() {
        assertNull(defaultJsonb.fromJson("null", new TestTypeToken<Map<String, Street>>() {}.getType()));
    }

}
