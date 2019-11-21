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
 * Deserializer for {@link Instant} type.
 */
public class InstantTypeDeserializer extends AbstractDateTimeDeserializer<Instant> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(UTC);

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public InstantTypeDeserializer(Customization customization) {
        super(Instant.class, customization);
    }

    @Override
    protected Instant fromInstant(Instant instant) {
        return instant;
    }

    @Override
    protected Instant parseDefault(String jsonValue, Locale locale) {
        return Instant.from(DEFAULT_FORMATTER.withLocale(locale).parse(jsonValue));
    }

    @Override
    protected Instant parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return Instant.from(getZonedFormatter(formatter).parse(jsonValue));
    }
}
