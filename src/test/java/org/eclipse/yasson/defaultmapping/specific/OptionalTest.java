/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 *     Patrik Dudits
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.specific;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.eclipse.yasson.defaultmapping.specific.model.OptionalWrapper;
import org.eclipse.yasson.defaultmapping.specific.model.NotMatchingGettersAndSetters;
import org.eclipse.yasson.defaultmapping.specific.model.Street;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Default mapping Optional* tests.
 *
 * @author Dmitry Kornilov
 */
public class OptionalTest {


    @Test
    public void testOptionalString() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{\"value\":\"abc\"}", jsonb.toJson(new ScalarValueWrapper<>(Optional.of("abc"))));

        ScalarValueWrapper<Optional> result = jsonb.fromJson("{\"value\":\"abc\"}", new TestTypeToken<ScalarValueWrapper<Optional>>() {}.getType());
        assertEquals(Optional.of("abc"), result.getValue());
    }

    @Test
    public void testOptionalObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        final OptionalWrapper optionalWrapper = new OptionalWrapper();
        Street street = new Street("Xaveriova", 110);
        optionalWrapper.setStreetOptional(Optional.of(street));

        assertEquals("{\"streetOptional\":{\"name\":\"Xaveriova\",\"number\":110}}", jsonb.toJson(optionalWrapper));

        OptionalWrapper result = jsonb.fromJson("{\"streetOptional\":{\"name\":\"Xaveriova\",\"number\":110}}", OptionalWrapper.class);
        assertTrue(result.getStreetOptional().isPresent());
        assertEquals("Xaveriova", result.getStreetOptional().get().getName());
        assertEquals(Integer.valueOf(110), result.getStreetOptional().get().getNumber());
    }

    @Test
    public void testMarshallOptional() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalInt.empty())));
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalLong.empty())));
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.empty())));
        assertEquals("{\"value\":10}", jsonb.toJson(new ScalarValueWrapper<>(OptionalInt.of(10))));
        assertEquals("{\"value\":100}", jsonb.toJson(new ScalarValueWrapper<>(OptionalLong.of(100L))));
        assertEquals("{\"value\":10.0}", jsonb.toJson(new ScalarValueWrapper<>(OptionalDouble.of(10.0D))));

        final ScalarValueWrapper<OptionalInt> result = jsonb.fromJson("{\"value\":10}", new TestTypeToken<ScalarValueWrapper<OptionalInt>>() {}.getType());
        assertEquals(OptionalInt.of(10), result.getValue());
    }

    @Test
    public void testMarshallOptionalObject() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("{}", jsonb.toJson(new ScalarValueWrapper<>(Optional.empty())));
        assertEquals("{\"id\":1,\"name\":\"Cust1\"}", jsonb.toJson(Optional.of(new Customer(1, "Cust1"))));

    }

    @Test
    public void testMarshallOptionalIntArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final OptionalInt[] array = {OptionalInt.of(1), OptionalInt.of(2), OptionalInt.empty()};
        assertEquals("[1,2,null]", jsonb.toJson(array));

        OptionalInt[] result = jsonb.fromJson("[1,2,null]", new TestTypeToken<OptionalInt[]>() {}.getType());

        assertTrue(result[0].isPresent());
        assertEquals(1, result[0].getAsInt());

        assertTrue(result[1].isPresent());
        assertEquals(2, result[1].getAsInt());

        assertEquals(OptionalInt.empty(), result[2]);
    }

    @Test
    public void testMarshallOptionalArray() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final Optional[] array = {Optional.of(new Customer(1, "Cust1")), Optional.of(new Customer(2, "Cust2")), Optional.empty()};
        assertEquals("[{\"id\":1,\"name\":\"Cust1\"},{\"id\":2,\"name\":\"Cust2\"},null]", jsonb.toJson(array));
    }

    @Test
    public void testUnmarshallNullAsOptionalEmpty() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final ScalarValueWrapper<OptionalInt> result = jsonb.fromJson("{\"value\":null}", new ScalarValueWrapper<OptionalInt>() {
        }.getClass().getGenericSuperclass());
        Assert.assertEquals(OptionalInt.empty(), result.getValue());
    }

    @Test
    public void testUnmarshallOptionalArrayNulls() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final OptionalLong[] result = jsonb.fromJson("[null, null]", OptionalLong[].class);

        assertEquals(2, result.length);

        for (OptionalLong item : result) {
            Assert.assertEquals(OptionalLong.empty(), item);
        }
    }

    @Test
    public void testUnmarshallOptionalList() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        final List<Optional<Integer>> result = jsonb.fromJson("[null, null]", new TestTypeToken<List<Optional<Integer>>>() {}.getType());

        assertEquals(2, result.size());

        for (Optional<Integer> item : result) {
            Assert.assertEquals(Optional.empty(), item);
        }
    }

    @Test
    public void testMarshallOptionalMap() {
        Map<String, OptionalInt> ints = new HashMap<>();
        ints.put("first", OptionalInt.empty());
        ints.put("second", OptionalInt.empty());
        final Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(ints);
        Assert.assertEquals("{\"first\":null,\"second\":null}", result);
    }

    @Test
    public void testCorrectOptionalGetter() {
        NotMatchingGettersAndSetters personWithCorrectGetter = new NotMatchingGettersAndSetters();
        final Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(personWithCorrectGetter);
        assertEquals("{\"firstName\":1,\"lastName\":\"Correct\"}", result);

        NotMatchingGettersAndSetters deserialized = jsonb.fromJson(result, NotMatchingGettersAndSetters.class);
        personWithCorrectGetter.setFirstName(1);
        assertEquals(personWithCorrectGetter, deserialized);
    }

    @Test
    public void testMarshalEmptyRoot() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("null", jsonb.toJson(Optional.empty()));
    }

    @Test
    public void testUnmarshalEmptyRoot() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals(Optional.empty(), jsonb.fromJson("null", new TestTypeToken<Optional<Customer>>() {}.getType()));
    }

    @Test
    public void testMarshalEmptyInt() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("null", jsonb.toJson(OptionalInt.empty()));
    }

    @Test
    public void testUnmarshalEmptyInt() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals(OptionalInt.empty(), jsonb.fromJson("null", OptionalInt.class));
    }

    @Test
    public void testMarshalEmptyLong() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("null", jsonb.toJson(OptionalLong.empty()));
    }

    @Test
    public void testUnmarshalEmptyLong() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals(OptionalLong.empty(), jsonb.fromJson("null", OptionalLong.class));
    }

    @Test
    public void testMarshalEmptyDouble() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals("null", jsonb.toJson(OptionalDouble.empty()));
    }

    @Test
    public void testUnmarshalEmptyDouble() {
        final Jsonb jsonb = (new JsonBindingBuilder()).build();
        assertEquals(OptionalDouble.empty(), jsonb.fromJson("null", OptionalDouble.class));
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
