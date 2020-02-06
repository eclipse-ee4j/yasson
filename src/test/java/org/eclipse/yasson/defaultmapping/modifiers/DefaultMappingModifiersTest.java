/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.defaultmapping.modifiers.model.Person;
import org.eclipse.yasson.defaultmapping.modifiers.model.FieldModifiersClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.MethodModifiersClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.PrivateConstructorClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.ProtectedConstructorClass;

import jakarta.json.bind.JsonbException;

/**
 * Test access modifiers for default mapping.
 *
 * @author Roman Grigoriadi
 */
public class DefaultMappingModifiersTest {

    @Test
    public void testFieldModifiers() {
        FieldModifiersClass fieldModifiersClass = new FieldModifiersClass();
        assertEquals("{\"finalString\":\"FINAL_STRING\"}", defaultJsonb.toJson(fieldModifiersClass));
        FieldModifiersClass result = defaultJsonb.fromJson("{\"finalString\":\"FINAL_STRING\",\"staticString\":\"STATIC_STRING\",\"transientString\":\"TRANSIENT_STRING\"}", FieldModifiersClass.class);
        //no setter throwing illegal has been called.
    }

    @Test
    public void testMethodModifiers() {
        MethodModifiersClass methodModifiers = new MethodModifiersClass();
        methodModifiers.publicFieldWithoutMethods = "WITHOUT_METHODS";

        String validJson = "{\"getterWithoutFieldValue\":\"GETTER_WITHOUT_FIELD\",\"publicFieldWithoutMethods\":\"WITHOUT_METHODS\"}";
        assertEquals(validJson, defaultJsonb.toJson(methodModifiers));

        MethodModifiersClass result = defaultJsonb.fromJson("{\"publicFieldWithPrivateMethods\":\"value\"}", MethodModifiersClass.class);
        assertNull(result.publicFieldWithPrivateMethods);

        result = defaultJsonb.fromJson(validJson, MethodModifiersClass.class);
        assertEquals("WITHOUT_METHODS", result.publicFieldWithoutMethods);
    }

    @Test
    public void testConstructorModifiers() {
        try{
            ProtectedConstructorClass instance = defaultJsonb.fromJson("{\"randomField\":\"test\"}", ProtectedConstructorClass.class);
            assertEquals(instance.randomField, "test");
        } catch (JsonbException e){
            fail("No exception should be thrown for protected constructor");
            throw e;
        }
        try {
        	defaultJsonb.fromJson("{\"randomField\":\"test\"}", PrivateConstructorClass.class);
            fail("Exception should have been thrown");
        }catch (JsonbException e){
            assertTrue(e.getMessage().endsWith("Can't create instance"));
        }
    }

    @Test
    public void testMultipleInstancesOfSameType() {
        Person person = new Person();
        Person personTwo = new Person();
        person.name = "Person 1";
        personTwo.name = "Person 2";
        person.child = personTwo;

        assertEquals("{\"child\":{\"name\":\"Person 2\"},\"name\":\"Person 1\"}", bindingJsonb.toJson(person));
    }
}
