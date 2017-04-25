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
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.model.JsonBindingModel;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Deserializer for {@link Instant} type.
 *
 * @author David Kral
 */
public class InstantTypeDeserializer extends AbstractDateTimeDeserializer<Instant> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public InstantTypeDeserializer(JsonBindingModel model) {
        super(Instant.class, model);
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
