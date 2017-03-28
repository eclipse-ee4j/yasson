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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer for {@link LocalDateTime} type.
 * 
 * @author David Kral
 */
public class LocalDateTimeTypeSerializer extends AbstractDateTimeSerializer<LocalDateTime> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public LocalDateTimeTypeSerializer(JsonBindingModel model) {
        super(model);
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