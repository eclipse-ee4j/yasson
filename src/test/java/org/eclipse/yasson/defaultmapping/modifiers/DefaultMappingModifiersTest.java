/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.defaultmapping.modifiers;

import org.eclipse.yasson.defaultmapping.modifiers.model.Person;
import org.eclipse.yasson.defaultmapping.modifiers.model.FieldModifiersClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.MethodModifiersClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.PrivateConstructorClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.ProtectedConstructorClass;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import static org.junit.Assert.*;

/**
 * Test access modifiers for default mapping.
 *
 * @author Roman Grigoriadi
 */
public class DefaultMappingModifiersTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testFieldModifiers() {
        FieldModifiersClass fieldModifiersClass = new FieldModifiersClass();
        assertEquals("{\"finalString\":\"FINAL_STRING\"}", jsonb.toJson(fieldModifiersClass));
        FieldModifiersClass result = jsonb.fromJson("{\"finalString\":\"FINAL_STRING\",\"staticString\":\"STATIC_STRING\",\"transientString\":\"TRANSIENT_STRING\"}", FieldModifiersClass.class);
        //no setter throwing illegal has been called.
    }

    @Test
    public void testMethodModifiers() {
        MethodModifiersClass methodModifiers = new MethodModifiersClass();
        methodModifiers.publicFieldWithoutMethods = "WITHOUT_METHODS";

        String validJson = "{\"getterWithoutFieldValue\":\"GETTER_WITHOUT_FIELD\",\"publicFieldWithoutMethods\":\"WITHOUT_METHODS\"}";
        assertEquals(validJson, jsonb.toJson(methodModifiers));

        MethodModifiersClass result = jsonb.fromJson("{\"publicFieldWithPrivateMethods\":\"value\"}", MethodModifiersClass.class);
        assertNull(result.publicFieldWithPrivateMethods);

        result = jsonb.fromJson(validJson, MethodModifiersClass.class);
        assertEquals("WITHOUT_METHODS", result.publicFieldWithoutMethods);

    }

    @Test
    public void testConstructorModifiers() {
        try{
            ProtectedConstructorClass instance = jsonb.fromJson("{\"randomField\":\"test\"}", ProtectedConstructorClass.class);
            assertEquals(instance.randomField, "test");
        } catch (JsonbException e){
            fail("No exception should be thrown for protected constructor");
            throw e;
        }
        try {
            jsonb.fromJson("{\"randomField\":\"test\"}", PrivateConstructorClass.class);
            fail("Exception should have been thrown");
        }catch (JsonbException e){
            assertTrue(e.getMessage().endsWith("Can't create instance"));
        }
    }

    @Test
    public void testMultipleInstancesOfSameType() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        Person person = new Person();
        Person personTwo = new Person();
        person.name = "Person 1";
        personTwo.name = "Person 2";
        person.child = personTwo;

        assertEquals("{\"child\":{\"name\":\"Person 2\"},\"name\":\"Person 1\"}",jsonb.toJson(person));
    }

}
