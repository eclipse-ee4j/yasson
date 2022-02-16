/*
 * Copyright (c) 2018, 2022 Oracle and/or its affiliates. All rights reserved.
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Common serializer for {@link Date} and {@link java.sql.Date} types.
 */
class SqlDateSerializer extends DateSerializer<Date> {

    SqlDateSerializer(TypeSerializerBuilder serializerBuilder) {
        super(serializerBuilder);
    }

    @Override
    protected Instant toInstant(Date value) {
        if (value instanceof java.sql.Date) {
            // java.sql.Date doesn't have a time component, so do our best if TIME_IN_MILLIS is requested
            // In the future (at a breaking change boundary) we should probably reject this code path
            return Instant.ofEpochMilli(value.getTime());
        } else {
            return super.toInstant(value);
        }
    }

    @Override
    protected String formatDefault(Date value, Locale locale) {
        if (value instanceof java.sql.Date) {
            return value.toString() + 'Z'; // Z is the UTC timezone indicator
        } else {
            return super.formatDefault(value, locale);
        }
    }

    @Override
    protected String formatWithFormatter(Date value, DateTimeFormatter formatter) {
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().format(formatter);
        } else {
            return super.formatWithFormatter(value, formatter);
        }
    }
}
