/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.Animal;
import org.eclipse.yasson.customization.model.Dog;
import org.eclipse.yasson.customization.model.ImplementationClassPojo;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.util.HashMap;

import static org.eclipse.yasson.YassonConfig.USER_TYPE_MAPPING;

public class ImplementationClassTest {

    @Test
    public void testAnnotatedImplementation() {
        ImplementationClassPojo pojo = new ImplementationClassPojo();
        Animal dog = new Dog("Bulldog");
        pojo.setAnimal(dog);
        String expected = "{\"animal\":{\"dogProperty\":\"Bulldog\"}}";
        String json = defaultJsonb.toJson(pojo);

        assertEquals(expected, json);
        ImplementationClassPojo result = defaultJsonb.fromJson(expected, ImplementationClassPojo.class);
        assertTrue(result.getAnimal() instanceof Dog);
        assertEquals("Bulldog", ((Dog)result.getAnimal()).getDogProperty());
    }

    @Test
    public void testJsonbConfigUserImplementation() {
        HashMap<Class, Class> userMapping = new HashMap<>();
        userMapping.put(Animal.class, Dog.class);
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(USER_TYPE_MAPPING, userMapping));
        Animal animal = new Dog("Bulldog");
        String expected = "{\"dogProperty\":\"Bulldog\"}";
        String json = jsonb.toJson(animal);

        assertEquals(expected, json);

        Dog result = (Dog) jsonb.fromJson("{\"dogProperty\":\"Bulldog\"}", Animal.class);
        assertEquals("Bulldog", result.getDogProperty());
    }
}
