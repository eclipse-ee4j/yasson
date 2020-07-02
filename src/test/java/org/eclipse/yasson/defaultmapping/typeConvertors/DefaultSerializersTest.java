/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.typeConvertors;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.eclipse.yasson.defaultmapping.typeConvertors.model.ByteArrayWrapper;
import org.eclipse.yasson.internal.JsonBindingBuilder;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

/**
 * This class contains Converter tests
 *
 * @author David Kral
 */
public class DefaultSerializersTest {

    @Test
    public void testCharacter() {
        final String json = "{\"value\":\"\uFFFF\"}";
        assertEquals(json, defaultJsonb.toJson(new ScalarValueWrapper<>('\uFFFF')));
        ScalarValueWrapper<Character> result = defaultJsonb.fromJson(json, new TestTypeToken<ScalarValueWrapper<Character>>(){}.getType());
        assertEquals((Character)'\uFFFF', result.getValue());
    }

    @Test
    public void testByteArray() {
        byte[] array = {1, 2, 3};
        assertEquals("[1,2,3]", bindingJsonb.toJson(array));
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
    
    @Test
    public void testUUID() {
        UUID uuid = UUID.randomUUID();
        String json = defaultJsonb.toJson(uuid);
        UUID result = defaultJsonb.fromJson(json, UUID.class);
        assertEquals(uuid, result);
    }
    
    @Test
    public void serializeObjectWithPth() {
        
        Path expectedPath = Paths.get("/tmp/hello/me.txt");
        String expectedPathString = expectedPath.toString().replace("\\", "\\\\");        
        String expectedJson = "{\"path\":\"" + expectedPathString + "\"}";
        final ObjectWithPath objectWithPath = new ObjectWithPath();
        objectWithPath.path = expectedPath;
        final String s = defaultJsonb.toJson(objectWithPath);
        assertEquals(expectedJson, s);
        
        ObjectWithPath actualObject = defaultJsonb.fromJson(expectedJson, ObjectWithPath.class);
        assertEquals(expectedPath, actualObject.path);
    }
    
    public static class ObjectWithPath {
        public Path path;
    }
}
