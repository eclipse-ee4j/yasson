/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation. All rights reserved.
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

import static java.time.ZoneId.systemDefault;

/**
 * Deserializer of the {@link Date} type.
 */
class DateDeserializer extends AbstractDateDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    DateDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    Date fromInstant(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    @Override
    Date parseDefault(String jsonValue, Locale locale) {
        try {
            return parseWithOrWithoutZone(jsonValue, DEFAULT_DATE_TIME_FORMATTER.withLocale(locale));
        } catch (DateTimeParseException e3) {
            LocalDate localDate = LocalDate.parse(jsonValue, DateTimeFormatter.ISO_DATE);
            return Date.from(localDate.atStartOfDay(systemDefault()).toInstant());
        }
    }

    @Override
    Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return parseWithOrWithoutZone(jsonValue, formatter);
    }

    private static Date parseWithOrWithoutZone(String jsonValue, DateTimeFormatter formatter) {
        ZonedDateTime parsed;
        if (formatter.getZone() == null) {
            parsed = ZonedDateTime.parse(jsonValue, formatter.withZone(UTC));
        } else {
            parsed = ZonedDateTime.parse(jsonValue, formatter);
        }
        return Date.from(parsed.toInstant());
    }

}
