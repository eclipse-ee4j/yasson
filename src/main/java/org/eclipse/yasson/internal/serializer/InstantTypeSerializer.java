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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link Instant} type.
 */
public class InstantTypeSerializer extends AbstractDateTimeSerializer<Instant> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public InstantTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(Instant value) {
        return value;
    }

    @Override
    protected String formatDefault(Instant value, Locale locale) {
        return DateTimeFormatter.ISO_INSTANT.withLocale(locale).format(value);
    }

    @Override
    protected String formatWithFormatter(Instant value, DateTimeFormatter formatter) {
        return formatter.withZone(UTC).format(value);
    }

    @Override
    protected String formatStrictIJson(Instant value) {
        return JsonbDateFormatter.IJSON_DATE_FORMATTER.withZone(UTC).format(value);
    }

}
