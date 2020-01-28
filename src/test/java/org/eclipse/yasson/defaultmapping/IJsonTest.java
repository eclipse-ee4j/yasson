/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Roman Grigoriadi
 */
public class IJsonTest {

    private Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withStrictIJSON(true));

    @Test
    public void testStrictCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1970, 0, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendar));
        assertEquals("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", jsonString);

        ScalarValueWrapper<Calendar> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", new TestTypeToken<ScalarValueWrapper<Calendar>>() {}.getType());

        assertEquals(calendar.toInstant(), result.getValue().toInstant());
    }

    @Test
    public void testStrictDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.clear(Calendar.MILLISECOND);

        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendar.getTime()));
        assertTrue(jsonString.matches("\\{\"value\":\"1970-01-01T00:00:00Z\\+[0-9]{2}:[0-9]{2}\"}"));

        ScalarValueWrapper<Date> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<Date>>(){}.getType());
        assertEquals(0, result.getValue().compareTo(calendar.getTime()));

    }

    @Test
    public void testStrictInstant() {
        Instant instant = LocalDateTime.of(2017, 3, 24, 12,0,0).toInstant(ZoneOffset.MIN);
        final String json = jsonb.toJson(new ScalarValueWrapper<>(instant));
        assertEquals("{\"value\":\"2017-03-25T06:00:00Z+00:00\"}", json);
        ScalarValueWrapper<Instant> result = jsonb.fromJson("{\"value\":\"2017-03-25T06:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<Instant>>() {}.getType());
        assertEquals(instant, result.getValue());
    }

    @Test
    public void testLocalDate() {
        final LocalDate localDate = LocalDate.of(1970, 1, 1);
        final String json = jsonb.toJson(new ScalarValueWrapper<>(localDate));
        assertEquals("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", json);

        ScalarValueWrapper<LocalDate> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<LocalDate>>() {
        }.getType());


        assertEquals(localDate, result.getValue());
    }

    @Test
    public void testLocalDateTime() {
        final LocalDateTime localDateTime = LocalDateTime.of(1970, 1, 1, 1, 1, 1);
        final String json = jsonb.toJson(new ScalarValueWrapper<>(localDateTime));

        assertEquals("{\"value\":\"1970-01-01T01:01:01Z+00:00\"}", json);

        ScalarValueWrapper<LocalDateTime> result = jsonb.fromJson("{\"value\":\"1970-01-01T01:01:01Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<LocalDateTime>>() {
        }.getType());


        assertEquals(localDateTime, result.getValue());
    }

    @Test
    public void testDuration() {
        Duration duration = Duration.ofDays(1).plus(Duration.ofHours(1)).plus(Duration.ofSeconds(1));

        final String json = jsonb.toJson(new ScalarValueWrapper<>(duration));
        assertEquals("{\"value\":\"PT25H1S\"}", json);

        ScalarValueWrapper<Duration> result = jsonb.fromJson(json, new TestTypeToken<ScalarValueWrapper<Duration>>() {
        }.getType());
        assertEquals(duration, result.getValue());
    }
}
