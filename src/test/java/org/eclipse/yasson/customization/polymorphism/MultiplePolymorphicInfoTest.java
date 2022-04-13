/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * TODO javadoc
 */
public class MultiplePolymorphicInfoTest {

    private static final Jsonb JSONB = JsonbBuilder.create();

    @Test
    public void testMultiplePolymorphicInfoPropertySerialization() {
        String expected = "{\"@something\":\"animal\",\"@animal\":\"dog\",\"@dogRace\":\"labrador\",\"isLabrador\":true}";
        Labrador labrador = new Labrador();
        assertThat(JSONB.toJson(labrador), is(expected));
    }

    @Test
    public void testMultiplePolymorphicInfoPropertyDeserialization() {
        String json = "{\"@something\":\"animal\",\"@animal\":\"dog\",\"@dogRace\":\"labrador\",\"isLabrador\":true}";
        assertThat(JSONB.fromJson(json, Labrador.class), instanceOf(Labrador.class));
    }

    @Test
    public void testPolymorphicParentInstanceSerialization() {
        String expected = "{\"@type\":\"area\",\"name\":\"North America\",\"population\":600000000}";
        Area northAmerica = new Area();
        northAmerica.name = "North America";
        northAmerica.population = 600000000;
        assertThat(JSONB.toJson(northAmerica), is(expected));
    }

    @Test
    public void testPolymorphicParentInstanceDeserialization() {
        String json = "{\"@type\":\"area\",\"name\":\"North America\",\"population\":600000000}";
        assertThat(JSONB.fromJson(json, Location.class), instanceOf(Area.class));
    }

    @JsonbTypeInfo(key = "@something", value = {
            @JsonbSubtype(alias = "animal", type = Animal.class)
    })
    public interface Something { }

    @JsonbTypeInfo(key = "@animal", value = {
            @JsonbSubtype(alias = "dog", type = Dog.class)
    })
    public interface Animal extends Something {
    }

    @JsonbTypeInfo(key = "@dogRace", value = {
            @JsonbSubtype(alias = "labrador", type = Labrador.class)
    })
    public interface Dog extends Animal {
    }

    public static class Labrador implements Dog {

        public boolean isLabrador = true;

    }

    @JsonbTypeInfo({
            @JsonbSubtype(alias = "area", type = Area.class)
    })
    public interface Location {
    }

    @JsonbTypeInfo(key = "@area", value = {
            @JsonbSubtype(alias = "city", type = City.class),
            @JsonbSubtype(alias = "state", type = State.class)
    })
    public static class Area implements Location {
        public String name;
        public long population;
    }

    public static class City extends Area {
        public String state;
    }

    public static class State extends Area {
        public String capital;
    }

}
