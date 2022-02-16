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

package org.eclipse.yasson.internal.serializer.types;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.JsonbDateFormatter;

/**
 * Serializer of the {@link LocalDateTime} type.
 */
class LocalDateTimeSerializer extends AbstractDateSerializer<LocalDateTime> {

    LocalDateTimeSerializer(TypeSerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected Instant toInstant(LocalDateTime value) {
        return value.atZone(UTC).toInstant();
    }

    @Override
    protected String formatDefault(LocalDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale).format(value);
    }

    @Override
    protected String formatWithFormatter(LocalDateTime value, DateTimeFormatter formatter) {
        return getZonedFormatter(formatter).format(value);
    }

    @Override
    protected String formatStrictIJson(LocalDateTime value) {
        final ZonedDateTime zonedDateTime = value.atZone(UTC);
        return JsonbDateFormatter.IJSON_DATE_FORMATTER.format(zonedDateTime);
    }

}
