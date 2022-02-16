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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Deserializer of the {@link LocalDate} type.
 */
class LocalDateDeserializer extends AbstractDateDeserializer<LocalDate> {

    LocalDateDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected LocalDate fromInstant(Instant instant) {
        return instant.atZone(UTC).toLocalDate();
    }

    @Override
    protected LocalDate parseDefault(String jsonValue, Locale locale) {
        return LocalDate.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
    }

    @Override
    protected LocalDate parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalDate.parse(jsonValue, formatter);
    }
}
