/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.JsonbDateFormatter;

/**
 * Serializer of the {@link LocalDate} type.
 */
class LocalDateSerializer extends AbstractDateSerializer<LocalDate> {

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC);

    LocalDateSerializer(TypeSerializerBuilder serializerBuilder) {
        super(serializerBuilder);
    }

    @Override
    protected Instant toInstant(LocalDate value) {
        return Instant.from(value.atStartOfDay(UTC));
    }

    @Override
    protected String formatDefault(LocalDate value, Locale locale) {
        return DEFAULT_FORMAT.withLocale(locale).format(value);
    }

    @Override
    protected String formatStrictIJson(LocalDate value) {
        final ZonedDateTime zonedDateTime = value.atTime(0, 0, 0).atZone(UTC);
        return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(zonedDateTime);
    }

}
