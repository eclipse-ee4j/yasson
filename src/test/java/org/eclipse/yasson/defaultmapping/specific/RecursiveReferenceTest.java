/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Jorge Bescos Gascon
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.specific;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.Jsonbs;
import org.junit.jupiter.api.Test;

public class RecursiveReferenceTest {

    @Test
    public void testSerializeRecursiveReference() {
        Recursive recursive = new Recursive();
        recursive.ref = recursive;
        try {
            Jsonbs.defaultJsonb.toJson(recursive);
            fail("Fail is expected");
        } catch (JsonbException e) {
            assertEquals(
                    "Unable to serialize property 'ref' from org.eclipse.yasson.defaultmapping.specific.RecursiveReferenceTest.Recursive",
                    e.getMessage());
            assertEquals(
                    "Recursive reference has been found in class class org.eclipse.yasson.defaultmapping.specific.RecursiveReferenceTest$Recursive.",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void testSerializeRepeatedInstance() {
        Recursive recursive = new Recursive();
        recursive.ref = new Recursive();
        String result = Jsonbs.defaultJsonb.toJson(Arrays.asList(recursive, recursive));
        assertEquals("[{\"ref\":{\"val\":\"test\"},\"val\":\"test\"},{\"ref\":{\"val\":\"test\"},\"val\":\"test\"}]",
                result);
    }

    @Test
    public void testDeserializeRecursiveReference() {
        Recursive recursive = Jsonbs.defaultJsonb.fromJson("{\"ref\":{\"val\":\"test\"},\"val\":\"test\"}",
                Recursive.class);
        assertNotEquals(recursive.ref, recursive);
    }

    @Test
    public void testDeserialize2ReferencesSameObject() {
        A a = new A();
        B b = new B();
        a.ref1 = b;
        a.ref2 = b;
        String result = Jsonbs.defaultJsonb.toJson(a);
        assertEquals("{\"ref1\":{\"prop\":\"foo\"},\"ref2\":{\"prop\":\"foo\"}}", result);
    }

    public static class A {
        public B ref1;
        public B ref2;
    }

    public static class B {
        public String prop = "foo";
    }

    public static class Recursive {

        private Recursive ref;
        private String val = "test";

        public Recursive getRef() {
            return ref;
        }

        public void setRef(Recursive ref) {
            this.ref = ref;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

    }

}
