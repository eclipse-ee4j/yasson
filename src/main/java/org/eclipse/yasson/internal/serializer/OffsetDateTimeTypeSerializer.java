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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link OffsetDateTime} type.
 */
public class OffsetDateTimeTypeSerializer extends AbstractDateTimeSerializer<OffsetDateTime> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public OffsetDateTimeTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(OffsetDateTime value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(OffsetDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format(value);
    }
}
