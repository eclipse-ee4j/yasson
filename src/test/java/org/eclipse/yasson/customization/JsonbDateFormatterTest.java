/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.YassonProperties;
import org.eclipse.yasson.customization.model.DateFormatPojo;
import org.eclipse.yasson.customization.model.DateFormatPojoWithClassLevelFormatter;
import org.eclipse.yasson.customization.model.TrimmedDatePojo;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

        assertEquals(expectedJson, jsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserialization() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
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

        assertEquals(expectedJson, jsonb.toJson(dateFormatPojo));
    }

    @Test
    public void testCustomDateFormatDeserializationWithClassLevelDateFormatterDefined() throws Exception {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2017, Calendar.MARCH, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
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
                .withConfig(new JsonbConfig().setProperty(YassonProperties.ZERO_TIME_PARSE_DEFAULTING, true))
                .build();

        String serialized = zeroDefaultingJsonb.toJson(pojo);
        Assert.assertEquals(
                "{\"calendar\":\"2018.01.30\",\"date\":\"2018.01.30\",\"localDateTime\":\"2018.01.30\",\"zonedDateTime\":\"2018.01.30\",\"zonedDateTimeHoursAndSeconds\":\"2018.01.30 00:00\",\"zonedDateTimeNanosOfDay\":\"2018.01.30 3000\",\"zonedDateTimeOverriddenZone\":\"2018.01.30 Europe/Paris\",\"zonedInstant\":\"2018.01.30 UTC\"}",
                serialized);

        String jsonToDeserialize = "{\"calendar\":\"2018.01.30\",\"date\":\"2018.01.30\",\"localDateTime\":\"2018.01.30\",\"zonedDateTime\":\"2018.01.30\",\"zonedDateTimeHoursAndSeconds\":\"2018.01.30 12:15\",\"zonedDateTimeNanosOfDay\":\"2018.01.30 9000\",\"zonedDateTimeOverriddenZone\":\"2018.01.30 Europe/Prague\",\"zonedInstant\":\"2018.01.30 Europe/Prague\"}";
        TrimmedDatePojo trimmedDatePojo = zeroDefaultingJsonb.fromJson(jsonToDeserialize, TrimmedDatePojo.class);

        //Nanos are overridden in json for deserialization. Tests that defaulting hour/minute/second does not affect other units.
        Assert.assertEquals(2018, trimmedDatePojo.getZonedDateTimeNanosOfDay().getYear());
        Assert.assertEquals(1, trimmedDatePojo.getZonedDateTimeNanosOfDay().getMonthValue());
        Assert.assertEquals(30, trimmedDatePojo.getZonedDateTimeNanosOfDay().getDayOfMonth());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getHour());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getMinute());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeNanosOfDay().getSecond());
        Assert.assertEquals(9000, trimmedDatePojo.getZonedDateTimeNanosOfDay().getNano());
        Assert.assertEquals(utcZone, trimmedDatePojo.getZonedDateTimeNanosOfDay().getZone());

        //Test trimmed zoned date time have correct values.
        Assert.assertEquals(2018, trimmedDatePojo.getZonedDateTime().getYear());
        Assert.assertEquals(1, trimmedDatePojo.getZonedDateTime().getMonthValue());
        Assert.assertEquals(30, trimmedDatePojo.getZonedDateTime().getDayOfMonth());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTime().getHour());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTime().getMinute());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTime().getSecond());
        Assert.assertEquals(utcZone, trimmedDatePojo.getZonedDateTime().getZone());


        //Zone is overridden in JSON, causing
        Assert.assertEquals(2018, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getYear());
        Assert.assertEquals(1, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getMonthValue());
        Assert.assertEquals(30, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getDayOfMonth());
        Assert.assertEquals(12, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getHour());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getMinute());
        Assert.assertEquals(15, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getSecond());
        Assert.assertEquals(utcZone, trimmedDatePojo.getZonedDateTimeHoursAndSeconds().getZone());

        //Defaulting UTC zone is overridden in JSON.
        Assert.assertEquals(2018, trimmedDatePojo.getZonedDateTimeOverriddenZone().getYear());
        Assert.assertEquals(1, trimmedDatePojo.getZonedDateTimeOverriddenZone().getMonthValue());
        Assert.assertEquals(30, trimmedDatePojo.getZonedDateTimeOverriddenZone().getDayOfMonth());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getHour());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getMinute());
        Assert.assertEquals(0, trimmedDatePojo.getZonedDateTimeOverriddenZone().getSecond());
        Assert.assertEquals(ZoneId.of("Europe/Prague"), trimmedDatePojo.getZonedDateTimeOverriddenZone().getZone());

        //Tests LocalDateTime trimmed values
        Assert.assertEquals(2018, trimmedDatePojo.getLocalDateTime().getYear());
        Assert.assertEquals(1, trimmedDatePojo.getLocalDateTime().getMonthValue());
        Assert.assertEquals(30, trimmedDatePojo.getLocalDateTime().getDayOfMonth());
        Assert.assertEquals(0, trimmedDatePojo.getLocalDateTime().getHour());
        Assert.assertEquals(0, trimmedDatePojo.getLocalDateTime().getMinute());
        Assert.assertEquals(0, trimmedDatePojo.getLocalDateTime().getSecond());

        //Test date and instant have correct time.
        Assert.assertEquals(zdt.toInstant().toEpochMilli(), trimmedDatePojo.getDate().getTime());
        Assert.assertEquals(zdt.withZoneSameLocal(ZoneId.of("Europe/Prague")).toInstant().toEpochMilli(), trimmedDatePojo.getZonedInstant().toEpochMilli());

        //Test calendar instance
        //Tests LocalDateTime trimmed values
        Assert.assertEquals(2018, trimmedDatePojo.getCalendar().get(Calendar.YEAR));
        Assert.assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.MONTH));
        Assert.assertEquals(30, trimmedDatePojo.getCalendar().get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.MINUTE));
        Assert.assertEquals(0, trimmedDatePojo.getCalendar().get(Calendar.SECOND));
    }
}
