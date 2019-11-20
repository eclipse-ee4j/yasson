/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Locale;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link XMLGregorianCalendar} type.
 */
public class XMLGregorianCalendarTypeSerializer extends AbstractDateTimeSerializer<XMLGregorianCalendar> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public XMLGregorianCalendarTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(XMLGregorianCalendar value) {
        return Instant.ofEpochMilli(value.toGregorianCalendar().getTimeInMillis());
    }

    @Override
    protected String formatDefault(XMLGregorianCalendar value, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter
                .withLocale(locale)
                .withZone(value.toGregorianCalendar().getTimeZone().toZoneId())
                .format(toTemporalAccessor(value));
    }

    @Override
    protected TemporalAccessor toTemporalAccessor(XMLGregorianCalendar object) {
        return toZonedDateTime(object);
    }

    private ZonedDateTime toZonedDateTime(XMLGregorianCalendar object) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(object.toGregorianCalendar().getTimeInMillis()),
                                       object.toGregorianCalendar().getTimeZone().toZoneId());
    }
}
