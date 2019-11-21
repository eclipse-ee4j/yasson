/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link Calendar} type.
 */
public class CalendarTypeSerializer extends AbstractDateTimeSerializer<Calendar> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public CalendarTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(Calendar value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(Calendar value, Locale locale) {
        DateTimeFormatter formatter = value.isSet(Calendar.HOUR) || value.isSet(Calendar.HOUR_OF_DAY)
                ? DateTimeFormatter.ISO_DATE_TIME
                : DateTimeFormatter.ISO_DATE;
        return formatter.withZone(value.getTimeZone().toZoneId())
                .withLocale(locale).format(toTemporalAccessor(value));
    }

    @Override
    protected TemporalAccessor toTemporalAccessor(Calendar object) {
        return toZonedDateTime(object);
    }

    private ZonedDateTime toZonedDateTime(Calendar object) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(object.getTimeInMillis()),
                                       object.getTimeZone().toZoneId());
    }
}
