/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link LocalDateTime} type.
 */
public class LocalDateTimeTypeDeserializer extends AbstractDateTimeDeserializer<LocalDateTime> {

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public LocalDateTimeTypeDeserializer(Customization customization) {
        super(LocalDateTime.class, customization);
    }

    @Override
    protected LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, UTC);
    }

    @Override
    protected LocalDateTime parseDefault(String jsonValue, Locale locale) {
        return LocalDateTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale));
    }

    @Override
    protected LocalDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalDateTime.parse(jsonValue, formatter);
    }
}
