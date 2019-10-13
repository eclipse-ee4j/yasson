/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests calling JSONB with {@link java.util.stream.Stream} and {@link Readable}
 *
 * @author Roman Grigoriadi
 */
public class JsonStreamsTest {
    private static final String CHARSET = "UTF8";

    @Test
    public void testUnmarshall() throws Exception {

        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        Map<String, String> result = defaultJsonb.fromJson(new InputStreamReader(new ByteArrayInputStream(json.getBytes(CHARSET)), Charset.forName(CHARSET)), new TestTypeToken<HashMap<String, String>>(){}.getType());
        assertMapValues(result);

        result = defaultJsonb.fromJson(new ByteArrayInputStream(json.getBytes(CHARSET)), new TestTypeToken<HashMap<String, String>>() {}.getType());
        assertMapValues(result);
    }

    @Test
    public void testMarshall() throws Exception {
        String expected = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        int len = expected.getBytes(CHARSET).length;

        Map<String, String> strMap = new HashMap<>();
        strMap.put("key1", "value1");
        strMap.put("key2", "value2");

        ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
        defaultJsonb.toJson(strMap, baos);
        assertEquals(expected, baos.toString(CHARSET));

        baos = new ByteArrayOutputStream(len);
        OutputStreamWriter writer = new OutputStreamWriter(baos, Charset.forName(CHARSET));
        defaultJsonb.toJson(strMap, writer);
        writer.close();

        assertEquals(expected, baos.toString(CHARSET));
    }

    private static void assertMapValues(Map<String, String> result) {
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }
}
