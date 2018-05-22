/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.model.customization.Customization;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * Deserializer for {@link Date} type.
 *
 * @author David Kral
 */
public class DateTypeDeserializer extends AbstractDateTimeDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public DateTypeDeserializer(Customization customization) {
        super(Date.class, customization);
    }


    @Override
    protected Date fromInstant(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    @Override
    protected Date parseDefault(String jsonValue, Locale locale) {
        final TemporalAccessor parsed = DEFAULT_DATE_TIME_FORMATTER.withLocale(locale).parse(jsonValue);
        return new Date(Instant.from(parsed).toEpochMilli());
    }

    @Override
    protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor parsed = getZonedFormatter(formatter).parse(jsonValue);
        return new Date(Instant.from(parsed).toEpochMilli());
    }
}
