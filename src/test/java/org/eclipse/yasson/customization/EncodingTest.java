/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;

import static org.eclipse.yasson.Jsonbs.testWithJsonbBuilderCreate;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.TestTypeToken;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Tests encoding to JSONP propagation
 *
 * @author Roman Grigoriadi
 */
public class EncodingTest {

    private static final Logger logger = Logger.getLogger(EncodingTest.class.getName());

    private static final String[] CZECH;
    private static final String[] RUSSIAN;


    static {
        ResourceBundle messages = ResourceBundle.getBundle("yasson-messages");
        CZECH = messages.getString("czechDiacritics").split(",");
        RUSSIAN = messages.getString("russianDiacritics").split(",");
    }

    @Test
    public void testCP1250Encoding() {
    	String encoding = "cp1250";
    	testWithJsonbBuilderCreate(new JsonbConfig().withEncoding(encoding), jsonb -> {

            try {
                testMarshaller(CZECH, jsonb, encoding);
                testUnmarshaller(CZECH, jsonb, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testUTF8Encoding() {
    	String encoding = "UTF-8";
    	testWithJsonbBuilderCreate(new JsonbConfig().withEncoding(encoding), jsonb -> {

            try {
                testMarshaller(CZECH, jsonb, encoding);
                testUnmarshaller(CZECH, jsonb, encoding);
                testMarshaller(RUSSIAN, jsonb, encoding);
                testUnmarshaller(RUSSIAN, jsonb, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testcp1251Encoding() {
    	String encoding = "cp1251";
    	testWithJsonbBuilderCreate(new JsonbConfig().withEncoding(encoding), jsonb -> {

            try {
                testMarshaller(RUSSIAN, jsonb, encoding);
                testUnmarshaller(RUSSIAN, jsonb, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void testMarshaller(String[] input, Jsonb jsonb, String encoding) throws UnsupportedEncodingException {
        List<String> strings = Arrays.asList(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonb.toJson(strings, baos);
        String marshallerResult = baos.toString(encoding);
        logger.finest("Marshaller JSON result: "+marshallerResult);
        assertEquals(diacriticsToJsonArray(input), marshallerResult);
    }

    private static void testUnmarshaller(String[] input, Jsonb jsonb, String encoding) throws UnsupportedEncodingException {
        String json = diacriticsToJsonArray(input);
        logger.finest("JSON for unmarshaller: "+json);
        InputStream bis = new ByteArrayInputStream(json.getBytes(encoding));
        ArrayList<String> result = jsonb.fromJson(bis, new TestTypeToken<ArrayList<String>>(){}.getType());
        assertArrayEquals(input, result.toArray(new String[0]));
    }

    private static String diacriticsToJsonArray(String[] diacritics) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String str : diacritics) {
            sb.append("\"").append(str).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
