/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link java.sql.Timestamp} type.
 */
public class SqlTimestampTypeSerializer extends AbstractDateTimeSerializer<Timestamp> {

    /**
     * Default Yasson {@link java.time.format.DateTimeFormatter}.
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public SqlTimestampTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(Timestamp value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(Timestamp value, Locale locale) {
        return DEFAULT_FORMATTER.withLocale(locale).format(toInstant(value));
    }
}
