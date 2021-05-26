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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link MonthDay} type.
 */
public class MonthDayTypeDeserializer extends AbstractDateTimeDeserializer<MonthDay> {

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("--MM-dd").withZone(UTC);

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public MonthDayTypeDeserializer(Customization customization) {
        super(MonthDay.class, customization);
    }

    @Override
    protected MonthDay fromInstant(Instant instant) {
        return MonthDay.from(instant.atZone(UTC));
    }

    @Override
    protected MonthDay parseDefault(String jsonValue, Locale locale) {
        return MonthDay.parse(jsonValue, DEFAULT_FORMAT.withLocale(locale));
    }

    @Override
    protected MonthDay parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return MonthDay.parse(jsonValue, formatter);
    }

}
