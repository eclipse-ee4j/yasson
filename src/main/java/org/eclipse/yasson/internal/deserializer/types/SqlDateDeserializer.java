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

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Deserializer of the {@link Date} type.
 */
public class SqlDateDeserializer extends AbstractDateDeserializer<Date> implements JsonbDeserializer<Date> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

    SqlDateDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    /**
     * Create new instance.
     */
    public SqlDateDeserializer() {
        super(Date.class);
    }

    @Override
    protected Date fromInstant(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    @Override
    protected Date parseDefault(String jsonValue, Locale locale) {
        return Date.valueOf(LocalDate.parse(jsonValue, DEFAULT_FORMATTER.withLocale(locale)));
    }

    @Override
    protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return Date.valueOf(LocalDate.parse(jsonValue, formatter));
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        DeserializationContextImpl context = (DeserializationContextImpl) ctx;
        return (Date) deserialize(parser.getString(), context);
    }
}
