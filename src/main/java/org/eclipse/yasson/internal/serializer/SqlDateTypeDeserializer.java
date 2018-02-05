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

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Deserializer for {@link Date} type.
 *
 */
public class SqlDateTypeDeserializer extends AbstractDateTimeDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public SqlDateTypeDeserializer(JsonBindingModel model) {
        super(Date.class, model);
    }

    /**
     * No arg constructor in order ot make usable in {@link javax.json.bind.annotation.JsonbTypeDeserializer}.
     */
    public SqlDateTypeDeserializer() {
        super(Date.class, null);
    }

    @Override
    protected Date fromInstant(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    @Override
    protected Date parseDefault(String jsonValue, Locale locale) {
        final TemporalAccessor parsed = DEFAULT_FORMATTER.withLocale(locale).parse(jsonValue);
        return new Date(getInstant(parsed).toEpochMilli());
    }

    @Override
    protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor parsed = getZonedFormatter(formatter).parse(jsonValue);
        return new Date(getInstant(parsed).toEpochMilli());
    }

    private Instant getInstant(TemporalAccessor parsed) {
        LocalDate local = LocalDate.from(parsed);
        return local.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant();
    }
}
