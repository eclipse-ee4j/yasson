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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link Date} type.
 */
public class DateTypeDeserializer extends AbstractDateTimeDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public DateTypeDeserializer(Customization customization) {
        super(Date.class, customization);
    }

    @Override
    protected Date fromInstant(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    @Override
    protected Date parseDefault(String jsonValue, Locale locale) {
        TemporalAccessor parsed = parseWithOrWithoutZone(jsonValue, DEFAULT_DATE_TIME_FORMATTER.withLocale(locale), UTC);

        return new Date(Instant.from(parsed).toEpochMilli());
    }

    @Override
    protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        TemporalAccessor parsed = parseWithOrWithoutZone(jsonValue, formatter, UTC);

        return new Date(Instant.from(parsed).toEpochMilli());
    }

    /**
     * Parses the jsonValue as a java.time.ZonedDateTime that can later be use to be converted into a java.util.Date.<br>
     * At first the Json-Date is parsed with an Offset/Zone.<br>
     * If no Offset/Zone is present and the parsing fails, it will be parsed again with the fixed Zone that was passed as
     * defaultZone.
     *
     * @param jsonValue   String value from json
     * @param formatter   DateTimeFormat options
     * @param defaultZone This Zone will be used if no other Zone was found in the jsonValue
     * @return Parsed date on base of a java.time.ZonedDateTime
     */
    private TemporalAccessor parseWithOrWithoutZone(String jsonValue, DateTimeFormatter formatter, ZoneId defaultZone) {
        try {
            // Try parsing with a Zone
            return ZonedDateTime.parse(jsonValue, formatter);
        } catch (DateTimeParseException e) {
            // Possibly exception occures because no Offset/ZoneId was found
            // Therefore parse with defaultZone again
            return ZonedDateTime.parse(jsonValue, formatter.withZone(defaultZone));
        }
    }
}
