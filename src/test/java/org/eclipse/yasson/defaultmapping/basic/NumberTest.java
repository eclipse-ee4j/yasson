/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.basic;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.basic.model.BigDecimalInNumber;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Roman Grigoriadi
 */
public class NumberTest {

    private Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testSerializeFloat() {
        final String json = jsonb.toJson(0.35f);
        Assert.assertEquals("0.35", json);

        Float result = jsonb.fromJson("0.35", Float.class);
        Assert.assertEquals((Float) .35f, result);
    }

    @Test
    public void testBigDecimalMarshalling() {
        String jsonString = jsonb.toJson(new BigDecimal("0.10000000000000001"));
        Assert.assertEquals("\"0.10000000000000001\"", jsonString);

        jsonString = jsonb.toJson(new BigDecimal("0.1000000000000001"));
        Assert.assertEquals("0.1000000000000001", jsonString);

        BigDecimal result = jsonb.fromJson("0.10000000000000001", BigDecimal.class);
        Assert.assertEquals(new BigDecimal("0.10000000000000001"), result);

        result = jsonb.fromJson("\"0.100000000000000001\"", BigDecimal.class);
        Assert.assertEquals(new BigDecimal("0.100000000000000001"), result);
    }

    @Test
    public void testBigDecimalIEEE748() {
        String jsonString = jsonb.toJson(new BigDecimal("9007199254740991"));
        Assert.assertEquals("9007199254740991", jsonString);

        jsonString = jsonb.toJson(new BigDecimal("9007199254740992"));
        Assert.assertEquals("\"9007199254740992\"", jsonString);

        jsonString = jsonb.toJson(new BigDecimal("9007199254740991.1"));
        Assert.assertEquals("\"9007199254740991.1\"", jsonString);

        jsonString = jsonb.toJson(new BigDecimal(new BigInteger("1"), -400));
        Assert.assertEquals("\"" + new BigDecimal(new BigInteger("1"), -400) + "\"", jsonString);
    }

    @Test
    public void testBigIntegerIEEE748() {
        String jsonString = jsonb.toJson(new BigInteger("9007199254740991"));
        Assert.assertEquals("9007199254740991", jsonString);

        jsonString = jsonb.toJson(new BigInteger("9007199254740992"));
        Assert.assertEquals("\"9007199254740992\"", jsonString);
    }

    @Test
    public void testBigDecimalInNumber() {
        BigDecimalInNumber testValueQuoted = new BigDecimalInNumber() {{setBigDecValue(new BigDecimal("9007199254740992"));}};
        BigDecimalInNumber testValueUnQuoted = new BigDecimalInNumber() {{setBigDecValue(new BigDecimal("9007199254740991"));}};
        String jsonString = jsonb.toJson(testValueQuoted);
        Assert.assertEquals("{\"bigDecValue\":\"9007199254740992\"}", jsonString);

        jsonString = jsonb.toJson(testValueUnQuoted);
        Assert.assertEquals("{\"bigDecValue\":9007199254740991}", jsonString);

        BigDecimalInNumber result = jsonb.fromJson("{\"bigDecValue\":\"9007199254740992\"}", BigDecimalInNumber.class);
        Assert.assertEquals(testValueQuoted.getBigDecValue(), result.getBigDecValue());

        result = jsonb.fromJson("{\"bigDecValue\":9007199254740991}", BigDecimalInNumber.class);
        Assert.assertEquals(testValueUnQuoted.getBigDecValue(), result.getBigDecValue());
    }

    @Test
    public void testBigDecimalWrappedMarshalling() {
        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(new BigDecimal("0.1000000000000001")));
        Assert.assertEquals("{\"value\":0.1000000000000001}", jsonString);

        jsonString = jsonb.toJson(new ScalarValueWrapper<>(new BigDecimal("0.10000000000000001")));
        Assert.assertEquals("{\"value\":\"0.10000000000000001\"}", jsonString);

        ScalarValueWrapper<BigDecimal> result = jsonb.fromJson("{\"value\":0.1000000000000001}", new TestTypeToken<ScalarValueWrapper<BigDecimal>>(){}.getType());
        Assert.assertEquals(new BigDecimal("0.1000000000000001"), result.getValue());

        result = jsonb.fromJson("{\"value\":\"0.10000000000000001\"}", new TestTypeToken<ScalarValueWrapper<BigDecimal>>(){}.getType());
        Assert.assertEquals(new BigDecimal("0.10000000000000001"), result.getValue());
    }

    @Test
    public void testBigDecimalCastedToNumber() {
        String jsonString = jsonb.toJson(new Object() { public Number number = new BigDecimal("0.10000000000000001"); });
        Assert.assertEquals("{\"number\":\"0.10000000000000001\"}", jsonString);

        jsonString = jsonb.toJson(new Object() { public Number number = new BigDecimal("0.1000000000000001"); });
        Assert.assertEquals("{\"number\":0.1000000000000001}", jsonString);
    }

    @Test
    public void testLongIEEE748() {

        // 9007199254740991L
        Long maxJsSafeValue = Double.valueOf(Math.pow(2, 53)).longValue() - 1;
        Long upperJsUnsafeValue = maxJsSafeValue + 1;

        String json = jsonb.toJson(maxJsSafeValue);
        Assert.assertEquals("9007199254740991", json);
        Long deserialized = jsonb.fromJson(json, Long.class);
        Assert.assertEquals(Long.valueOf("9007199254740991"), deserialized);

        json = jsonb.toJson(upperJsUnsafeValue);
        Assert.assertEquals("\"9007199254740992\"", json);
        deserialized = jsonb.fromJson(json, Long.class);
        Assert.assertEquals(Long.valueOf("9007199254740992"), deserialized);


        Long minJsSafeValue = Math.negateExact(maxJsSafeValue);
        Long lowerJsUnsafeValue = minJsSafeValue - 1;

        json = jsonb.toJson(minJsSafeValue);
        Assert.assertEquals("-9007199254740991", json);
        deserialized = jsonb.fromJson(json, Long.class);
        Assert.assertEquals(Long.valueOf("-9007199254740991"), deserialized);

        json = jsonb.toJson(lowerJsUnsafeValue);
        Assert.assertEquals("\"-9007199254740992\"", json);
        deserialized = jsonb.fromJson(json, Long.class);
        Assert.assertEquals(Long.valueOf("-9007199254740992"), deserialized);
    }



}
