/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.model.JsonBindingModel;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Deserializer for {@link Calendar} type.
 *
 * @author David Kral
 */
public class CalendarTypeDeserializer extends AbstractDateTimeDeserializer<Calendar> {

    private final Calendar calendarTemplate;
    private final LocalTime ZERO_LOCAL_TIME = LocalTime.parse("00:00:00");

    /**
     * Creates an instance.
     *
     * @param model Binding model.
     */
    public CalendarTypeDeserializer(JsonBindingModel model) {
        super(Calendar.class, model);
        this.calendarTemplate = new GregorianCalendar();
        this.calendarTemplate.clear();
        this.calendarTemplate.setTimeZone(TimeZone.getTimeZone(UTC));
    }

    @Override
    protected Calendar fromInstant(Instant instant) {
        final Calendar calendar = (Calendar) calendarTemplate.clone();
        calendar.setTimeInMillis(instant.toEpochMilli());
        return calendar;
    }

    @Override
    protected Calendar parseDefault(String jsonValue, Locale locale) {
        DateTimeFormatter formatter = jsonValue.contains("T") ?
                DateTimeFormatter.ISO_DATE_TIME : DateTimeFormatter.ISO_DATE;
        return parseWithFormatter(jsonValue, formatter.withLocale(locale));
    }

    @Override
    protected Calendar parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
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
