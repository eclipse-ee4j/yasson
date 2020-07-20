/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.documented;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

/**
 * Contains tests from http://json-b.net/docs/user-guide.html
 */
public class DocumentationExampleTest {

    public static class Dog {
        public String name;
        public int age;
        public boolean bitable;
    }

    @Test
    public void testMappingExample() {
        // Create a dog instance
        Dog dog = new Dog();
        dog.name = "Falco";
        dog.age = 4;
        dog.bitable = false;

        // Create Jsonb and serialize
        String result = defaultJsonb.toJson(dog);
        assertEquals("{\"age\":4,\"bitable\":false,\"name\":\"Falco\"}", result);

        // Deserialize back
        dog = defaultJsonb.fromJson("{\"name\":\"Falco\",\"age\":4,\"bites\":false}", Dog.class);
        assertEquals("Falco", dog.name);
        assertEquals(4, dog.age);
        assertEquals(false, dog.bitable);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testMappingCollection() {
        Dog falco = new Dog();
        falco.name = "Falco";
        falco.age = 4;
        Dog cassidy = new Dog();
        cassidy.name = "Cassidy";
        cassidy.age = 5;

        // List of dogs
        List dogs = new ArrayList();
        dogs.add(falco);
        dogs.add(cassidy);

        // Create Jsonb and serialize
        String result = defaultJsonb.toJson(dogs);
        assertEquals(
                "[{\"age\":4,\"bitable\":false,\"name\":\"Falco\"},{\"age\":5,\"bitable\":false,\"name\":\"Cassidy\"}]",
                result);

        // We can also deserialize back into a raw collection, but since there is no way
        // to infer a type here,
        // the result will be a list of java.util.Map instances with string keys.
        dogs = defaultJsonb.fromJson(result, ArrayList.class);
        assertEquals(2, dogs.size());
        assertEquals("Falco", ((Map) dogs.get(0)).get("name"));
        assertEquals("Cassidy", ((Map) dogs.get(1)).get("name"));
        // assertEquals(4, ((Map) dogs.get(0)).get("age")); // TODO should these
        // actually be BigDecimals?
        // assertEquals(5, ((Map) dogs.get(1)).get("age"));
    }

    @SuppressWarnings("serial")
    @Test
    public void testMappingGenericCollection() {
        Dog falco = new Dog();
        falco.name = "Falco";
        falco.age = 4;
        Dog cassidy = new Dog();
        cassidy.name = "Cassidy";
        cassidy.age = 5;

        // List of dogs
        List<Dog> dogs = new ArrayList<>();
        dogs.add(falco);
        dogs.add(cassidy);

        // Create Jsonb and serialize
        String result = defaultJsonb.toJson(dogs);
        assertEquals(
                "[{\"age\":4,\"bitable\":false,\"name\":\"Falco\"},{\"age\":5,\"bitable\":false,\"name\":\"Cassidy\"}]",
                result);

        // Deserialize back
        dogs = defaultJsonb.fromJson(result, new ArrayList<Dog>() {
        }.getClass().getGenericSuperclass());
        assertEquals(2, dogs.size());
        assertEquals("Falco", dogs.get(0).name);
        assertEquals("Cassidy", dogs.get(1).name);
    }

    @Test
    public void testFormattedOutput() {
        Dog pojo = new Dog();
        pojo.name = "Falco";
        pojo.age = 4;

        // Use it!
        String result = formattingJsonb.toJson(pojo);
        assertEquals("{\n" + 
                "    \"age\": 4,\n" + 
                "    \"bitable\": false,\n" + 
                "    \"name\": \"Falco\"\n" + 
                "}", result);
    }
    
    public static class Person1 {
        @JsonbProperty("person-name")
        public String name;
        public String profession;
    }
    
    @Test
    public void testChangingPropertyNames1() {
        Person1 p = new Person1();
        p.name = "Jason Bourne";
        p.profession = "Super Agent";
        
        String result = formattingJsonb.toJson(p);
        assertEquals("{\n" + 
                "    \"person-name\": \"Jason Bourne\",\n" + 
                "    \"profession\": \"Super Agent\"\n" + 
                "}", result);
    }
    
    public class Person2 {
        private String name;
        private String profession;

        @JsonbProperty("person-name")
        public String getName() {
            return name;
        }

        public String getProfession() {
            return profession;
        }

        // public setters ...
        public void setName(String name) {
            this.name = name;
        }
        public void setProfession(String profession) {
            this.profession = profession;
        }
    }
    
    @Test
    public void testChangingPropertyNames2() {
        Person2 p = new Person2();
        p.name = "Jason Bourne";
        p.profession = "Super Agent";
        
        String result = formattingJsonb.toJson(p);
        assertEquals("{\n" + 
                "    \"person-name\": \"Jason Bourne\",\n" + 
                "    \"profession\": \"Super Agent\"\n" + 
                "}", result);
    }
    
    public static class Person3 {
        private String name;

        @JsonbProperty("name-to-write")
        public String getName() {
            return name;
        }

        @JsonbProperty("name-to-read")
        public void setName(String name) {
            this.name = name;
        }
    }
    
    @Test
    public void testChangingPropertyNames3() {
        Person3 p = new Person3();
        p.name = "Jason Bourne";
        String result = defaultJsonb.toJson(p);
        assertEquals("{\"name-to-write\":\"Jason Bourne\"}", result);
        
        String json = "{\"name-to-read\":\"Jason Bourne\"}";
        Person3 after = defaultJsonb.fromJson(json, Person3.class);
        assertEquals("Jason Bourne", after.name);
    }
    
    public static class Person4 { // TODO: a non-static class results in an NPE
        @JsonbTransient
        private String name;

        private String profession;

        // public getters/setters ...
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getProfession() {
            return this.profession;
        }
        public void setProfession(String profession) {
            this.profession = profession;
        }
    }
    
    @Test
    public void testIgnoringProperties() {
        Person4 p = new Person4();
        p.name = "Jason Bourne";
        p.profession = "Super Agent";
        String result = defaultJsonb.toJson(p);
        assertEquals("{\"profession\":\"Super Agent\"}", result);
        
        String json = "{\"profession\":\"Super Agent\"}";
        Person4 after = defaultJsonb.fromJson(json, Person4.class);
        assertEquals("Super Agent", after.profession);
        assertNull(after.name);
    }
    
    @JsonbNillable
    public class Person5 {
        private String name;
        private String profession;

        // public getters/setters ...
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getProfession() {
            return profession;
        }
        public void setProfession(String profession) {
            this.profession = profession;
        }
    }
    
    @Test
    public void testNullHandling1() {
        Person5 p = new Person5();
        String result = defaultJsonb.toJson(p);
        assertEquals("{\"name\":null,\"profession\":null}", result);
    }
    
    public class Person6 {
        @JsonbProperty(nillable=true)
        private String name;

        private String profession;

        // public getters/setters ...
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getProfession() {
            return profession;
        }
        public void setProfession(String profession) {
            this.profession = profession;
        }
    }
    
    @Test
    public void testNullHandling2() {
        Person6 p = new Person6();
        String result = defaultJsonb.toJson(p);
        assertEquals("{\"name\":null}", result);
    }
    
    public static class Person {
        public String name;
        public String profession;
    }

    @Test
    public void testNullHandling3() {
        Person p = new Person();
        String result = nullableJsonb.toJson(p);
        assertEquals("{\"name\":null,\"profession\":null}", result);
    }
    
    public static class Person8 { // TODO: obscure error here if non-static
        public final String name;
        public String profession;

        @JsonbCreator
        public Person8(@JsonbProperty("name") String name) {
            this.name = name;
        }
    }
    
    @Test
    public void testCustomInstantiation() {
        Person8 p = defaultJsonb.fromJson("{\"name\":\"Jason Bourne\"}", Person8.class);
        assertEquals("Jason Bourne", p.name);
    }
    
    public static class Person9 {
        public String name;

        @JsonbDateFormat("dd.MM.yyyy")
        public LocalDate birthDate;

        @JsonbNumberFormat("#0.00")
        public BigDecimal salary;
    }
    
    @Test
    public void testDateNumberFormats1() {
        Person9 p = new Person9();
        p.name = "Jason Bourne";
        p.birthDate = LocalDate.of(1999, 8, 7);
        p.salary = new BigDecimal("123.45678");
        String json = defaultJsonb.toJson(p);
        assertEquals("{\"birthDate\":\"07.08.1999\",\"name\":\"Jason Bourne\",\"salary\":\"123.46\"}", json);
        
        Person9 after = defaultJsonb.fromJson("{\"birthDate\":\"07.08.1999\",\"name\":\"Jason Bourne\",\"salary\":\"123.46\"}", Person9.class);
        assertEquals(p.name, after.name);
        assertEquals(p.birthDate, after.birthDate);
        assertEquals(new BigDecimal("123.46"), after.salary);
    }
    
    public static class Person10 {
        public String name;

        public LocalDate birthDate;

        public BigDecimal salary;
    }
    
    @Test
    public void testDateNumberFormats2() {
        Person10 p = new Person10();
        p.name = "Jason Bourne";
        p.birthDate = LocalDate.of(1999, 8, 7);
        p.salary = new BigDecimal("123.45678");
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()//
                .withDateFormat("dd.MM.yyyy", null)); // TODO: why no withNumberFormat?
        String json = jsonb.toJson(p);
        assertEquals("{\"birthDate\":\"07.08.1999\",\"name\":\"Jason Bourne\",\"salary\":123.45678}", json);
        
        Person9 after = jsonb.fromJson("{\"birthDate\":\"07.08.1999\",\"name\":\"Jason Bourne\",\"salary\":123.45678}", Person9.class);
        assertEquals(p.name, after.name);
        assertEquals(p.birthDate, after.birthDate);
        assertEquals(p.salary, after.salary);
    }
    
