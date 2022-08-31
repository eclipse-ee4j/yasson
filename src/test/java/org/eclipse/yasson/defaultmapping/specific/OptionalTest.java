/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2020 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.specific;

import org.eclipse.yasson.defaultmapping.specific.model.SpecificOptionalWrapper;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.eclipse.yasson.defaultmapping.specific.model.OptionalWrapper;
import org.eclipse.yasson.defaultmapping.specific.model.NotMatchingGettersAndSetters;
import org.eclipse.yasson.defaultmapping.specific.model.Street;
import org.eclipse.yasson.internal.JsonBindingBuilder;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.*;

/**
 * Default mapping Optional* tests.
 *
 * @author Dmitry Kornilov
 */
public class OptionalTest {


    @Test
    public void testOptionalString() {
        assertEquals("{\"value\":\"abc\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(Optional.of("abc"))));

        ScalarValueWrapper<Optional> result = bindingJsonb.fromJson("{\"value\":\"abc\"}", new TestTypeToken<ScalarValueWrapper<Optional>>() {}.getType());
        assertEquals(Optional.of("abc"), result.getValue());
    }

    @Test
    public void testOptionalObject() {
        final OptionalWrapper optionalWrapper = new OptionalWrapper();
        Street street = new Street("Xaveriova", 110);
        optionalWrapper.setStreetOptional(Optional.of(street));

        assertEquals("{\"streetOptional\":{\"name\":\"Xaveriova\",\"number\":110}}", bindingJsonb.toJson(optionalWrapper));

        OptionalWrapper result = bindingJsonb.fromJson("{\"streetOptional\":{\"name\":\"Xaveriova\",\"number\":110}}", OptionalWrapper.class);
        assertTrue(result.getStreetOptional().isPresent());
        assertEquals("Xaveriova", result.getStreetOptional().get().getName());
        assertEquals(Integer.valueOf(110), result.getStreetOptional().get().getNumber());
    }

    @Test
    public void testMarshallOptional() {
        assertEquals("{}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalInt.empty())));
        assertEquals("{}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalLong.empty())));
        assertEquals("{}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.empty())));
        assertEquals("{\"value\":10}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalInt.of(10))));
        assertEquals("{\"value\":100}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalLong.of(100L))));
        assertEquals("{\"value\":10.0}", bindingJsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.of(10.0D))));

        final ScalarValueWrapper<OptionalInt> result = bindingJsonb.fromJson("{\"value\":10}", new TestTypeToken<ScalarValueWrapper<OptionalInt>>() {}.getType());
        assertEquals(OptionalInt.of(10), result.getValue());
    }

    @Test
    public void testMarshallOptionalObject() {
        assertEquals("{}", bindingJsonb.toJson(new ScalarValueWrapper<>(Optional.empty())));
        assertEquals("{\"id\":1,\"name\":\"Cust1\"}", bindingJsonb.toJson(Optional.of(new Customer(1, "Cust1"))));

    }

    @Test
    public void testMarshallOptionalIntArray() {
        final OptionalInt[] array = {OptionalInt.of(1), OptionalInt.of(2), OptionalInt.empty()};
        assertEquals("[1,2,null]", bindingJsonb.toJson(array));

        OptionalInt[] result = bindingJsonb.fromJson("[1,2,null]", new TestTypeToken<OptionalInt[]>() {}.getType());

        assertTrue(result[0].isPresent());
        assertEquals(1, result[0].getAsInt());

        assertTrue(result[1].isPresent());
        assertEquals(2, result[1].getAsInt());

        assertEquals(OptionalInt.empty(), result[2]);
    }

    @Test
    public void testMarshallOptionalArray() {
        final Optional[] array = {Optional.of(new Customer(1, "Cust1")), Optional.of(new Customer(2, "Cust2")), Optional.empty()};
        assertEquals("[{\"id\":1,\"name\":\"Cust1\"},{\"id\":2,\"name\":\"Cust2\"},null]", bindingJsonb.toJson(array));
    }

    @Test
    public void testUnmarshallNullAsOptionalEmpty() {
        final ScalarValueWrapper<OptionalInt> result = bindingJsonb.fromJson("{\"value\":null}", new ScalarValueWrapper<OptionalInt>() {
        }.getClass().getGenericSuperclass());
        assertEquals(OptionalInt.empty(), result.getValue());
    }

    @Test
    public void testUnmarshallOptionalArrayNulls() {
        final OptionalLong[] result = bindingJsonb.fromJson("[null, null]", OptionalLong[].class);

        assertEquals(2, result.length);

        for (OptionalLong item : result) {
            assertEquals(OptionalLong.empty(), item);
        }
    }

    @Test
    public void testUnmarshallOptionalList() {
        final List<Optional<Integer>> result = bindingJsonb.fromJson("[null, null]", new TestTypeToken<List<Optional<Integer>>>() {}.getType());

        assertEquals(2, result.size());

        for (Optional<Integer> item : result) {
            assertEquals(Optional.empty(), item);
        }
    }

    @Test
    public void testMarshallOptionalMap() {
        Map<String, OptionalInt> ints = new HashMap<>();
        ints.put("first", OptionalInt.empty());
        ints.put("second", OptionalInt.empty());
        String result = defaultJsonb.toJson(ints);
        assertEquals("{\"first\":null,\"second\":null}", result);
    }

    @Test
    public void testCorrectOptionalGetter() {
        NotMatchingGettersAndSetters personWithCorrectGetter = new NotMatchingGettersAndSetters();
        String result = defaultJsonb.toJson(personWithCorrectGetter);
        assertEquals("{\"firstName\":1,\"lastName\":\"Correct\"}", result);

        NotMatchingGettersAndSetters deserialized = defaultJsonb.fromJson(result, NotMatchingGettersAndSetters.class);
        personWithCorrectGetter.setFirstName(1);
        assertEquals(personWithCorrectGetter, deserialized);
    }

    @Test
    public void testMarshalEmptyRoot() {
        assertEquals("null", bindingJsonb.toJson(Optional.empty()));
    }

    @Test
    public void testUnmarshalEmptyRoot() {
        assertEquals(Optional.empty(), bindingJsonb.fromJson("null", new TestTypeToken<Optional<Customer>>() {}.getType()));
    }

    @Test
    public void testMarshalEmptyInt() {
        assertEquals("null", bindingJsonb.toJson(OptionalInt.empty()));
    }

    @Test
    public void testUnmarshalEmptyInt() {
        assertEquals(OptionalInt.empty(), bindingJsonb.fromJson("null", OptionalInt.class));
    }

    @Test
    public void testMarshalEmptyLong() {
        assertEquals("null", bindingJsonb.toJson(OptionalLong.empty()));
    }

    @Test
    public void testUnmarshalEmptyLong() {
        assertEquals(OptionalLong.empty(), bindingJsonb.fromJson("null", OptionalLong.class));
    }

    @Test
    public void testMarshalEmptyDouble() {
        assertEquals("null", bindingJsonb.toJson(OptionalDouble.empty()));
    }

    @Test
    public void testUnmarshalEmptyDouble() {
        assertEquals(OptionalDouble.empty(), bindingJsonb.fromJson("null", OptionalDouble.class));
    }

    @Test
    public void testNullInsteadOfOptional() {
        OptionalWrapper optionalWrapper = new OptionalWrapper();
        String expected = "{}";
        assertThat(bindingJsonb.toJson(optionalWrapper), is(expected));
    }

    @Test
    public void testNullInsteadOfOptionalInSpecificOptionals() {
        SpecificOptionalWrapper optionalWrapper = new SpecificOptionalWrapper();
        String expected = "{}";
        assertThat(bindingJsonb.toJson(optionalWrapper), is(expected));
    }

    public static class Customer {
        private int id;
        private String name;

        public Customer() {
        }

        public Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

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
}
