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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Deserializer of the {@link Date} type.
 *
 * <p>
 * For date-only patterns (e.g., "yyyy-MM-dd"), this deserializer uses {@link DateTimeFormatter#parseBest} to detect the
 * appropriate temporal type [ZonedDateTime, LocalDateTime, LocalDate, or YearMonth]
 * and creates the Date object at midnight in the specified
 * timezone. When no timezone is specified in the pattern, UTC is used as required by Jakarta JSON Binding specification
 * section 3.5.
 * </p>
 * <p>
 * critical, use {@link java.time.LocalDate} (recommended) or {@link java.sql.Date}.
 * </p>
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
        return parseWithOrWithoutZone(jsonValue, DEFAULT_DATE_TIME_FORMATTER.withLocale(locale));
    }

    @Override
    Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return parseWithOrWithoutZone(jsonValue, formatter);
    }

    private static Date parseWithOrWithoutZone(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor best = formatter.parseBest(jsonValue,
                ZonedDateTime::from,
                LocalDateTime::from,
                LocalDate::from,
                YearMonth::from);

        // If no zone provided in string, use the formatter's zone or UTC per the Jakarta JSON Binding specification
        // section 3.5
        final ZoneId zone = formatter.getZone() != null ? formatter.getZone() : ZoneOffset.UTC;

        // Determine the type of the best option
        final Instant instant;
        if (best instanceof ZonedDateTime) {
            instant = ((ZonedDateTime) best).toInstant();
        } else if (best instanceof LocalDateTime) {
            instant = ((LocalDateTime) best).atZone(zone).toInstant();
        } else if (best instanceof LocalDate) {
            instant = LocalDate.from(best).atStartOfDay(zone).toInstant();
        } else if (best instanceof YearMonth) {
            instant = ((YearMonth) best).atDay(1).atStartOfDay(zone).toInstant();
        } else {
            // Fallback
            instant = Instant.from(best);
        }
        return Date.from(instant);
    }

}
