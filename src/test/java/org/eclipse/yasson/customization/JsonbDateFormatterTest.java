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
 *      Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/

package org.eclipse.yasson.customization;

import org.eclipse.yasson.customization.model.DateFormatPojo;
import org.eclipse.yasson.customization.model.DateFormatPojoWithClassLevelFormatter;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Tests customization of date fields via {@link javax.json.bind.annotation.JsonbDateFormat} annotation
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class JsonbDateFormatterTest {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testCustomDateFormatSerialization() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .build();

        DateFormatPojo dateFormatPojo = new DateFormatPojo();
        dateFormatPojo.plainDateField = timeCalendar.getTime();
        dateFormatPojo.formattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.setterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndFieldFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.setterAndFieldFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndSetterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndSetterAndFieldFormattedDateField = timeCalendar.getTime();

        String expectedJson = "{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"plainDateField\":\"2017-03-03T11:11:10\",\"setterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"setterFormattedDateField\":\"2017-03-03T11:11:10\"}";

        assertEquals(expectedJson, jsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserialization() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .build();

        DateFormatPojo result = jsonb.fromJson("{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 $$ 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"2017-03-03T11:11:10\",\"plainDateField\":\"2017-03-03T11:11:10\",\"setterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"setterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\"}", DateFormatPojo.class);

        assertEquals(timeCalendar.getTime(), result.plainDateField);
        assertEquals(timeCalendar.getTime(), result.formattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.setterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndFieldFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.setterAndFieldFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndSetterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndSetterAndFieldFormattedDateField);
    }

    @Test
    public void testCustomDateFormatSerializationWithClassLevelDateFormatterDefined() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .build();

        DateFormatPojoWithClassLevelFormatter dateFormatPojo = new DateFormatPojoWithClassLevelFormatter();
        dateFormatPojo.plainDateField = timeCalendar.getTime();
        dateFormatPojo.formattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.setterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndFieldFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.setterAndFieldFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndSetterFormattedDateField = timeCalendar.getTime();
        dateFormatPojo.getterAndSetterAndFieldFormattedDateField = timeCalendar.getTime();

        String expectedJson = "{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"plainDateField\":\"11:11:10 ^ 03-03-2017\",\"setterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"setterFormattedDateField\":\"11:11:10 ^ 03-03-2017\"}";

        assertEquals(expectedJson, jsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserializationWithClassLevelDateFormatterDefined() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .build();

        DateFormatPojoWithClassLevelFormatter result = jsonb.fromJson("{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 $$ 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"11:11:10 ^ 03-03-2017\",\"plainDateField\":\"11:11:10 ^ 03-03-2017\",\"setterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"setterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\"}", DateFormatPojoWithClassLevelFormatter.class);

        assertEquals(timeCalendar.getTime(), result.plainDateField);
        assertEquals(timeCalendar.getTime(), result.formattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.setterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndFieldFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.setterAndFieldFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndSetterFormattedDateField);
        assertEquals(timeCalendar.getTime(), result.getterAndSetterAndFieldFormattedDateField);
    }
}
