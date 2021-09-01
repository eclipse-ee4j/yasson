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

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.eclipse.yasson.Jsonbs.bindingJsonb;
import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Patrick Reinhart
 */
public class SimpleNumberKeyMapTest {

    @Test
    public void testSimpleByteMapSerialize() {
        final ByteMapWrapper wrapper = new ByteMapWrapper();
        wrapper.setValues(singletonMap(Byte.valueOf("1"), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleByteMapDeserializer() {
        final ByteMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", ByteMapWrapper.class);
        assertEquals(singletonMap(Byte.valueOf("1"), "abc"), wrapper.getValues());
    }

    @Test
    public void testSimpleDoubleMapSerialize() {
        final DoubleMapWrapper wrapper = new DoubleMapWrapper();
        wrapper.setValues(singletonMap(Double.valueOf("1.0"), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1.0\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleDoubleMapDeserializer() {
        final DoubleMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", DoubleMapWrapper.class);
        assertEquals(singletonMap(Double.valueOf("1"), "abc"), wrapper.getValues());
    }

    @Test
    public void testSimpleFloatMapSerialize() {
        final FloatMapWrapper wrapper = new FloatMapWrapper();
        wrapper.setValues(singletonMap(Float.valueOf("1.0"), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1.0\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleFloatMapDeserializer() {
        final FloatMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", FloatMapWrapper.class);
        assertEquals(singletonMap(Float.valueOf("1.0"), "abc"), wrapper.getValues());
    }

    @Test
    public void testSimpleIntegerMapSerialize() {
        final IntegerMapWrapper wrapper = new IntegerMapWrapper();
        wrapper.setValues(singletonMap(Integer.valueOf(1), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleIntegerMapDeserializer() {
        final IntegerMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", IntegerMapWrapper.class);
        assertEquals(singletonMap(Integer.valueOf(1), "abc"), wrapper.getValues());
    }

    @Test
    public void testSimpleLongMapSerialize() {
        final LongMapWrapper wrapper = new LongMapWrapper();
        wrapper.setValues(singletonMap(Long.valueOf("1"), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleLongMapDeserializer() {
        final LongMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", LongMapWrapper.class);
        assertEquals(singletonMap(Long.valueOf("1"), "abc"), wrapper.getValues());
    }

    @Test
    public void testSimpleShortMapSerialize() {
        final ShortMapWrapper wrapper = new ShortMapWrapper();
        wrapper.setValues(singletonMap(Short.valueOf("1"), "abc"));
        bindingJsonb.toJson(wrapper);
        final String val = bindingJsonb.toJson(wrapper);
        assertEquals("{\"values\":{\"1\":\"abc\"}}", val);
    }

    @Test
    public void testSimpleShortMapDeserializer() {
        final ShortMapWrapper wrapper = defaultJsonb.fromJson("{\"values\":{\"1\":\"abc\"}}", ShortMapWrapper.class);
        assertEquals(singletonMap(Short.valueOf("1"), "abc"), wrapper.getValues());
    }

    public static class ByteMapWrapper {
        public Map<Byte, String> values;

        public Map<Byte, String> getValues() {
            return values;
        }

        public void setValues(Map<Byte, String> values) {
            this.values = values;
        }
    }

    public static class DoubleMapWrapper {
        public Map<Double, String> values;

        public Map<Double, String> getValues() {
            return values;
        }

        public void setValues(Map<Double, String> values) {
            this.values = values;
        }
    }

    public static class FloatMapWrapper {
        public Map<Float, String> values;

        public Map<Float, String> getValues() {
            return values;
        }

        public void setValues(Map<Float, String> values) {
            this.values = values;
        }
    }

    public static class IntegerMapWrapper {
        public Map<Integer, String> values;

        public Map<Integer, String> getValues() {
            return values;
        }

        public void setValues(Map<Integer, String> values) {
            this.values = values;
        }
    }

    public static class LongMapWrapper {
        public Map<Long, String> values;

        public Map<Long, String> getValues() {
            return values;
        }

        public void setValues(Map<Long, String> values) {
            this.values = values;
        }
    }

    public static class ShortMapWrapper {
        public Map<Short, String> values;

        public Map<Short, String> getValues() {
            return values;
        }

        public void setValues(Map<Short, String> values) {
            this.values = values;
        }
    }
}
