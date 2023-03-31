/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization.polymorphism;

import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

import org.eclipse.yasson.Jsonbs;
import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for verification of proper polymorphism handling based on annotation.
 */
public class NestedPolymorphismTest {

    /**
     * 1st test for: https://github.com/eclipse-ee4j/yasson/issues/589
     * <p>(deserialization of nested polymorphic and unmapped properties)
     */
    @Test
    public void testNestedUnmappedProperty() {
        String json = "{\"inner\":{\"id\":123,\"@type\":\"derivationA\","
                + "\"unmapped\":{\"x\":9,\"y\":[9,8,7]},\"name\":\"abc\"}}";
        Outer obj = assertDoesNotThrow(() -> defaultJsonb.fromJson(json, Outer.class));
        assertEquals(123L, obj.inner.id);
        assertEquals("abc", obj.inner.name);
    }

    // a base class
    @JsonbTypeInfo(key = "@type", value =
            @JsonbSubtype(type = InnerBase.class, alias = "derivationA"))
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

    /**
     * 2nd test for: https://github.com/eclipse-ee4j/yasson/issues/589
     * <p>(deserialization of multiple nested polymorphic properties)
     */
    @Test
    public void testNestedDeserialization() {
        String json = "{\"@type\":\"Pets\",\"pet1\":{\"@type\":\"Cat\",\"name\":\"kitty\"}"
                + ",\"pet2\":{\"@type\":\"Dog\",\"name\":\"puppy\"}}";
        final Animals animals = Jsonbs.defaultJsonb.fromJson(json, Animals.class);
        assertThat(animals, instanceOf(Pets.class));
        assertNotNull(((Pets) animals).pet1, "Empty 'pet1' property");
        assertEquals("kitty", ((Cat) ((Pets) animals).pet1).name, "First pet has invalid name");
        assertNotNull(((Pets) animals).pet2, "Empty 'pet2' property");
        assertThat("Invalid pet nr 2", ((Pets) animals).pet2, instanceOf(Dog.class));
    }
    
    @JsonbTypeInfo(key = "@type", value = {
            @JsonbSubtype(alias = "Dog", type = Dog.class),
            @JsonbSubtype(alias = "Cat", type = Cat.class)
    })
    public interface Pet {
        public String getType();
    }

    public static class Dog implements Pet {

        public String name;

        @Override
        public String getType() {
            return "Dog";
        }
    }

    public static class Cat implements Pet {

        public String name;

        @Override
        public String getType() {
            return "Cat";
        }
    }

    @JsonbTypeInfo(key = "@type", value = {
            @JsonbSubtype(alias = "Pets", type = Pets.class),
            @JsonbSubtype(alias = "Fishes", type = Fishes.class)
        })
    public interface Animals {

    }

    public static class Pets implements Animals {
        public Pet pet1;
        public Pet pet2;
    }

    public static class Fishes implements Animals {

    }

}