    public static class Customer {
        private int id;
        private String name;
        private String organization;
        private String position;
        
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getOrganization() {
            return organization;
        }
        public void setOrganization(String organization) {
            this.organization = organization;
        }
        public String getPosition() {
            return position;
        }
        public void setPosition(String position) {
            this.position = position;
        }
    }

    public static class CustomerAnnotated {
        @JsonbProperty("customer_id")
        private int id;

        @JsonbProperty("customer_name")
        private String name;
        
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CustomerAdapter implements JsonbAdapter<Customer, CustomerAnnotated> {
        @Override
        public CustomerAnnotated adaptToJson(Customer c) throws Exception {
            CustomerAnnotated customer = new CustomerAnnotated();
            customer.setId(c.getId());
            customer.setName(c.getName());
            return customer;
        }

        @Override
        public Customer adaptFromJson(CustomerAnnotated adapted) throws Exception {
            Customer customer = new Customer();
            customer.setId(adapted.getId());
            customer.setName(adapted.getName());
            return customer;
        }
    }
    
    @Test
    public void testAdapters1() {
        // Create customer
        Customer customer = new Customer();

        customer.setId(1);
        customer.setName("Jason Bourne");
        customer.setOrganization("Super Agents");
        customer.setPosition("Super Agent");

        // Serialize
        String json = defaultJsonb.toJson(customer);
        assertEquals("{\"id\":1,\"name\":\"Jason Bourne\",\"organization\":\"Super Agents\",\"position\":\"Super Agent\"}", json);
    }
    
