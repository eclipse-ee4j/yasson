/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.jsonpsubstitution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.Assertions;
import org.eclipse.yasson.TestTypeToken;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PreinstantiatedJsonpTest {

    public static class Dog {

        public String name;
        public int age;
        public boolean goodDog = true;

        public Dog() {
        }

        public Dog(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Dog:\nname: " + name + "\nage: " + age + "\ngoodDog: " + goodDog;
        }
    }

    public static class Wrapper<T> {
        private T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    private final String EXPECTED_JSON = "{\"age\":4,\"goodDog\":true,\"name\":\"Falco\",\"suffix\":\"Best dog ever!\"}";

    private final String WRAPPED_JSON = "{\"instance\":" + EXPECTED_JSON + "}";

    private Dog dog = new Dog("Falco", 4);

    @Test
    public void testPreinstantiatedJsonGeneratorAndParser() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Best dog ever!", out);
        bindingYassonJsonb.toJson(dog, generator);
        generator.close();

        assertEquals(EXPECTED_JSON, new String(out.toByteArray()));

        ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED_JSON.getBytes());
        JsonParser parser = new AdaptedJsonParser((value) -> {
            if ("Falco".equals(value)) {
                return value + ", a best dog ever!";
            }
            return value;
        }, in);
        Dog result = bindingYassonJsonb.fromJson(parser, Dog.class);

        assertEquals("Falco, a best dog ever!", result.name);
        assertEquals(4, result.age);
        assertTrue(result.goodDog);
    }

    @Test
    public void testJsonParserAdvancedToCustomPosition() {
        ByteArrayInputStream in = new ByteArrayInputStream(WRAPPED_JSON.getBytes());
        JsonParser parser = new AdaptedJsonParser((value) -> {
            if ("Falco".equals(value)) {
                return value + ", a best dog ever!";
            }
            return value;
        }, in);

        parser.next(); //START_OBJECT
        parser.next(); //"instance" KEY

        Dog result = bindingYassonJsonb.fromJson(parser, Dog.class);

        parser.next(); //END_OJBECT

        assertEquals("Falco, a best dog ever!", result.name);
        assertEquals(4, result.age);
        assertTrue(result.goodDog);
    }

    @Test
    public void testGeneratorWrappedWithUserInteraction() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Best dog ever!", out);

        generator.writeStartObject();
        generator.writeKey("instance");
        bindingYassonJsonb.toJson(dog, generator);
        generator.writeEnd();
        generator.close();

        assertEquals(WRAPPED_JSON, new String(out.toByteArray()));
    }

    @Test
    public void testInvalidJsonParserAdvancedToCustomPosition() {
        ByteArrayInputStream in = new ByteArrayInputStream(WRAPPED_JSON.getBytes());
        JsonParser parser = new AdaptedJsonParser((value) -> {
            if ("Falco".equals(value)) {
                return value + ", a best dog ever!";
            }
            return value;
        }, in);

        parser.next(); //START_OBJECT
        //should be advanced further

        try {
        	bindingYassonJsonb.fromJson(parser, Dog.class);
            fail("JsonbException not thrown");
        } catch (JsonbException e) {
            //OK, parser in inconsistent state
        }
    }

    @Test
    public void testInvalidGeneratorWrappedWithUserInteraction() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Best dog ever!", out);

        generator.writeStartObject();
        //key not written

        Assertions.shouldFail(() -> bindingYassonJsonb.toJson(dog, generator));
    }

    @Test
    public void testRuntimeTypeParser() {
        Wrapper<String> stringWrapper = new Wrapper<>();
        stringWrapper.setValue("String value");
        ByteArrayInputStream in = new ByteArrayInputStream("{\"value\":\"String value\"}".getBytes());
        JsonParser parser = new AdaptedJsonParser((value) -> {
            if (value.equals("String value")) {
                return "Adapted string";
            }
            return value;
        }, in);
        Wrapper<String> result = bindingYassonJsonb.fromJson(parser, new TestTypeToken<Wrapper<String>>() {}.getType());
        assertEquals("Adapted string", result.getValue());
    }

    /**
     * This test tests that provided generator is actually used.
     */
    @Test
    public void testRuntimeTypeGenerator() {
        Wrapper<String> stringWrapper = new Wrapper<>();
        stringWrapper.setValue("String value");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Appended value.", out);
        bindingYassonJsonb.toJson(stringWrapper, new TestTypeToken<Wrapper<String>>(){}.getType(), generator);
        generator.close();
        assertEquals("{\"value\":\"String value\",\"suffix\":\"Appended value.\"}", out.toString());
    }
}
