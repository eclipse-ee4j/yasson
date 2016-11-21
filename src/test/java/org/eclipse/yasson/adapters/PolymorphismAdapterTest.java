/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.adapters;


import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.model.PolymorphismAdapter;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Testing polymorphic adapters.
 *
 * @author Roman Grigoriadi
 */
public class PolymorphismAdapterTest {

    public static class AnimalAdapter extends PolymorphismAdapter<Animal> {
        public AnimalAdapter() {
            super(Dog.class, Cat.class);
        }
    }

    public static class Animal {
        public String name;
    }

    public static class Dog extends Animal {
        public String dogProperty;
    }

    public static class Cat extends Animal {
        public String catProperty;
    }

    public static class Pojo {
        public Animal animal;

        public List<Animal> listOfAnimals = new ArrayList<>();
    }


    @Test
    public void testPolymorphic() {
        Dog ralph = new Dog();
        ralph.name = "Ralph";
        ralph.dogProperty = "Property of a Dog.";

        Dog cassidy = new Dog();
        cassidy.name = "Cassidy";
        cassidy.dogProperty = "Property of a Dog.";

        Cat cat = new Cat();
        cat.name = "Harris";
        cat.catProperty = "Property of a Cat.";

        Pojo pojo = new Pojo();
        pojo.animal = ralph;
        pojo.listOfAnimals.add(cat);
        pojo.listOfAnimals.add(cassidy);

        final String expected = "{\"animal\":{\"className\":\"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Dog\",\"instance\":{\"name\":\"Ralph\",\"dogProperty\":\"Property of a Dog.\"}},\"listOfAnimals\":[{\"className\":\"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Cat\",\"instance\":{\"name\":\"Harris\",\"catProperty\":\"Property of a Cat.\"}},{\"className\":\"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Dog\",\"instance\":{\"name\":\"Cassidy\",\"dogProperty\":\"Property of a Dog.\"}}]}";

        JsonbConfig config = new JsonbConfig();
        config.withAdapters(new AnimalAdapter());
        Jsonb jsonb = JsonbBuilder.create(config);
        String json = jsonb.toJson(pojo);

        assertEquals(expected, json);

        Pojo result = jsonb.fromJson(expected, Pojo.class);
        assertEquals(Dog.class, result.animal.getClass());
        assertEquals("Ralph", result.animal.name);
        assertEquals("Property of a Dog.", ((Dog)result.animal).dogProperty);
        assertEquals(2, result.listOfAnimals.size());
        assertEquals(Cat.class, result.listOfAnimals.get(0).getClass());
        assertEquals("Harris", result.listOfAnimals.get(0).name);
        assertEquals("Property of a Cat.", ((Cat)result.listOfAnimals.get(0)).catProperty);
        assertEquals(Dog.class, result.listOfAnimals.get(1).getClass());
        assertEquals("Cassidy", result.listOfAnimals.get(1).name);
        assertEquals("Property of a Dog.", ((Dog)result.listOfAnimals.get(1)).dogProperty);

        /*
        Produces following JSON:
        {
            "animal":{
                "className":"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Dog",
                "instance":{
                    "name":"Ralph",
                    "dogProperty":"Property of a Dog."
                }
            },
            "listOfAnimals":[
                {
                    "className":"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Cat",
                    "instance":{
                        "name":"Harris",
                        "catProperty":"Property of a Cat."
                    }
                },
                {
                    "className":"org.eclipse.yasson.adapters.PolymorphismAdapterTest$Dog",
                    "instance":{
                        "name":"Cassidy",
                        "dogProperty":"Property of a Dog."
                    }
                }
            ]
        }
         */
    }

    /**
     * Tests class load not allowed exception.
     */
    @Test()
    public void testClassLoadNotAllowed() {
        final String expected = "{\"animal\":{\"className\":\"PolymorphismAdapterTest$Dog\",\"instance\":{\"name\":\"Ralph\",\"dogProperty\":\"Property of a Dog.\"}},\"listOfAnimals\":[{\"className\":\"PolymorphismAdapterTest$Cat\",\"instance\":{\"name\":\"Harris\",\"catProperty\":\"Property of a Cat.\"}},{\"className\":\"PolymorphismAdapterTest$Dog\",\"instance\":{\"name\":\"Ralph\",\"dogProperty\":\"Property of a Dog.\"}}]}";

        JsonbConfig config = new JsonbConfig();
        config.withAdapters(new PolymorphismAdapter<Animal>(Cat.class){}); // Dog.class missing and allowedClasses is not empty.
        Jsonb jsonb = JsonbBuilder.create(config);

        try {
            jsonb.fromJson(expected, Pojo.class);
            fail("Should throw class load not allowed");
        } catch (JsonbException e) {
            Assert.assertEquals(Messages.getMessage(MessageKeys.CLASS_LOAD_NOT_ALLOWED, "PolymorphismAdapterTest$Dog"), e.getCause().getMessage());
        }
    }
}
