/*******************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.defaultmapping.specific;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonProperties;
import org.eclipse.yasson.defaultmapping.generics.model.GenericTestClass;
import org.eclipse.yasson.defaultmapping.specific.model.ClassWithUnsupportedFields;
import org.eclipse.yasson.defaultmapping.specific.model.CustomUnsupportedInterface;
import org.eclipse.yasson.defaultmapping.specific.model.SupportedTypes;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
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
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class UnmarshallingUnsupportedTypesTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    private static final Logger logger = Logger.getLogger(UnmarshallingUnsupportedTypesTest.class.getSimpleName());

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
        assertEquals(expected, jsonb.toJson(unsupported));
        try {
            jsonb.fromJson(expected, ClassWithUnsupportedFields.class);
            fail("Should report an error");
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Can't infer a type"));
        }
    }

    @Test
    public void testPojoForMalformedJson() throws Exception {

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
        assertEquals(json, jsonb.toJson(supportedTypes));

        SupportedTypes result = jsonb.fromJson(json, SupportedTypes.class);
        assertEquals(result.getInstant(), supportedTypes.getInstant());
        assertEquals(result.getZonedDateTime(), supportedTypes.getZonedDateTime());
        assertEquals(result.getOptionalLong(), supportedTypes.getOptionalLong());
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());

    }

    @Test
    public void testPojoAsScalarValue() {
        //wrong, nestedPojo is a value.
        String json  = "{\"nestedPojo\":\"10\",\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "Error deserialize JSON value into type: class org.eclipse.yasson.defaultmapping.specific.model.SupportedTypes$NestedPojo");
    }

    @Test
    public void testPojoAsArray() {
        //wrong, nestedPojo is a collection.
        String json  = "{\"nestedPojo\":[\"10\"],\"optionalLong\":11}";
        assertFail(json, SupportedTypes.class, "Can't deserialize JSON array into: class org.eclipse.yasson.defaultmapping.specific.model.SupportedTypes$NestedPojo");
    }

    @Test()
    public void testMissingFieldDefault() {
        Jsonb defaultConfig = JsonbBuilder.create();
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":5},\"optionalLong\":11}";
        SupportedTypes result = defaultConfig.fromJson(json, SupportedTypes.class);
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());
        assertEquals(11, result.getOptionalLong().getAsLong());
    }

    @Test()
    public void testMissingFieldDefaultNull() {
        Jsonb defaultConfig = JsonbBuilder.create();
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":null},\"optionalLong\":11}";
        SupportedTypes result = defaultConfig.fromJson(json, SupportedTypes.class);
        assertEquals(Integer.valueOf(10), result.getNestedPojo().getIntegerValue());
        assertEquals(11, result.getOptionalLong().getAsLong());
    }

    @Test(expected = JsonbException.class)
    public void testMissingFieldIgnored() {
        Jsonb defaultConfig = JsonbBuilder.create(new JsonbConfig().setProperty(YassonProperties.FAIL_ON_UNKNOWN_PROPERTIES, true));
        String json  = "{\"nestedPojo\":{\"integerValue\":10,\"missingField\":5},\"optionalLong\":11}";
        SupportedTypes result = defaultConfig.fromJson(json, SupportedTypes.class);
    }

    @Test
    public void testEmptyStringAsInteger() {
        Type type = new TestTypeToken<GenericTestClass<Integer, Integer>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Integer.");
    }

    @Test
    public void testEmptyStringAsDouble() {
        Type type = new TestTypeToken<GenericTestClass<Double, Double>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Double.");
    }

    @Test
    public void testEmptyStringAsFloat() {
        Type type = new TestTypeToken<GenericTestClass<Float, Float>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Float.");
    }

    @Test
    public void testEmptyStringAsLong() {
        Type type = new TestTypeToken<GenericTestClass<Long, Long>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Long.");
    }

    @Test
    public void testEmptyStringAsShort() {
        Type type = new TestTypeToken<GenericTestClass<Short, Short>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Short.");
    }

    @Test
    public void testEmptyStringAsByte() {
        Type type = new TestTypeToken<GenericTestClass<Byte, Byte>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.lang.Byte.");
    }

    @Test
    public void testEmptyStringAsBigDecimal() {
        Type type = new TestTypeToken<GenericTestClass<BigDecimal, BigDecimal>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.math.BigDecimal.");
    }

    @Test
    public void testEmptyStringAsBigInteger() {
        Type type = new TestTypeToken<GenericTestClass<BigInteger, BigInteger>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.math.BigInteger.");
    }

    @Test
    public void testEmptyStringAsOptionalDouble() {
        Type type = new TestTypeToken<GenericTestClass<OptionalDouble, OptionalDouble>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.util.OptionalDouble.");
    }

    @Test
    public void testEmptyStringAsOptionalInt() {
        Type type = new TestTypeToken<GenericTestClass<OptionalInt, OptionalInt>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.util.OptionalInt.");
    }

    @Test
    public void testEmptyStringAsOptionalLong() {
        Type type = new TestTypeToken<GenericTestClass<OptionalLong, OptionalLong>>(){}.getType();
        assertFail("{\"field1\":\"\"}", type, "Error deserialize JSON value into type: class java.util.OptionalLong.");
    }

    private void assertFail(String json, Type type, String msg) {
        try {
            jsonb.fromJson(json, type);
            fail();
        } catch (JsonbException e) {
            if(!e.getMessage().startsWith(msg)) {
                logger.severe("Exception message does not match");
                logger.severe("Expected: "+ msg);
                logger.severe("Current:  "+e.getMessage());
                fail();
            }
        }
    }
}
