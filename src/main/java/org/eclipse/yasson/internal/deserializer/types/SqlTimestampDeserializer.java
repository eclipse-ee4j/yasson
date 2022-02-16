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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Deserializer of the {@link Timestamp} type.
 */
class SqlTimestampDeserializer extends AbstractDateDeserializer<Timestamp> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    SqlTimestampDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected Timestamp fromInstant(Instant instant) {
        return Timestamp.from(instant);
    }

    @Override
    protected Timestamp parseDefault(String jsonValue, Locale locale) {
        final TemporalAccessor parsed = DEFAULT_FORMATTER.withLocale(locale).parse(jsonValue);
        return Timestamp.from(getInstant(parsed));
    }

    @Override
    protected Timestamp parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor parsed = getZonedFormatter(formatter).parse(jsonValue);
        return Timestamp.from(getInstant(parsed));
    }

    private Instant getInstant(TemporalAccessor parsed) {
        LocalDateTime local = LocalDateTime.from(parsed);
        return local.atZone(ZoneId.of("UTC")).toInstant();
    }

}
