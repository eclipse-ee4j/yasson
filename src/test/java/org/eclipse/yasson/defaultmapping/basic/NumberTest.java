/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.basic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import static org.eclipse.yasson.Assertions.*;
import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.basic.model.BigDecimalInNumber;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import jakarta.json.stream.JsonGenerator;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class NumberTest {

    @Test
    public void testSerializeFloat() {
        final String json = defaultJsonb.toJson(0.35f);
        assertEquals("0.35", json);

        Float result = defaultJsonb.fromJson("0.35", Float.class);
        assertEquals((Float) .35f, result);
    }

    @Test
    public void testBigDecimalMarshalling() {
        String jsonString = defaultJsonb.toJson(new BigDecimal("0.10000000000000001"));
        assertEquals("0.10000000000000001", jsonString);

        jsonString = defaultJsonb.toJson(new BigDecimal("0.1000000000000001"));
        assertEquals("0.1000000000000001", jsonString);

        BigDecimal result = defaultJsonb.fromJson("0.10000000000000001", BigDecimal.class);
        assertEquals(new BigDecimal("0.10000000000000001"), result);

        result = defaultJsonb.fromJson("0.100000000000000001", BigDecimal.class);
        assertEquals(new BigDecimal("0.100000000000000001"), result);
    }

    @Test
    public void testBigDecimalIEEE748() {
        String jsonString = defaultJsonb.toJson(new BigDecimal("9007199254740991"));
        assertEquals("9007199254740991", jsonString);

        jsonString = defaultJsonb.toJson(new BigDecimal("9007199254740992"));
        assertEquals("9007199254740992", jsonString);

        jsonString = defaultJsonb.toJson(new BigDecimal("9007199254740991.1"));
        assertEquals("9007199254740991.1", jsonString);

        jsonString = defaultJsonb.toJson(new BigDecimal(new BigInteger("1"), -400));
        assertEquals(new BigDecimal(new BigInteger("1"), -400).toString(), jsonString);
    }

    @Test
    public void testBigIntegerIEEE748() {
        String jsonString = defaultJsonb.toJson(new BigInteger("9007199254740991"));
        assertEquals("9007199254740991", jsonString);

        jsonString = defaultJsonb.toJson(new BigInteger("9007199254740992"));
        assertEquals("9007199254740992", jsonString);
    }

    @Test
    public void testBigDecimalInNumber() {
        BigDecimalInNumber testValueQuoted = new BigDecimalInNumber() {{setBigDecValue(new BigDecimal("9007199254740992"));}};
        BigDecimalInNumber testValueUnQuoted = new BigDecimalInNumber() {{setBigDecValue(new BigDecimal("9007199254740991"));}};
        String jsonString = defaultJsonb.toJson(testValueQuoted);
        assertEquals("{\"bigDecValue\":9007199254740992}", jsonString);

        jsonString = defaultJsonb.toJson(testValueUnQuoted);
        assertEquals("{\"bigDecValue\":9007199254740991}", jsonString);

        BigDecimalInNumber result = defaultJsonb.fromJson("{\"bigDecValue\":9007199254740992}", BigDecimalInNumber.class);
        assertEquals(testValueQuoted.getBigDecValue(), result.getBigDecValue());

        result = defaultJsonb.fromJson("{\"bigDecValue\":9007199254740991}", BigDecimalInNumber.class);
        assertEquals(testValueUnQuoted.getBigDecValue(), result.getBigDecValue());
    }

    @Test
    public void testBigDecimalWrappedMarshalling() {
        String jsonString = defaultJsonb.toJson(new ScalarValueWrapper<>(new BigDecimal("0.1000000000000001")));
        assertEquals("{\"value\":0.1000000000000001}", jsonString);

        jsonString = defaultJsonb.toJson(new ScalarValueWrapper<>(new BigDecimal("0.10000000000000001")));
        assertEquals("{\"value\":0.10000000000000001}", jsonString);

        ScalarValueWrapper<BigDecimal> result = defaultJsonb.fromJson("{\"value\":0.1000000000000001}", new TestTypeToken<ScalarValueWrapper<BigDecimal>>(){}.getType());
        assertEquals(new BigDecimal("0.1000000000000001"), result.getValue());

        result = defaultJsonb.fromJson("{\"value\":0.10000000000000001}", new TestTypeToken<ScalarValueWrapper<BigDecimal>>(){}.getType());
        assertEquals(new BigDecimal("0.10000000000000001"), result.getValue());
    }

    @Test
    public void testBigDecimalCastedToNumber() {
        String jsonString = defaultJsonb.toJson(new Object() { public Number number = new BigDecimal("0.10000000000000001"); });
        assertEquals("{\"number\":0.10000000000000001}", jsonString);

        jsonString = defaultJsonb.toJson(new Object() { public Number number = new BigDecimal("0.1000000000000001"); });
        assertEquals("{\"number\":0.1000000000000001}", jsonString);
    }

    @Test
    public void testLongIEEE748() {
        // 9007199254740991L
        Long maxJsSafeValue = Double.valueOf(Math.pow(2, 53)).longValue() - 1;
        Long upperJsUnsafeValue = maxJsSafeValue + 1;

        String json = defaultJsonb.toJson(maxJsSafeValue);
        assertEquals("9007199254740991", json);
        Long deserialized = defaultJsonb.fromJson(json, Long.class);
        assertEquals(Long.valueOf("9007199254740991"), deserialized);

        json = defaultJsonb.toJson(upperJsUnsafeValue);
        assertEquals("9007199254740992", json);
        deserialized = defaultJsonb.fromJson(json, Long.class);
        assertEquals(Long.valueOf("9007199254740992"), deserialized);


        Long minJsSafeValue = Math.negateExact(maxJsSafeValue);
        Long lowerJsUnsafeValue = minJsSafeValue - 1;

        json = defaultJsonb.toJson(minJsSafeValue);
        assertEquals("-9007199254740991", json);
        deserialized = defaultJsonb.fromJson(json, Long.class);
        assertEquals(Long.valueOf("-9007199254740991"), deserialized);

        json = defaultJsonb.toJson(lowerJsUnsafeValue);
        assertEquals("-9007199254740992", json);
        deserialized = defaultJsonb.fromJson(json, Long.class);
        assertEquals(Long.valueOf("-9007199254740992"), deserialized);
    }

    /**
     * Tests that JSON-P RI itself does no big number (out of IEEE 754 quotation).
     * This is why it is now must be done in Yasson to match the JSONB spec.
     */
    @Test
    public void testJsonpBigNumber() {
        StringWriter w = new StringWriter();
        JsonGenerator generator = Json.createGenerator(w);

        Long maxJsSafeValue = Double.valueOf(Math.pow(2, 53)).longValue() - 1;
        Long upperJsUnsafeValue = Long.MAX_VALUE;

        generator.writeStartObject();
        generator.write("safeLongValue", maxJsSafeValue);
        generator.write("unsafeLongValue", upperJsUnsafeValue);
        generator.write("safeBigDecimalValue", BigDecimal.TEN);
        generator.write("unsafeBigDecimalValue", BigDecimal.valueOf(upperJsUnsafeValue));
        generator.writeEnd();
        generator.close();

        assertEquals("{" +
                        "\"safeLongValue\":9007199254740991," +
                        "\"unsafeLongValue\":9223372036854775807," +
                        "\"safeBigDecimalValue\":10," +
                        "\"unsafeBigDecimalValue\":9223372036854775807}",
                w.toString());


        w = new StringWriter();
        JsonWriter writer = Json.createWriter(w);


        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("safeLongValue", maxJsSafeValue);
        objectBuilder.add("unsafeLongValue", upperJsUnsafeValue);
        objectBuilder.add("safeBigDecimalValue", BigDecimal.valueOf(maxJsSafeValue));
        objectBuilder.add("unsafeBigDecimalValue", BigDecimal.valueOf(upperJsUnsafeValue));
        JsonObject build = objectBuilder.build();
        writer.write(build);
        writer.close();

        assertEquals("{" +
                "\"safeLongValue\":9007199254740991," +
                "\"unsafeLongValue\":9223372036854775807," +
                "\"safeBigDecimalValue\":9007199254740991," +
                "\"unsafeBigDecimalValue\":9223372036854775807}", w.toString());

    }
    
    public static class NumberContainer {
    	public Double doubleProp;
    	public Collection<Double> collectionProp;
    	public Map<String,Double> mapProp;
    }
    
    @Test
    public void testSerializeInvalidDouble() {
        shouldFail(() -> defaultJsonb.toJson(Double.POSITIVE_INFINITY));

        NumberContainer obj = new NumberContainer();
        obj.doubleProp = Double.POSITIVE_INFINITY;
        shouldFail(() -> defaultJsonb.toJson(obj), msg -> msg.contains("doubleProp") && msg.contains("NumberContainer"));
    }
    
    
    @Test
    public void testSerializeInvalidDoubleCollection() {
        NumberContainer obj = new NumberContainer();
        obj.collectionProp = Collections.singleton(Double.POSITIVE_INFINITY);
        shouldFail(() -> defaultJsonb.toJson(obj),
                  msg -> msg.contains("collectionProp") && msg.contains("NumberContainer"));
    }

    @Test
    public void testSerializeInvalidDoubleMap() {
        NumberContainer obj = new NumberContainer();
        obj.mapProp = Collections.singletonMap("doubleKey", Double.POSITIVE_INFINITY);
        shouldFail(() -> defaultJsonb.toJson(obj),
                  msg -> msg.contains("mapProp") && msg.contains("NumberContainer"));
    }
}
