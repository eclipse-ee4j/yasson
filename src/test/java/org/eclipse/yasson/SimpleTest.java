/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.bindingJsonb;
import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public class SimpleTest {

    @Test
    public void testSimpleSerialize() {
        final StringWrapper wrapper = new StringWrapper();
        wrapper.setValue("abc");
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"value\":\"abc\"}", val);
    }

    @Test
    public void testSimpleDeserializer() {
        final StringWrapper stringWrapper = defaultJsonb.fromJson("{\"value\":\"abc\"}", StringWrapper.class);
        assertEquals("abc", stringWrapper.value);
    }

    public static class StringWrapper {

        public String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
