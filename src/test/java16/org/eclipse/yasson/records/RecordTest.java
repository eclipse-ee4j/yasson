/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.records;

import org.eclipse.yasson.Jsonbs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordTest {

    @Test
    public void testRecordProcessing() {
        Car car = new Car("skoda", "green");
        String expected = "{\"colorChanged\":\"green\",\"typeChanged\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        Car deserialized = Jsonbs.defaultJsonb.fromJson(expected, Car.class);
        assertEquals(car, deserialized);
    }

    @Test
    public void testRecordProcessingWithoutJsonbProperties() {
        CarWithoutAnnotations car = new CarWithoutAnnotations("skoda", "green");
        String expected = "{\"color\":\"green\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        CarWithoutAnnotations deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithoutAnnotations.class);
        assertEquals(car, deserialized);
    }

}
