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

package org.eclipse.yasson.defaultmapping.modifiers;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.Assertions;
import org.eclipse.yasson.defaultmapping.modifiers.model.ChildOfPackagePrivateParent;

/**
 * Test access modifiers on classes
 *
 * @author David Kral
 */
public class ClassModifiersTest {

    @Test
    public void testPackagePrivateParent() {
        ChildOfPackagePrivateParent child = new ChildOfPackagePrivateParent();
        child.id = 1;
        child.name = "SomeName";
        String json = defaultJsonb.toJson(child);
        assertEquals("{\"id\":1,\"name\":\"SomeName\"}", json);
        ChildOfPackagePrivateParent result = defaultJsonb.fromJson(json, ChildOfPackagePrivateParent.class);
        assertEquals(child.id, result.id);
        assertEquals(child.name, result.name);
    }


    class NestedPackageParent {
        public int id;
    }

    public class NestedPackageChild extends NestedPackageParent {
        public String name;
    }

    @Test
    public void testNestedPackagePrivateParent() {
        NestedPackageChild child = new NestedPackageChild();
        child.id = 1;
        child.name = "SomeName";
        Assertions.shouldFail(() -> defaultJsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException") &&
                       msg.contains("Error accessing field 'id' declared in " +
                                    "'class org.eclipse.yasson.defaultmapping.modifiers.ClassModifiersTest$NestedPackageParent'"));
    }

    private class NestedPrivateParent {
        public int id;
    }

    public class NestedPrivateChild extends NestedPrivateParent {
        public String name;
    }

    @Test
    public void testNestedPrivateParent() {
        NestedPrivateChild child = new NestedPrivateChild();
        child.id = 1;
        child.name = "SomeName";
        Assertions.shouldFail(() -> defaultJsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException") &&
                       msg.contains("Error accessing field 'id' declared in " +
                                    "'class org.eclipse.yasson.defaultmapping.modifiers.ClassModifiersTest$NestedPrivateParent'"));
    }


    static class NestedStaticPackageParent {
        public int id;
    }

    public static class NestedStaticPackageChild extends NestedStaticPackageParent {
        public String name;
    }

    @Test
    public void testNestedStaticPackagePrivateParent() {
        NestedStaticPackageChild child = new NestedStaticPackageChild();
        child.id = 1;
        child.name = "SomeName";
        Assertions.shouldFail(() -> defaultJsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException") &&
                       msg.contains("Error accessing field 'id' declared in " +
                                    "'class org.eclipse.yasson.defaultmapping.modifiers.ClassModifiersTest$NestedStaticPackageParent'"));
    }

    private static class NestedStaticPrivateParent {
        public int id;
    }

    public static class NestedStaticPrivateChild extends NestedStaticPrivateParent {
        public String name;
    }

    @Test
    public void testNestedStaticPrivateParent() {
        NestedStaticPrivateChild child = new NestedStaticPrivateChild();
        child.id = 1;
        child.name = "SomeName";
        Assertions.shouldFail(() -> defaultJsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException") &&
                       msg.contains("Error accessing field 'id' declared in " +
                                   "'class org.eclipse.yasson.defaultmapping.modifiers.ClassModifiersTest$NestedStaticPrivateParent'"));
    }
}
