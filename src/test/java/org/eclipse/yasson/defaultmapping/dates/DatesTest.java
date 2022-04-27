/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.dates;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonConfig;
import org.eclipse.yasson.defaultmapping.dates.model.CalendarPojo;
import org.eclipse.yasson.defaultmapping.dates.model.ClassLevelDateAnnotation;
import org.eclipse.yasson.defaultmapping.dates.model.DatePojo;
import org.eclipse.yasson.defaultmapping.dates.model.DateWithZonePojo;
import org.eclipse.yasson.defaultmapping.dates.model.InstantPojo;
import org.eclipse.yasson.defaultmapping.dates.model.LocalDatePojo;
import org.eclipse.yasson.defaultmapping.dates.model.LocalDateTimePojo;
import org.eclipse.yasson.defaultmapping.dates.model.LocalTimePojo;
import org.eclipse.yasson.defaultmapping.dates.model.MonthDayPojo;
import org.eclipse.yasson.defaultmapping.dates.model.OffsetDateTimePojo;
import org.eclipse.yasson.defaultmapping.dates.model.OffsetTimePojo;
import org.eclipse.yasson.defaultmapping.dates.model.YearMonthPojo;
import org.eclipse.yasson.defaultmapping.dates.model.ZonedDateTimePojo;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.eclipse.yasson.internal.deserializer.types.SqlDateDeserializer;
import org.junit.jupiter.api.Test;

import static org.eclipse.yasson.Jsonbs.bindingJsonb;
import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class contains tests for marshalling/unmarshalling dates.
 *
 * @author Dmitry Kornilov
 */
public class DatesTest {
    
    private static LocalDate localDate = LocalDate.of(2018, 1, 31);

    @SuppressWarnings("serial")
	public static class LocalDateObj implements Serializable {
        public LocalDate date = localDate;
    }

    @SuppressWarnings("serial")
	public static class SqlDateObj implements Serializable {
        public java.sql.Date sqlDate = java.sql.Date.valueOf("2018-01-31");
        //no way for runtime to choose java.sql.Date deserializer here without a hint
        @JsonbTypeDeserializer(SqlDateDeserializer.class)
        public java.util.Date utilDate = java.sql.Date.valueOf("2018-01-31");

    }
    
    public static class SqlDateFormatted {
        @JsonbDateFormat(value = "yyyy-MM-dd")
        public java.sql.Date sqlDate;
    }

    @Test
    public void testSqlTimestamp() {
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());

        String json = bindingJsonb.toJson(expectedTimestamp);

