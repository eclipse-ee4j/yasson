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

import org.eclipse.yasson.internal.model.JsonBindingModel;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer for {@link Instant} type.
 *
 * @author David Kral
 */
public class InstantTypeSerializer extends AbstractDateTimeSerializer<Instant> {

    /**
     * Construct serializer with its class.
     *
     * @param model Binding model.
     */
    public InstantTypeSerializer(JsonBindingModel model) {
        super(model);
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
