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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer of the {@link LocalTime} type.
 */
class LocalTimeDeserializer extends AbstractDateDeserializer<LocalTime> {

    LocalTimeDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected LocalTime fromInstant(Instant instant) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, LocalTime.class.getSimpleName()));
    }

    @Override
    protected LocalTime parseDefault(String jsonValue, Locale locale) {
        return LocalTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale));
    }

    @Override
    protected LocalTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalTime.parse(jsonValue, formatter);
    }
}
