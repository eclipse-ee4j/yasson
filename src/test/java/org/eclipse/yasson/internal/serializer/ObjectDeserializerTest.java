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

package org.eclipse.yasson.internal.serializer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.adapters.AdaptersTest.StringAdapter;

import static org.eclipse.yasson.Jsonbs.*;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.annotation.JsonbSubtype;

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


    
    /**
     * Test for: https://github.com/eclipse-ee4j/yasson/issues/589
     */
    @Test
    public void testNestedUnmappedProperty() {
        String json = "{\"inner\":{\"id\":123,\"_type\":\"derivationA\","
                + "\"unmapped\":{\"x\":9,\"y\":[9,8,7]},\"name\":\"abc\"}}";
        Outer obj = assertDoesNotThrow(() -> defaultJsonb.fromJson(json, Outer.class));
        assertEquals(123L, obj.inner.id);
        assertEquals("abc", obj.inner.name);
    }

    // a base class
    @JsonbTypeInfo(key = "_type", value = @JsonbSubtype(type = InnerBase.class, alias = "derivationA"))
    public static class InnerBase {
        public Long id;
        public String name;
    }

    // derivation of the base class
    public class Derivation extends InnerBase {}

    // an arbitrary 'outer' root element
    public static class Outer {
        public InnerBase inner;
    }
}
