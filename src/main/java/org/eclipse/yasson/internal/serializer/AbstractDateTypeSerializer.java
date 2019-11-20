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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Common serializer for {@link Date} and {@link java.sql.Date} types.
 *
 * @param <T> date type
 */
public abstract class AbstractDateTypeSerializer<T extends Date> extends AbstractDateTimeSerializer<T> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public AbstractDateTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(Date value) {
        return Instant.ofEpochMilli(value.getTime());
    }

    @Override
    protected String formatDefault(Date value, Locale locale) {
        DateTimeFormatter formatter = getDefaultFormatter();
        //in case field is of property is java.util.Date type with java.sql.Date instance
        if (value instanceof java.sql.Date) {
            formatter = SqlDateTypeSerializer.DEFAULT_FORMATTER;
        }
        return formatter.withLocale(locale).format(toInstant(value));
    }

    @Override
    protected String formatWithFormatter(Date value, DateTimeFormatter formatter) {
        return getZonedFormatter(formatter).format(toTemporalAccessor(value));
    }

    @Override
    protected String formatStrictIJson(Date value) {
        return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(toTemporalAccessor(value));
    }

    @Override
    protected TemporalAccessor toTemporalAccessor(Date object) {
        return toInstant(object);
    }

    /**
     * Returns default {@link DateTimeFormatter}.
     *
     * @return date time formatter
     */
    protected abstract DateTimeFormatter getDefaultFormatter();
}
