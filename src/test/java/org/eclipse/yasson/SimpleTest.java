/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson;

import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public class SimpleTest {

    public static class StringWrapper {
        public String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testSimpleSerialize() {
        Jsonb jsonb = (new JsonBindingBuilder()).build();
        final StringWrapper wrapper = new StringWrapper();
        wrapper.setValue("abc");
        jsonb.toJson(wrapper);
        final String val = jsonb.toJson(wrapper);
        assertEquals("{\"value\":\"abc\"}", val);
    }

    @Test
    public void testSimpleDeserializer() {
        final StringWrapper stringWrapper = jsonb.fromJson("{\"value\":\"abc\"}", StringWrapper.class);
        assertEquals("abc", stringWrapper.getValue());
    }
}
