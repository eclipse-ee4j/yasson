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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Serializer for {@link ZonedDateTime} type.
 *
 * @author David Kral
 */
public class ZonedDateTimeTypeSerializer extends AbstractDateTimeSerializer<ZonedDateTime> {

    /**
     * Creates a serializer.
     *
     * @param model Meta-data model.
     */
    public ZonedDateTimeTypeSerializer(JsonBindingModel model) {
        super(model);
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
