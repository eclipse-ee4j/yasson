/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.JsonbException;

import org.eclipse.yasson.Jsonbs;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void testRecordProcessingWithExtraMethod() {
        CarWithExtraMethod car = new CarWithExtraMethod("skoda", "green");
        String expected = "{\"color\":\"green\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        CarWithExtraMethod deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithExtraMethod.class);
        assertEquals(car, deserialized);
    }

    @Test
    public void testRecordMultipleConstructors() {
        CarWithMultipleConstructors car = new CarWithMultipleConstructors("skoda");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        JsonbException jsonbException = assertThrows(JsonbException.class,
                                                     () -> Jsonbs.defaultJsonb.fromJson(expected,
                                                                                        CarWithMultipleConstructors.class));
        String expectedMessage = Messages.getMessage(MessageKeys.RECORD_MULTIPLE_CONSTRUCTORS, CarWithMultipleConstructors.class);
        assertEquals(expectedMessage, jsonbException.getMessage());
    }

    @Test
    public void testRecordMultipleConstructorsWithJsonbCreator() {
        CarWithMultipleConstructorsAndCreator car = new CarWithMultipleConstructorsAndCreator("skoda");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        CarWithMultipleConstructorsAndCreator deserialized =  Jsonbs.defaultJsonb
                .fromJson(expected, CarWithMultipleConstructorsAndCreator.class);
        assertEquals(car, deserialized);
    }

    @Test
    public void testRecordJsonbCreator() {
        CarWithCreator car = new CarWithCreator("skoda", "red");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        CarWithCreator deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithCreator.class);
        assertEquals(car, deserialized);
    }

}
