/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.jsonpsubstitution;

import org.eclipse.yasson.Assertions;
import org.eclipse.yasson.JsonBindingProvider;
import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonJsonb;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    private YassonJsonb jsonb;

    @Before
    public void setUp() {
        // Create Jsonb and serialize
        JsonBindingProvider provider = new JsonBindingProvider();
        JsonbBuilder builder = provider.create();
        jsonb = (YassonJsonb) builder.build();
    }

    @Test
    public void testPreinstantiatedJsonGeneratorAndParser() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Best dog ever!", out);
        jsonb.toJson(dog, generator);
        generator.close();

        assertEquals(EXPECTED_JSON, new String(out.toByteArray()));

        ByteArrayInputStream in = new ByteArrayInputStream(EXPECTED_JSON.getBytes());
        JsonParser parser = new AdaptedJsonParser((value) -> {
            if ("Falco".equals(value)) {
                return value + ", a best dog ever!";
            }
            return value;
        }, in);
        Dog result = jsonb.fromJson(parser, Dog.class);

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

        Dog result = jsonb.fromJson(parser, Dog.class);

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
        jsonb.toJson(dog, generator);
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
            jsonb.fromJson(parser, Dog.class);
            Assert.fail("JsonbException not thrown");
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

        Assertions.shouldFail(() -> jsonb.toJson(dog, generator));
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
        Wrapper<String> result = jsonb.fromJson(parser, new TestTypeToken<Wrapper<String>>() {}.getType());
        Assert.assertEquals("Adapted string", result.getValue());
    }

    @Test
    public void testRuntimeTypeGenerator() {
        Wrapper<String> stringWrapper = new Wrapper<>();
        stringWrapper.setValue("String value");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SuffixJsonGenerator("Appended value.", out);
        jsonb.toJson(stringWrapper, new TestTypeToken<List<String>>(){}.getType(), generator);
        generator.close();
        Assert.assertEquals("{\"value\":\"String value\",\"suffix\":\"Appended value.\"}", out.toString());
    }

}
