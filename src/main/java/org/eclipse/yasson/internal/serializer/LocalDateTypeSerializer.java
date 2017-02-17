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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer for {@link LocalDate} type.
 *
 * @author David Kral
 */
public class LocalDateTypeSerializer extends AbstractDateTimeSerializer<LocalDate> {

    /**
     * Creates a serializer.
     *
     * @param model Binding model.
     */
    public LocalDateTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected Instant toInstant(LocalDate value) {
        return Instant.from(value.atStartOfDay(ZoneId.systemDefault()));
    }

    @Override
    protected String formatDefault(LocalDate value, Locale locale) {
        return DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale).format(value);
    }
}
