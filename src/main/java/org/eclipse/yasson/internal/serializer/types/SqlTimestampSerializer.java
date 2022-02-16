/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer of the {@link Timestamp} type.
 */
class SqlTimestampSerializer extends AbstractDateSerializer<Timestamp> {

    /**
     * Default Yasson {@link DateTimeFormatter}.
     */
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    SqlTimestampSerializer(TypeSerializerBuilder serializerBuilder) {
        super(serializerBuilder);
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
