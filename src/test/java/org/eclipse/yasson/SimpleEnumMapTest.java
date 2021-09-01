/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.awt.geom.RoundRectangle2D;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import static java.math.RoundingMode.CEILING;
import static java.util.Collections.singletonMap;
import static org.eclipse.yasson.Jsonbs.bindingJsonb;
import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Patrick Reinhart
 */
public class SimpleEnumMapTest {

    @Test
    public void testSimpleSerialize() {
        final MapWrapper wrapper = new MapWrapper();
        wrapper.setValues(singletonMap(CEILING, "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"CEILING\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleDeserializer() {
        final MapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"CEILING\":\"abc\"}}", MapWrapper.class);
        assertEquals(singletonMap(CEILING, "abc"), wrapper.getValues());
    }
    
    
    public static class MapWrapper {
        public Map<RoundingMode, String> values;

        public Map<RoundingMode, String> getValues() {
            return values;
        }

        public void setValues(Map<RoundingMode, String> values) {
            this.values = values;
        }
    }
}
