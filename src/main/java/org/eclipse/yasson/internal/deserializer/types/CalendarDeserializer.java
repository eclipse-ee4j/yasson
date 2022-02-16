/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer.types;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Deserializer of the {@link Calendar} type.
 */
class CalendarDeserializer extends AbstractDateDeserializer<Calendar> {

    private static final LocalTime ZERO_LOCAL_TIME = LocalTime.parse("00:00:00");

    private final Calendar calendarTemplate;

    CalendarDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
        this.calendarTemplate = new GregorianCalendar();
        this.calendarTemplate.clear();
        this.calendarTemplate.setTimeZone(TimeZone.getTimeZone(UTC));
    }

    @Override
    Calendar fromInstant(Instant instant) {
        final Calendar calendar = (Calendar) calendarTemplate.clone();
        calendar.setTimeInMillis(instant.toEpochMilli());
        return calendar;
    }

    @Override
    Calendar parseDefault(String jsonValue, Locale locale) {
        DateTimeFormatter formatter = jsonValue.contains("T")
                ? DateTimeFormatter.ISO_DATE_TIME
                : DateTimeFormatter.ISO_DATE;
        return parseWithFormatter(jsonValue, formatter.withLocale(locale));
    }

    @Override
    Calendar parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor parsed = formatter.parse(jsonValue);
        LocalTime time = parsed.query(TemporalQueries.localTime());
        ZoneId zone = parsed.query(TemporalQueries.zone());
        if (zone == null) {
            zone = UTC;
        }
        if (time == null) {
            time = ZERO_LOCAL_TIME;
        }
        ZonedDateTime result = LocalDate.from(parsed).atTime(time).atZone(zone);
        return GregorianCalendar.from(result);
    }
}
