/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.yasson.defaultmapping.specific.model.ClassWithUnsupportedFields;
import org.eclipse.yasson.defaultmapping.specific.model.CustomUnsupportedInterface;
import org.eclipse.yasson.defaultmapping.specific.model.SupportedTypes;
import org.eclipse.yasson.defaultmapping.specific.model.SupportedTypes.NestedPojo;
import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.eclipse.yasson.YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Roman Grigoriadi
 */
public class UnmarshallingUnsupportedTypesTest {

    @Test
    public void testUnmarshallToUnsupportedInterface() {
        ClassWithUnsupportedFields unsupported = new ClassWithUnsupportedFields();
        unsupported.customInterface = new CustomUnsupportedInterface() {

            private String value = "value1";
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public void setValue(String value) {
                throw new IllegalStateException("Not supposed to be called.");
            }
        };
        String expected = "{\"customInterface\":{\"value\":\"value1\"}}";
        assertEquals(expected, defaultJsonb.toJson(unsupported));
        try {
        	defaultJsonb.fromJson(expected, ClassWithUnsupportedFields.class);
            fail("Should report an error");
        } catch (JsonbException e) {
            assertTrue(e.getMessage().contains("Cannot infer a type"));
            assertTrue(e.getMessage().contains("customInterface"));
        }
    }

    @Test
    public void testPojoForMalformedJson() {

        SupportedTypes supportedTypes = new SupportedTypes();
        LocalDateTime localDateTime = LocalDateTime.of(2015, 12, 28, 15, 57);
        ZoneId prague = ZoneId.of("Europe/Prague");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, prague);
        supportedTypes.setInstant(Instant.from(zonedDateTime));
        supportedTypes.setOptionalLong(OptionalLong.of(11L));
        supportedTypes.setZonedDateTime(zonedDateTime);
        supportedTypes.setNestedPojo(new SupportedTypes.NestedPojo());
        supportedTypes.getNestedPojo().setIntegerValue(10);

        String json = "{\"instant\":\"2015-12-28T14:57:00Z\",\"nestedPojo\":{\"integerValue\":10},\"optionalLong\":11,\"zonedDateTime\":\"2015-12-28T15:57:00+01:00[Europe/Prague]\"}";
        assertEquals(json, defaultJsonb.toJson(supportedTypes));

        SupportedTypes result = defaultJsonb.fromJson(json, SupportedTypes.class);
        assertEquals(result.getInstant(), supportedTypes.getInstant());
        assertEquals(result.getZonedDateTime(), supportedTypes.getZonedDateTime());
        assertEquals(result.getOptionalLong(), supportedTypes.getOptionalLong());
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());

    }

    @Test
    public void testPojoAsScalarValue() {
        //wrong, nestedPojo is a value.
        String json  = "{\"nestedPojo\":\"10\",\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "nestedPojo", NestedPojo.class);
    }

    @Test
    public void testPojoAsArray() {
        //wrong, nestedPojo is a collection.
        String json  = "{\"nestedPojo\":[\"10\"],\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "nestedPojo", NestedPojo.class);
    }

    @Test()
    public void testMissingFieldDefault() {
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":5},\"optionalLong\":11}";
        SupportedTypes result = defaultJsonb.fromJson(json, SupportedTypes.class);
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());
        assertEquals(11, result.getOptionalLong().getAsLong());
    }

    @Test()
    public void testMissingFieldDefaultNull() {
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":null},\"optionalLong\":11}";
        SupportedTypes result = defaultJsonb.fromJson(json, SupportedTypes.class);
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());
        assertEquals(11, result.getOptionalLong().getAsLong());
    }

    @Test
    public void testMissingFieldIgnored() {
    	assertThrows(JsonbException.class, () -> {
	        Jsonb defaultConfig = JsonbBuilder.create(new JsonbConfig().setProperty(FAIL_ON_UNKNOWN_PROPERTIES, true));
	        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":5},\"optionalLong\":11}";
	        SupportedTypes result = defaultConfig.fromJson(json, SupportedTypes.class);
    	});
    }

    @Test
    public void testMissingFieldWithObjectValue() {
        String json  = "{\"missingProperty\":{\"optionalLong\":404},\"optionalLong\":11}";
        SupportedTypes result = defaultJsonb.fromJson(json, SupportedTypes.class);

        assertTrue(result.getOptionalLong().isPresent());
        assertThat(result.getOptionalLong().getAsLong(), is(11L));
    }

    @Test
    public void testMissingFieldWithArrayValue() {
        String json  = "{\"missingProperty\":[404],\"optionalLong\":11}";
        SupportedTypes result = defaultJsonb.fromJson(json, SupportedTypes.class);

        assertTrue(result.getOptionalLong().isPresent());
        assertThat(result.getOptionalLong().getAsLong(), is(11L));
    }

    @Test
    public void testEmptyStringAsInteger() {
        Type type = new TestTypeToken<GenericTestClass<Integer, Integer>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Integer.class);
    }

    @Test
    public void testEmptyStringAsDouble() {
        Type type = new TestTypeToken<GenericTestClass<Double, Double>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Double.class);
    }

    @Test
    public void testEmptyStringAsFloat() {
        Type type = new TestTypeToken<GenericTestClass<Float, Float>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Float.class);
    }

    @Test
    public void testEmptyStringAsLong() {
        Type type = new TestTypeToken<GenericTestClass<Long, Long>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Long.class);
    }

    @Test
    public void testEmptyStringAsShort() {
        Type type = new TestTypeToken<GenericTestClass<Short, Short>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Short.class);
    }

    @Test
    public void testEmptyStringAsByte() {
        Type type = new TestTypeToken<GenericTestClass<Byte, Byte>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Byte.class);
    }

    @Test
    public void testEmptyStringAsBigDecimal() {
        Type type = new TestTypeToken<GenericTestClass<BigDecimal, BigDecimal>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type,"field1", BigDecimal.class);
    }

    @Test
    public void testEmptyStringAsBigInteger() {
        Type type = new TestTypeToken<GenericTestClass<BigInteger, BigInteger>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", BigInteger.class);
    }

    @Test
    public void testEmptyStringAsOptionalDouble() {
        Type type = new TestTypeToken<GenericTestClass<OptionalDouble, OptionalDouble>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type,"field1", Double.class); //We are reusing Double deserializer
    }

    @Test
    public void testEmptyStringAsOptionalInt() {
        Type type = new TestTypeToken<GenericTestClass<OptionalInt, OptionalInt>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "field1", Integer.class); //We are reusing Integer deserializer
    }

    @Test
    public void testEmptyStringAsOptionalLong() {
        Type type = new TestTypeToken<GenericTestClass<OptionalLong, OptionalLong>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type,"field1", Long.class); //We are reusing Long deserializer
    }

    private void assertFail(String json, Type type, String failureProperty, Class<?> failurePropertyClass) {
        try {
        	defaultJsonb.fromJson(json, type);
            fail();
        } catch (JsonbException e) {
            if(!e.getMessage().contains(failureProperty) || !e.getMessage().contains(failurePropertyClass.getName())) {
                fail("Expected error message to contain '" + failureProperty + "' and '" + failurePropertyClass.getName() + "', but was: " +
                 e.getMessage());
            }
        }
    }
}
