/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.DateFormatPojo;
import org.eclipse.yasson.customization.model.DateFormatPojoWithClassLevelFormatter;
import org.eclipse.yasson.customization.model.TrimmedDatePojo;
import org.eclipse.yasson.internal.JsonBindingBuilder;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.eclipse.yasson.YassonConfig.ZERO_TIME_PARSE_DEFAULTING;

/**
 * Tests customization of date fields via {@link jakarta.json.bind.annotation.JsonbDateFormat} annotation
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class JsonbDateFormatterTest {

    @Test
    public void testCustomDateFormatSerialization() {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
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

        String expectedJson = "{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"plainDateField\":\"2017-03-03T11:11:10Z[UTC]\",\"setterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"setterFormattedDateField\":\"2017-03-03T11:11:10Z[UTC]\"}";

        assertEquals(expectedJson, defaultJsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserialization() {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .build();

        DateFormatPojo result = defaultJsonb.fromJson("{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 $$ 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"2017-03-03T11:11:10\",\"plainDateField\":\"2017-03-03T11:11:10\",\"setterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"setterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\"}", DateFormatPojo.class);

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
    public void testCustomDateFormatSerializationWithClassLevelDateFormatterDefined() {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
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

        assertEquals(expectedJson, defaultJsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserializationWithClassLevelDateFormatterDefined() {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .build();

        DateFormatPojoWithClassLevelFormatter result = defaultJsonb.fromJson("{\"formattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterAndSetterAndFieldFormattedDateField\":\"11:11:10 $$ 03-03-2017\",\"getterAndSetterFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"getterAndFieldFormattedDateField\":\"11:11:10 ^^ 03-03-2017\",\"getterFormattedDateField\":\"11:11:10 ^ 03-03-2017\",\"plainDateField\":\"11:11:10 ^ 03-03-2017\",\"setterAndFieldFormattedDateField\":\"11:11:10 <> 03-03-2017\",\"setterFormattedDateField\":\"11:11:10 ^^ 03-03-2017\"}", DateFormatPojoWithClassLevelFormatter.class);

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
    public void testTrimmedDateParsing() {
        ZoneId utcZone = ZoneId.of("UTC");
        ZonedDateTime zdt = ZonedDateTime.of(2018, 1, 30, 0, 0, 0, 0, utcZone);

        TrimmedDatePojo pojo = new TrimmedDatePojo();
        pojo.setDate(Date.from(zdt.toInstant()));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(utcZone));
        calendar.setTime(pojo.getDate());
        pojo.setCalendar(calendar);
        pojo.setLocalDateTime(zdt.toLocalDateTime());
        pojo.setZonedDateTime(zdt);
        pojo.setZonedDateTimeNanosOfDay(zdt.plusNanos(3000));
        pojo.setZonedDateTimeHoursAndSeconds(zdt);
        pojo.setZonedDateTimeOverriddenZone(zdt.withZoneSameInstant(ZoneId.of("Europe/Paris")));
        pojo.setZonedInstant(zdt.withZoneSameInstant(ZoneId.of("Europe/Paris")).toInstant());

        Jsonb zeroDefaultingJsonb = new JsonBindingBuilder()
                .withConfig(new JsonbConfig().setProperty(ZERO_TIME_PARSE_DEFAULTING, true))
                .build();

        String serialized = zeroDefaultingJsonb.toJson(pojo);
        assertEquals(
                "{\"calendar\":\"2018.01.30\",\"date\":\"2018.01.30\",\"localDateTime\":\"2018.01.30\",\"zonedDateTime\":\"2018.01.30\",\"zonedDateTimeHoursAndSeconds\":\"2018.01.30 00:00\",\"zonedDateTimeNanosOfDay\":\"2018.01.30 3000\",\"zonedDateTimeOverriddenZone\":\"2018.01.30 Europe/Paris\",\"zonedInstant\":\"2018.01.30 UTC\"}",
                serialized);

        String jsonToDeserialize = "{\"calendar\":\"2018.01.30\",\"date\":\"2018.01.30\",\"localDateTime\":\"2018.01.30\",\"zonedDateTime\":\"2018.01.30\",\"zonedDateTimeHoursAndSeconds\":\"2018.01.30 12:15\",\"zonedDateTimeNanosOfDay\":\"2018.01.30 9000\",\"zonedDateTimeOverriddenZone\":\"2018.01.30 Europe/Prague\",\"zonedInstant\":\"2018.01.30 Europe/Prague\"}";
        TrimmedDatePojo trimmedDatePojo = zeroDefaultingJsonb.fromJson(jsonToDeserialize, TrimmedDatePojo.class);

        //Nanos are overridden in json for deserialization. Tests that defaulting hour/minute/second does not affect other units.
        assertEquals(2018, trimmedDatePojo.getZonedDateTimeNanosOfDay().getYear());
        assertEquals(1, trimmedDatePojo.getZonedDateTimeNanosOfDay().getMonthValue());
        assertEquals(30, trimmedDatePojo.getZonedDateTimeNanosOfDay().getDayOfMonth());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getHour());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getMinute());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getSecond());
        assertEquals(9000, trimmedDatePojo.getZonedDateTimeNanosOfDay().getNano());
        assertEquals(utcZone, trimmedDatePojo.getZonedDateTimeNanosOfDay().getZone());

        //Test trimmed zoned date time have correct values.
        assertEquals(2018, trimmedDatePojo.getZonedDateTime().getYear());
        assertEquals(1, trimmedDatePojo.getZonedDateTime().getMonthValue());
        assertEquals(30, trimmedDatePojo.getZonedDateTime().getDayOfMonth());
        assertEquals(0, trimmedDatePojo.getZonedDateTime().getHour());
        assertEquals(0, trimmedDatePojo.getZonedDateTime().getMinute());
        assertEquals(0, trimmedDatePojo.getZonedDateTime().getSecond());
        assertEquals(utcZone, trimmedDatePojo.getZonedDateTime().getZone());


        //Zone is overridden in JSON, causing
        assertEquals(2018, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getYear());
        assertEquals(1, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getMonthValue());
        assertEquals(30, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getDayOfMonth());
        assertEquals(12, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getHour());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getMinute());
        assertEquals(15, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getSecond());
        assertEquals(utcZone, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getZone());

        //Defaulting UTC zone is overridden in JSON.
        assertEquals(2018, trimmedDatePojo.getZonedDateTimeOverriddenZone().getYear());
        assertEquals(1, trimmedDatePojo.getZonedDateTimeOverriddenZone().getMonthValue());
        assertEquals(30, trimmedDatePojo.getZonedDateTimeOverriddenZone().getDayOfMonth());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getHour());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getMinute());
        assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getSecond());
        assertEquals(ZoneId.of("Europe/Prague"), trimmedDatePojo.getZonedDateTimeOverriddenZone().getZone());

        //Tests LocalDateTime trimmed values
        assertEquals(2018, trimmedDatePojo.getLocalDateTime().getYear());
        assertEquals(1, trimmedDatePojo.getLocalDateTime().getMonthValue());
        assertEquals(30, trimmedDatePojo.getLocalDateTime().getDayOfMonth());
        assertEquals(0, trimmedDatePojo.getLocalDateTime().getHour());
        assertEquals(0, trimmedDatePojo.getLocalDateTime().getMinute());
        assertEquals(0, trimmedDatePojo.getLocalDateTime().getSecond());

        //Test date and instant have correct time.
        assertEquals(zdt.toInstant().toEpochMilli(), trimmedDatePojo.getDate().getTime());
        assertEquals(zdt.withZoneSameLocal(ZoneId.of("Europe/Prague")).toInstant().toEpochMilli(), trimmedDatePojo.getZonedInstant().toEpochMilli());

        //Test calendar instance
        //Tests LocalDateTime trimmed values
        assertEquals(2018, trimmedDatePojo.getCalendar().get(Calendar.YEAR));
        assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.MONTH));
        assertEquals(30, trimmedDatePojo.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.HOUR_OF_DAY));
        assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.MINUTE));
        assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.SECOND));
    }
}
