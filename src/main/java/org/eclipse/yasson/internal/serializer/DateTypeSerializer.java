/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.model.JsonBindingModel;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Serializer for {@link Date} type.
 *
 * @author David Kral
 */
public class DateTypeSerializer extends AbstractDateTimeSerializer<Date> {

    private DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    /**
     * Construct serializer with its class.
     *
     * @param model Binding model.
     */
    public DateTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected Instant toInstant(Date value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(Date value, Locale locale) {
        return DEFAULT_FORMATTER.withLocale(locale).format(value.toInstant());
    }

    @Override
    protected String formatWithFormatter(Date value, DateTimeFormatter formatter) {
        DateTimeFormatter dateTimeFormatter = formatter.getZone() != null ?
                formatter : formatter.withZone(UTC);
        return dateTimeFormatter.format(toTemporalAccessor(value));
    }

    @Override
    protected TemporalAccessor toTemporalAccessor(Date object) {
        return object.toInstant();
    }
}
