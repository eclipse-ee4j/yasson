/*******************************************************************************
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.Animal;
import org.eclipse.yasson.customization.model.Dog;
import org.eclipse.yasson.customization.model.ImplementationClassPojo;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.HashMap;

import static org.eclipse.yasson.YassonProperties.USER_TYPE_MAPPING;

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
