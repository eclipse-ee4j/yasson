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
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.modifiers;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import org.eclipse.yasson.Assertions;
import org.eclipse.yasson.defaultmapping.modifiers.model.ChildOfPackagePrivateParent;
import org.eclipse.yasson.defaultmapping.modifiers.model.FieldModifiersClass;

/**
 * Test access modifiers on classes
 *
 * @author David Kral
 */
public class ClassModifiersTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testPackagePrivateParent() {
        ChildOfPackagePrivateParent child = new ChildOfPackagePrivateParent();
        child.id = 1;
        child.name = "SomeName";
        String json = jsonb.toJson(child);
        assertEquals("{\"id\":1,\"name\":\"SomeName\"}", json);
        ChildOfPackagePrivateParent result = jsonb.fromJson(json, ChildOfPackagePrivateParent.class);
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
        Assertions.shouldFail(() -> jsonb.toJson(child),
                msg -> msg.contains("Unable to serialize property 'id'") &&
                msg.contains("java.lang.IllegalAccessException")); 
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
        Assertions.shouldFail(() -> jsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException"));
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
        Assertions.shouldFail(() -> jsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException"));
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
        Assertions.shouldFail(() -> jsonb.toJson(child),
                msg -> msg.contains("java.lang.IllegalAccessException"));
    }

}
