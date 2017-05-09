/*******************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.typeConvertors;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.eclipse.yasson.defaultmapping.typeConvertors.model.ByteArrayWrapper;
import org.eclipse.yasson.defaultmapping.typeConvertors.model.CalendarWrapper;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.eclipse.yasson.internal.serializer.DefaultSerializers;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.BinaryDataStrategy;
import java.util.Base64;
import java.util.Calendar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * This class contains Converter tests
 *
 * @author David Kral
 */
public class DefaultSerializersTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testCharacter() {
        final String json = "{\"value\":\"\uFFFF\"}";
        assertEquals(json, jsonb.toJson(new ScalarValueWrapper<>('\uFFFF')));
        ScalarValueWrapper<Character> result = jsonb.fromJson(json, new TestTypeToken<ScalarValueWrapper<Character>>(){}.getType());
        assertEquals((Character)'\uFFFF', result.getValue());
    }

    @Test
    public void testByteArray() {
        byte[] array = {1, 2, 3};
        final Jsonb jsonb = (new JsonBindingBuilder()).build();

        assertEquals("[1,2,3]", jsonb.toJson(array));
    }

    @Test
    public void testByteArrayWithBinaryStrategy() {
        byte[] array = {127, -128, 127};
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();

        assertEquals("[127,-128,127]", jsonb.toJson(array));
        assertArrayEquals(array, jsonb.fromJson("[127,-128,127]", byte[].class));
    }

    @Test
    public void testByteArrayWithStrictJson() {
        byte[] array = {1, 2, 3};
        ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper();
        byteArrayWrapper.array = array;
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true))).build();

        assertEquals("{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}", jsonb.toJson(byteArrayWrapper));

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(false))).build();

        assertEquals("{\"array\":[1,2,3]}", jsonb.toJson(byteArrayWrapper));
    }

    @Test
    public void testByteArrayWithStrictJsonAndBinaryStrategy() {
        byte[] array = {1, 2, 3};
        ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper();
        byteArrayWrapper.array = array;
        Jsonb jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();
        final String base64UrlEncodedJson = "{\"array\":\"" + Base64.getUrlEncoder().encodeToString(array) + "\"}";
        assertEquals(base64UrlEncodedJson, jsonb.toJson(byteArrayWrapper));
        ByteArrayWrapper result = jsonb.fromJson(base64UrlEncodedJson, ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BASE_64))).build();
        assertEquals(base64UrlEncodedJson, jsonb.toJson(byteArrayWrapper));
        result = jsonb.fromJson(base64UrlEncodedJson, ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withStrictIJSON(true).withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL))).build();
        assertEquals(base64UrlEncodedJson, jsonb.toJson(byteArrayWrapper));
        result = jsonb.fromJson(base64UrlEncodedJson, ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BYTE))).build();
        assertEquals("[1,2,3]", jsonb.toJson(array));
        result = jsonb.fromJson("{\"array\":[1,2,3]}", ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BASE_64))).build();
        final String base64EncodedJson = "{\"array\":\"" + Base64.getEncoder().encodeToString(array) + "\"}";
        assertEquals(base64EncodedJson, jsonb.toJson(byteArrayWrapper));
        result = jsonb.fromJson(base64EncodedJson, ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);

        jsonb = (new JsonBindingBuilder().withConfig(new JsonbConfig().withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL))).build();
        assertEquals(base64UrlEncodedJson, jsonb.toJson(byteArrayWrapper));
        result = jsonb.fromJson(base64UrlEncodedJson, ByteArrayWrapper.class);
        assertArrayEquals(array, result.array);
    }
}
