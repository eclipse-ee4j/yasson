/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link Date} type.
 */
public class SqlDateTypeDeserializer extends AbstractDateTimeDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public SqlDateTypeDeserializer(Customization customization) {
        super(Date.class, customization);
    }

    /**
     * No arg constructor in order ot make usable in {@link jakarta.json.bind.annotation.JsonbTypeDeserializer}.
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
        return Date.valueOf(LocalDate.parse(jsonValue, DEFAULT_FORMATTER));
    }

    @Override
    protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return Date.valueOf(LocalDate.parse(jsonValue, formatter));
    }
}
