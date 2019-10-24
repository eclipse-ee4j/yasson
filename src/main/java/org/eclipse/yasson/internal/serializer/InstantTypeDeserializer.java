/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