        Timestamp timestamp = bindingJsonb.fromJson(json, Timestamp.class);
        assertEquals(expectedTimestamp, timestamp);
    }

    @Test
    public void testMarshallSqlDate() {
        String jsonString = bindingJsonb.toJson(new SqlDateObj());
        assertEquals("{\"sqlDate\":\"2018-01-31Z\",\"utilDate\":\"2018-01-31Z\"}", jsonString);
    }

    @Test
    public void testUnmarshallSqlDate() {
        SqlDateObj result = bindingJsonb.fromJson("{\"sqlDate\":\"2018-01-31Z\",\"utilDate\":\"2018-01-31Z\"}", SqlDateObj.class);
        assertEquals("2018-01-31", result.sqlDate.toString());
        assertEquals("2018-01-31", result.utilDate.toString());
    }
    
    @Test
    public void testSqlDateTimeZonesFormatted() {
        testSqlDateWithTZFormatted(TimeZone.getTimeZone(ZoneId.of("Europe/Sofia")));
        testSqlDateWithTZFormatted(TimeZone.getTimeZone(ZoneId.of("US/Hawaii")));
        testSqlDateWithTZFormatted(TimeZone.getTimeZone(ZoneId.of("Australia/Sydney")));
    }
    
    private void testSqlDateWithTZFormatted(TimeZone tz) {
        final TimeZone originalTZ = TimeZone.getDefault();
        TimeZone.setDefault(tz);
        try {
            Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withDateFormat("yyyy-MM-dd", Locale.getDefault()));
            java.sql.Date d = java.sql.Date.valueOf("1966-11-04");
            String json = jsonb.toJson(d);
            assertEquals("\"1966-11-04\"", json);
            assertEquals("1966-11-04", jsonb.fromJson(json, java.sql.Date.class).toString());
        } finally {
            TimeZone.setDefault(originalTZ);
        }
    }
    
    @Test
    public void testSqlDateTimeZonesMillis() {
        testSqlDateTimeZonesMillis(TimeZone.getTimeZone(ZoneId.of("Europe/Sofia")), -99712800000L);
        testSqlDateTimeZonesMillis(TimeZone.getTimeZone(ZoneId.of("US/Hawaii")), -99669600000L);
        testSqlDateTimeZonesMillis(TimeZone.getTimeZone(ZoneId.of("Australia/Sydney")), -99741600000L);
    }
    
    private void testSqlDateTimeZonesMillis(TimeZone tz, long expectedMs) {
        final TimeZone originalTZ = TimeZone.getDefault();
        TimeZone.setDefault(tz);
        JsonbConfig jsonbConfig = new JsonbConfig().withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.getDefault());
        try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
            java.sql.Date d = java.sql.Date.valueOf("1966-11-04");
            String json = jsonb.toJson(d);
            assertEquals("" + expectedMs, json);
            assertEquals(d, jsonb.fromJson(json, java.sql.Date.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TimeZone.setDefault(originalTZ);
        }
    }
    
    @Test
    public void testSqlDateTimeZones() {
        testSqlDateWithTZ(TimeZone.getTimeZone(ZoneId.of("Europe/Sofia")));
        testSqlDateWithTZ(TimeZone.getTimeZone(ZoneId.of("US/Hawaii")));
        testSqlDateWithTZ(TimeZone.getTimeZone(ZoneId.of("Australia/Sydney")));
    }
    
    private void testSqlDateWithTZ(TimeZone tz) {
        final TimeZone originalTZ = TimeZone.getDefault();
        TimeZone.setDefault(tz);
        try {
            java.sql.Date d = java.sql.Date.valueOf("1966-11-04");
            String json = defaultJsonb.toJson(d);
            assertEquals("\"1966-11-04Z\"", json);
            assertEquals("1966-11-04", defaultJsonb.fromJson(json, java.sql.Date.class).toString());
        } finally {
            TimeZone.setDefault(originalTZ);
        }
    }

    @Test
    public void testMarshallLocalDate() {
        String jsonString = bindingJsonb.toJson(new LocalDateObj());
        if (jsonString.contains("T")) {
            fail("JSON contains time for a non Date that doesn't include time");
        }
    }

    @Test
    public void testUnmarshallLocalDate() {
        LocalDateObj localDateObj = bindingJsonb.fromJson("{\"date\":\"2018-01-31\"}", LocalDateObj.class);
        assertEquals(localDate, localDateObj.date);
    }

    @Test
    public void testDate() throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date parsedDate = sdf.parse("04.03.2015");

        final DatePojo pojo = new DatePojo();
        pojo.customDate = parsedDate;
        pojo.defaultFormatted = parsedDate;
        pojo.millisFormatted = parsedDate;

        // marshal to ISO format
        final String expected = "{\"defaultFormatted\":\"2015-03-04T00:00:00Z[UTC]\"," +
                "\"millisFormatted\":" + parsedDate.getTime()+ "," +
                "\"customDate\":\"00:00:00 | 04-03-2015\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final DatePojo result = bindingJsonb.fromJson(expected, DatePojo.class);
        assertEquals(parsedDate, result.customDate);
        assertEquals(parsedDate, result.defaultFormatted);
        assertEquals(parsedDate, result.millisFormatted);
    }

    @Test
    public void testDateWithZoneOffset() throws ParseException {
        // Test for Yasson-172
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        final Date parsedDate = sdf.parse("2018-11-02T00:00:00+01:00");
    	
    	String jsonDateWithZone = "{\"dateWithZone\":\"2018-11-02T00:00:00+01:00\"}";
    	final DateWithZonePojo result = bindingJsonb.fromJson(jsonDateWithZone, DateWithZonePojo.class);
    	
    	assertEquals(parsedDate, result.dateWithZone);
    }
    
    @Test
    public void testDateWithZoneId() throws ParseException {
        // Test for Yasson-172
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        final Date parsedDate = sdf.parse("2018-11-02T00:00:00+01:00");
    	
    	String jsonDateWithZone = "{\"dateWithZone\":\"2018-11-02T00:00:00+01:00[Europe/Berlin]\"}";
    	final DateWithZonePojo result = bindingJsonb.fromJson(jsonDateWithZone, DateWithZonePojo.class);
    	
    	assertEquals(parsedDate, result.dateWithZone);
    }
    
    @Test
    public void testCalendar() {
        final Calendar timeCalendar = new Calendar.Builder()
                .setDate(2015, Calendar.APRIL, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("Europe/Prague"))
                .build();

        final CalendarPojo calendarPojo = new CalendarPojo();
        calendarPojo.customCalendar = timeCalendar;
        calendarPojo.defaultFormatted = timeCalendar;
        calendarPojo.millisFormatted = timeCalendar;

        // marshal to ISO_DATE
        final String expected = "{\"defaultFormatted\":\"2015-04-03T11:11:10+02:00[Europe/Prague]\"," +
                "\"millisFormatted\":1428052270000," +
                "\"customCalendar\":\"11:11:10 | 03-04-2015, +0200\"}";
        assertEquals(expected, bindingJsonb.toJson(calendarPojo));

        // marshal to ISO_DATE_TIME
        final CalendarPojo result = bindingJsonb.fromJson(expected, CalendarPojo.class);
        assertEquals(timeCalendar.getTime(), result.customCalendar.getTime());
        assertEquals(timeCalendar.getTime(), result.millisFormatted.getTime());
        assertEquals(timeCalendar.getTime(), result.defaultFormatted.getTime());
    }

    @Test
    public void testCalendarWithoutTime() {
        ScalarValueWrapper<Calendar> result = bindingJsonb.fromJson("{\"value\":\"2015-04-03+01:00\"}", new TestTypeToken<ScalarValueWrapper<Calendar>>(){}.getType());
        assertEquals(2015, result.getValue().get(Calendar.YEAR));
        assertEquals(3, result.getValue().get(Calendar.MONTH));
        assertEquals(3, result.getValue().get(Calendar.DAY_OF_MONTH));
        assertEquals(0, result.getValue().get(Calendar.HOUR_OF_DAY));
        assertEquals(0, result.getValue().get(Calendar.MINUTE));
        assertEquals(0, result.getValue().get(Calendar.SECOND));
        assertEquals("GMT+01:00", result.getValue().getTimeZone().toZoneId().toString());
    }

    @Test
    public void testCalendarWithNonDefaultTimeZone() {
        final ZoneId zoneId = ZoneId.of("Europe/Prague");
        final Calendar cal = new Calendar.Builder()
                .setDate(2015, Calendar.APRIL, 3)
                .setTimeOfDay(10, 10, 10)
                .setTimeZone(TimeZone.getTimeZone(zoneId))
                .build();

        final CalendarPojo calendarPojo = new CalendarPojo();
        calendarPojo.customCalendar = cal;
        calendarPojo.defaultFormatted = cal;
        calendarPojo.millisFormatted = cal;

        // marshal to ISO_DATE
        final String expected = "{\"defaultFormatted\":\"2015-04-03T10:10:10+02:00[" + zoneId + "]\"," +
                "\"millisFormatted\":" + cal.getTimeInMillis() + "," +
                "\"customCalendar\":\"10:10:10 | 03-04-2015, +0200\"}";
        assertEquals(expected, bindingJsonb.toJson(calendarPojo));

        // marshal to ISO_DATE_TIME
        final CalendarPojo result = bindingJsonb.fromJson(expected, CalendarPojo.class);
        assertEquals(cal.getTime(), result.customCalendar.getTime());
        assertEquals(cal.getTime(), result.millisFormatted.getTime());
        assertEquals(cal.getTime(), result.defaultFormatted.getTime());
    }

    @Test
    public void testMarshalGregorianCalendar() {
        final Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.set(2015, Calendar.APRIL, 3);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        // marshal to ISO_DATE
        assertEquals("{\"value\":\"2015-04-03Z\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(cal)));

        // marshal to ISO_DATE_TIME
        final Calendar dateTimeGregorianCalendar = new Calendar.Builder().setDate(2015, 3, 3)
                .setTimeZone(TimeZone.getTimeZone("Europe/Prague"))
                .build();
        assertEquals("{\"value\":\"2015-04-03T00:00:00+02:00[Europe/Prague]\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(dateTimeGregorianCalendar)));
    }

    @Test
    public void testMarshalTimeZone() {
        assertEquals("{\"value\":\"Europe/Prague\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(TimeZone.getTimeZone("Europe/Prague"))));
        assertEquals("{\"value\":\"Europe/Prague\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(SimpleTimeZone.getTimeZone("Europe/Prague"))));
    }

    @Test
    public void testMarshalInstant() {
        final Instant instant = Instant.parse("2015-03-03T23:00:00Z");
        InstantPojo instantPojo = new InstantPojo(instant);

        final String expected = "{\"defaultFormatted\":\"2015-03-03T23:00:00Z\"," +
                "\"millisFormatted\":1425423600000," +
                "\"instant\":\"23:00:00 | 03-03-2015\"}";
        assertEquals(expected, bindingJsonb.toJson(instantPojo));

        InstantPojo result = bindingJsonb.fromJson(expected, InstantPojo.class);
        assertEquals(instant, result.defaultFormatted);
        assertEquals(instant, result.millisFormatted);
        assertEquals(instant, result.instant);
    }

    @Test
    public void testDateFormattedAsMillisInString() {
        Jsonb jsonb = JsonbBuilder.create(new YassonConfig().withTimeInMillisAsAString(true));
        final Instant instant = Instant.parse("2015-03-03T23:00:00Z");
        InstantPojo instantPojo = new InstantPojo(instant);

        final String expected = "{\"defaultFormatted\":\"2015-03-03T23:00:00Z\"," +
                "\"millisFormatted\":\"1425423600000\"," +
                "\"instant\":\"23:00:00 | 03-03-2015\"}";
        assertEquals(expected, jsonb.toJson(instantPojo));

        InstantPojo result = jsonb.fromJson(expected, InstantPojo.class);
        assertEquals(instant, result.defaultFormatted);
        assertEquals(instant, result.millisFormatted);
        assertEquals(instant, result.instant);
    }

    @Test
    public void testMarshalDuration() {
        assertEquals("{\"value\":\"PT5H4M\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(Duration.ofHours(5).plusMinutes(4))));
    }

    @Test
    public void testMarshalPeriod() {
        final Period period = Period.between(LocalDate.of(1960, Month.JANUARY, 1), LocalDate.of(1970, Month.JANUARY, 1));
        final ScalarValueWrapper<Period> value = new ScalarValueWrapper<>(period);
        assertEquals("{\"value\":\"P10Y\"}", bindingJsonb.toJson(value));
    }

    @Test
    public void testLocalDate() {
        final LocalDate localDate = LocalDate.of(2015, Month.APRIL, 10);
        final LocalDatePojo pojo = new LocalDatePojo(localDate);

        // Get proper milliseconds
        final long millis = localDate.atStartOfDay(ZoneId.of("Z")).toInstant().toEpochMilli();

        final String expected = "{\"defaultFormatted\":\"2015-04-10\"," +
                "\"millisFormatted\":" + millis + "," +
                "\"customLocalDate\":\"10-04-2015\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final LocalDatePojo result = bindingJsonb.fromJson(expected, LocalDatePojo.class);
        assertEquals(localDate, result.customLocalDate);
        assertEquals(localDate, result.millisFormatted);
        assertEquals(localDate, result.defaultFormatted);
    }

    @Test
    public void testlLocalTime() {
        final Jsonb jsonb = getJsonbWithMillisIgnored();
        final LocalTime localTime = LocalTime.of(22, 33);
        final LocalTimePojo localTimePojo = new LocalTimePojo(localTime);
        final String expected = "{\"defaultFormatted\":\"22:33:00\",\"localTime\":\"22:33:00\"}";
        assertEquals(expected, jsonb.toJson(localTimePojo));

        final LocalTimePojo result = jsonb.fromJson(expected, LocalTimePojo.class);
        assertEquals(localTime, result.defaultFormatted);
        assertEquals(localTime, result.localTime);
    }

    private Jsonb getJsonbWithMillisIgnored() {
        final JsonbConfig config = new JsonbConfig();
        config.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return !field.getName().startsWith("millis");
            }

            @Override
            public boolean isVisible(Method method) {
                return false;
            }
        });
        return JsonbBuilder.create(config);
    }

    @Test
    public void testLocalDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(2015, 2, 16, 13, 21);
        final LocalDateTimePojo pojo = new LocalDateTimePojo(dateTime);

        // Get proper milliseconds
        final long millis = dateTime.atZone(ZoneId.of("Z")).toInstant().toEpochMilli();

        final String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00\"," +
                "\"millisFormatted\":" + millis + "," +
                "\"customLocalDate\":\"16-02-2015--00:21:13\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final LocalDateTimePojo result = bindingJsonb.fromJson(expected, LocalDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.millisFormatted);
        assertEquals(dateTime, result.customLocalDate);
    }

    @Test
    public void testLocalDateTimeWithoutConfig() {
        final LocalDateTime dateTime = LocalDateTime.of(2015, 2, 16, 13, 21);
        final ScalarValueWrapper<LocalDateTime> pojo = new ScalarValueWrapper<>();
        pojo.setValue(dateTime);

        final String expected = "{\"value\":\"2015-02-16T13:21:00\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final ScalarValueWrapper<LocalDateTime> result = bindingJsonb.fromJson(expected, new TestTypeToken<ScalarValueWrapper<LocalDateTime>>(){}.getType());
        assertEquals(dateTime, result.getValue());
    }

    @Test
    public void testDifferentConfigsLocalDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(2015, 2, 16, 13, 21);
        final long millis = dateTime.atZone(ZoneId.of("Z")).toInstant().toEpochMilli();
        final ScalarValueWrapper<LocalDateTime> pojo = new ScalarValueWrapper<>();
        pojo.setValue(dateTime);

        final String expected = "{\"value\":\"2015-02-16T13:21:00\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final Jsonb jsonbCustom = JsonbBuilder.create(new JsonbConfig().withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.FRENCH));
        assertEquals("{\"value\":" + millis + "}", jsonbCustom.toJson(pojo));

        ScalarValueWrapper<LocalDateTime> result = bindingJsonb.fromJson(expected, new TestTypeToken<ScalarValueWrapper<LocalDateTime>>(){}.getType());
        assertEquals(dateTime, result.getValue());

        result = jsonbCustom.fromJson("{\"value\":\"" + millis + "\"}", new TestTypeToken<ScalarValueWrapper<LocalDateTime>>(){}.getType());
        assertEquals(dateTime, result.getValue());
    }

    @Test
    public void testZonedDateTime() {
        final ZoneId zone = ZoneId.of("Asia/Almaty");
        final ZonedDateTime dateTime = ZonedDateTime.of(2015, 2, 16, 13, 21, 0, 0, zone);
        final ZonedDateTimePojo pojo = new ZonedDateTimePojo(dateTime);

        final String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00+06:00[" + zone + "]\"," +
                "\"millisFormatted\":" + dateTime.toInstant().toEpochMilli() + "," +
                "\"customZonedDate\":\"+06" + zone + " | 16-02-2015--00:21:13\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final ZonedDateTimePojo result = bindingJsonb.fromJson(expected, ZonedDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.customZonedDate);

        // time zone and seconds omitted
        final ZonedDateTimePojo result1 = bindingJsonb.fromJson("{\"defaultFormatted\":\"2015-02-16T13:21+06:00\"}", ZonedDateTimePojo.class);
        assertEquals(dateTime.getHour(), result1.defaultFormatted.getHour());
        assertEquals(dateTime.getOffset(), result1.defaultFormatted.getOffset());
    }

    @Test
    public void testMarshalZoneId() {
        assertEquals("{\"value\":\"Europe/Prague\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(ZoneId.of("Europe/Prague"))));
    }

    @Test
    public void testMarshalZoneOffset() {
        assertEquals("{\"value\":\"+02:00\"}", bindingJsonb.toJson(new ScalarValueWrapper<>(ZoneOffset.of("+02:00"))));
    }

    @Test
    public void testMarshalOffsetDateTime() {
        final OffsetDateTime dateTime = OffsetDateTime.of(2015, 2, 16, 13, 21, 0, 0, ZoneOffset.of("+05:00"));
        final OffsetDateTimePojo pojo = new OffsetDateTimePojo(dateTime);

        final String expected = "{\"defaultFormatted\":\"2015-02-16T13:21:00+05:00\"," +
                "\"millisFormatted\":1424074860000," +
                "\"offsetDateTime\":\"+0500 16-02-2015--00:21:13\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final OffsetDateTimePojo result = bindingJsonb.fromJson(expected, OffsetDateTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.offsetDateTime);
    }

    @Test
    public void testMarshalOffsetTime() {
        final Jsonb jsonb = getJsonbWithMillisIgnored();
        final OffsetTime dateTime = OffsetTime.of(13, 21, 15, 0, ZoneOffset.of("+05:00"));
        final OffsetTimePojo pojo = new OffsetTimePojo(dateTime);

        final String expected = "{\"defaultFormatted\":\"13:21:15+05:00\",\"offsetTime\":\"13:21:15+0500\"}";
        assertEquals(expected, jsonb.toJson(pojo));

        final OffsetTimePojo result = jsonb.fromJson(expected, OffsetTimePojo.class);
        assertEquals(dateTime, result.defaultFormatted);
        assertEquals(dateTime, result.offsetTime);
    }

    @Test
    public void testClassLevel() throws ParseException {
        final ClassLevelDateAnnotation pojo = new ClassLevelDateAnnotation();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        pojo.date = sdf.parse("04.03.2015");

        final ZoneId zone = ZoneId.of("Asia/Almaty");
        pojo.calendar = new Calendar.Builder().setDate(2015, Calendar.APRIL, 3).setTimeOfDay(11, 11, 10).setTimeZone(TimeZone.getTimeZone(zone)).build();
        pojo.zonedDateTime = ZonedDateTime.of(2015, 4, 3, 13, 21, 0, 0, zone);
        pojo.defaultZoned = pojo.zonedDateTime;
        pojo.localDateTime  = LocalDateTime.of(2015, 4, 3, 13, 21, 0, 0);

        final String expected = "{\"date\":\"04-03-2015 00:00:00\"," +
                "\"localDateTime\":\"03-04-2015 13:21:00\"," +
                "\"calendar\":\"+06 ALMT ven. avril 03-04-2015 11:11:10\"," +
                "\"defaultZoned\":\"2015-04-03T13:21:00+06:00[Asia/Almaty]\"," +
                "\"zonedDateTime\":\"+06 ALMT ven. avril 03-04-2015 13:21:00\"}";
        assertEquals(expected, bindingJsonb.toJson(pojo));

        final ClassLevelDateAnnotation result = bindingJsonb.fromJson(expected, ClassLevelDateAnnotation.class);
        assertEquals(pojo.date, result.date);
        assertEquals(pojo.localDateTime, result.localDateTime);
        assertEquals(pojo.calendar.getTime(), result.calendar.getTime());
        assertEquals(pojo.zonedDateTime, result.zonedDateTime);
    }

    @Test
    public void testGlobalConfigDateFormat() {
        final JsonbConfig config = new JsonbConfig();
        config.withDateFormat("X z E MMMM dd-MM-yyyy HH:mm:ss", Locale.FRENCH);

        final Jsonb jsonb = JsonbBuilder.create(config);

        final ZonedDateTime dateTime = ZonedDateTime.of(2015, 4, 3, 13, 21, 0, 0, ZoneId.of("Asia/Almaty"));
        final String expected = "{\"value\":\"+06 ALMT ven. avril 03-04-2015 13:21:00\"}";
        assertEquals(expected, jsonb.toJson(new ScalarValueWrapper<>(dateTime)));

        final ScalarValueWrapper<ZonedDateTime> result = jsonb.fromJson(expected, new TestTypeToken<ScalarValueWrapper<ZonedDateTime>>(){}.getType());
        assertEquals(dateTime, result.getValue());
    }

    @Test
    public void testDateFrenchLocale() {
        String format = "E DD MMM yyyy HH:mm:ss z";
        Locale locale = Locale.forLanguageTag("fr-FR");
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withDateFormat(format, locale));

        final ScalarValueWrapper<Date> result = jsonb.fromJson("{ \"value\" : \"lun. 93 avr. 2017 16:51:12 CEST\" }", new TestTypeToken<ScalarValueWrapper<Date>>(){}.getType());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        final Instant instant = Instant.from(formatter.withLocale(locale).parse("lun. 93 avr. 2017 16:51:12 CEST"));
        assertEquals(instant, result.getValue().toInstant());
    }

    @Test
    public void testSimpleTimeZone() {
        String json = bindingJsonb.toJson(new ScalarValueWrapper<>(new SimpleTimeZone(0, "America/Los_Angeles")));
        assertEquals("{\"value\":\"America/Los_Angeles\"}", json);

        ScalarValueWrapper<TimeZone> result = bindingJsonb.fromJson(json, new TestTypeToken<ScalarValueWrapper<TimeZone>>() {
        }.getType());
        assertEquals("America/Los_Angeles", result.getValue().getID());
        assertEquals(LocalDateTime.now().atZone(ZoneId.of("America/Los_Angeles")).getOffset().getTotalSeconds() * 1000,
                result.getValue().getOffset(System.currentTimeMillis()));
    }

    @Test
    public void testDateInMap() {

        JsonbConfig config = new JsonbConfig()
                .withDateFormat("yyyy", Locale.ENGLISH);

        Jsonb jsonb = JsonbBuilder.create(config);
        LocalDate localDate = LocalDate.of(2017, 9, 14);

        DateInMapPojo pojo = new DateInMapPojo();
        pojo.setLocalDate(localDate);
        pojo.setDateMap(new HashMap<>());
        pojo.getDateMap().put("first", localDate);

        String json = jsonb.toJson(pojo);

        assertEquals("{\"dateMap\":{\"first\":\"2017\"},\"localDate\":\"2017\"}", json);

        config = new JsonbConfig()
                .withDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        jsonb = JsonbBuilder.create(config);
        DateInMapPojo result = jsonb.fromJson("{\"dateMap\":{\"first\":\"01.01.2017\"},\"localDate\":\"01.01.2017\"}", DateInMapPojo.class);
        assertEquals(LocalDate.of(2017,1,1), result.localDate);
        assertEquals(LocalDate.of(2017,1,1), result.dateMap.get("first"));
    }

    @Test
    public void testYearMonth() {
        YearMonthPojo yearMonthPojo = new YearMonthPojo();
        yearMonthPojo.yearMonth = YearMonth.of(2019, Month.MAY);
        yearMonthPojo.yearMonthWithFormatter = YearMonth.of(2019, Month.MAY);

        String expected = "{\"yearMonth\":\"2019-05\",\"yearMonthWithFormatter\":\"05-2019\"}";
        String serialized = bindingJsonb.toJson(yearMonthPojo);
        assertEquals(expected, serialized);

        YearMonthPojo deserialized = bindingJsonb.fromJson(expected, YearMonthPojo.class);
        assertEquals(yearMonthPojo, deserialized);
    }

    @Test
    public void testMonthDay() {
        MonthDayPojo monthDay = new MonthDayPojo();
        monthDay.monthDay = MonthDay.of(Month.MAY, 2);
        monthDay.monthDayWithFormatter = MonthDay.of(Month.MAY, 2);

        String expected = "{\"monthDay\":\"--05-02\",\"monthDayWithFormatter\":\"02-05\"}";
        String serialized = bindingJsonb.toJson(monthDay);
        assertEquals(expected, serialized);

        MonthDayPojo deserialized = bindingJsonb.fromJson(expected, MonthDayPojo.class);
        assertEquals(monthDay, deserialized);
    }

    @Test
    public void testXMLGregorianCalendar() throws DatatypeConfigurationException {
        final Calendar calendar = new Calendar.Builder()
                .setDate(2015, Calendar.APRIL, 3)
                .setTimeOfDay(11, 11, 10)
                .setTimeZone(TimeZone.getTimeZone("Europe/Prague"))
                .build();

        final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar((GregorianCalendar) calendar);

        String serialized = bindingJsonb.toJson(xmlGregorianCalendar);

        assertEquals("\"2015-04-03T11:11:10+02:00[GMT+02:00]\"", serialized);
        assertEquals(xmlGregorianCalendar, bindingJsonb.fromJson(serialized, XMLGregorianCalendar.class));
    }


    public static class DateInMapPojo {
        private LocalDate localDate;
        private Map<String, LocalDate> dateMap;

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public Map<String, LocalDate> getDateMap() {
            return dateMap;
        }

        public void setDateMap(Map<String, LocalDate> dateMap) {
            this.dateMap = dateMap;
        }
    }
}
