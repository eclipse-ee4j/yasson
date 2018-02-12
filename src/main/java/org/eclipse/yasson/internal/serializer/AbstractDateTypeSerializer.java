/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.model.JsonBindingModel;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Common serializer for {@link Date} and {@link java.sql.Date} types.
 *
 */
public abstract class AbstractDateTypeSerializer<T extends Date> extends AbstractDateTimeSerializer<T> {

    /**
     * Construct serializer with its class.
     *
     * @param model Binding model.
     */
    public AbstractDateTypeSerializer(JsonBindingModel model) {
        super(model);
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

    protected abstract DateTimeFormatter getDefaultFormatter();
}
