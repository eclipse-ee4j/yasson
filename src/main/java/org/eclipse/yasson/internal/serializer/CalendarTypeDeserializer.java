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

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
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
        final TemporalAccessor parsed = formatter.withLocale(locale).parse(jsonValue);
        return GregorianCalendar.from(ZonedDateTime.from(parsed));
    }

    @Override
    protected Calendar parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return GregorianCalendar.from(ZonedDateTime.from(formatter.parse(jsonValue)));
    }
}
