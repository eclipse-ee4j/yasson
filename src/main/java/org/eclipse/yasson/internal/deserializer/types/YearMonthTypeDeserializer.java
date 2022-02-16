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

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Deserializer of the {@link YearMonth} type.
 */
class YearMonthTypeDeserializer extends AbstractDateDeserializer<YearMonth> {

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM").withZone(UTC);

    YearMonthTypeDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected YearMonth fromInstant(Instant instant) {
        return YearMonth.from(instant.atZone(UTC));
    }

    @Override
    protected YearMonth parseDefault(String jsonValue, Locale locale) {
        return YearMonth.parse(jsonValue, DEFAULT_FORMAT.withLocale(locale));
    }

    @Override
    protected YearMonth parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return YearMonth.parse(jsonValue, formatter);
    }

}