    @Test
    public void testAdapters2() {
     // Create custom configuration
        JsonbConfig config = new JsonbConfig()
            .withAdapters(new CustomerAdapter());

        // Create Jsonb with custom configuration
        Jsonb jsonb = JsonbBuilder.create(config);

        // Create customer
        Customer customer = new Customer();

        customer.setId(1);
        customer.setName("Jason Bourne");
        customer.setOrganization("Super Agents");
        customer.setPosition("Super Agent");

        // Serialize
        String json = jsonb.toJson(customer);
        assertEquals("{\"customer_id\":1,\"customer_name\":\"Jason Bourne\"}", json);
    }
    
    public static class CustomerSerializer implements JsonbSerializer<Customer> {
        @Override
        public void serialize(Customer customer, JsonGenerator generator, SerializationContext ctx) {
            generator.writeStartObject();
            generator.write("customer_id", customer.getId());
            generator.write("customer_name", customer.getName());
            generator.writeEnd();
        }
    }

    public static class CustomerDeserializer implements JsonbDeserializer<Customer> {
        @Override
        public Customer deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Customer customer = new Customer();
            JsonParser.Event next;

            // Moving parser by hand looking for customer_id and customer_name properties
            while ((next = parser.next()) != JsonParser.Event.END_OBJECT) {
                if (next == JsonParser.Event.KEY_NAME) {
                    String jsonKeyName = parser.getString();

                    // Move to json value
                    parser.next();

                    if ("customer_id".equals(jsonKeyName)) {
                        customer.setId(parser.getInt());
                    } else if ("customer_name".equals(jsonKeyName)) {
                        customer.setName(parser.getString());
                    }
                }
            }
            return customer;
        }
    }
    
    @Test
    public void testSerializerDeserializer() {
        // Create pojo
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("Freddie");
        customer.setOrganization("Super Agents");
        customer.setPosition("Super Agent");

        // Also configurable with @JsonbSerializer / JsonbDeserializer on properties and class.
        JsonbConfig config = new JsonbConfig()
                .withSerializers(new CustomerSerializer())
                .withDeserializers(new CustomerDeserializer());

        Jsonb jsonb = JsonbBuilder.create(config);
        String json = jsonb.toJson(customer);
        assertEquals("{\"customer_id\":1,\"customer_name\":\"Freddie\"}", json);
        
        Customer result = jsonb.fromJson(json, Customer.class);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getName(), result.getName());
        assertNull(result.getOrganization());
        assertNull(result.getPosition());
    }
}
