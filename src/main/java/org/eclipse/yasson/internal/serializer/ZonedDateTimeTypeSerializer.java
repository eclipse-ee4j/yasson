/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link ZonedDateTime} type.
 */
public class ZonedDateTimeTypeSerializer extends AbstractDateTimeSerializer<ZonedDateTime> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public ZonedDateTimeTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(ZonedDateTime value) {
        return value.toInstant();
    }

    @Override
    protected String formatDefault(ZonedDateTime value, Locale locale) {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale).format(value);
    }
}
