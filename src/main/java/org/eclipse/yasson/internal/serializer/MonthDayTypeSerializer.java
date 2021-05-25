/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.time.Instant;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link MonthDay} type.
 */
public class MonthDayTypeSerializer extends AbstractDateTimeSerializer<MonthDay> {

    private static final int YEAR_NUMBER = Year.now().getValue();

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("MM-dd").withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public MonthDayTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(MonthDay value) {
        return value.atYear(YEAR_NUMBER).atStartOfDay(UTC).toInstant();
    }

    @Override
    protected String formatDefault(MonthDay value, Locale locale) {
        return DEFAULT_FORMAT.withLocale(locale).format(value);
    }

}
