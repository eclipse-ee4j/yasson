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

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer for {@link OffsetTime} type.
 *
 * @author David Kral
 * @author Roman Grigoriadi
 */
public class OffsetTimeTypeSerializer extends AbstractDateTimeSerializer<OffsetTime> {

    /**
     * Creates a serializer.
     *
     * @param model Binding model.
     */
    public OffsetTimeTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected Instant toInstant(OffsetTime value) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, OffsetTime.class.getSimpleName()));
    }

    @Override
    protected String formatDefault(OffsetTime value, Locale locale) {
        return DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale).format(value);
    }
}
