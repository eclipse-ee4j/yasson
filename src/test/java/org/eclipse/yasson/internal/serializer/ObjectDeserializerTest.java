/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import jakarta.json.bind.JsonbException;

public class ObjectDeserializerTest {

    @Test
    public void testGetInstanceExceptionShouldContainClassNameOnMissingConstructor() {
        assertThrows(JsonbException.class, 
                () -> defaultJsonb.fromJson("{\"key\":\"value\"}", DummyDeserializationClass.class),
                DummyDeserializationClass.class::getName);
    }

    public static class DummyDeserializationClass {
        private String key;

        public DummyDeserializationClass(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
