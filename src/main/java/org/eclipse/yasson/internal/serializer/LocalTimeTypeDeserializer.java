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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link LocalTime} type.
 */
public class LocalTimeTypeDeserializer extends AbstractDateTimeDeserializer<LocalTime> {

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public LocalTimeTypeDeserializer(Customization customization) {
        super(LocalTime.class, customization);
    }

    @Override
    protected LocalTime fromInstant(Instant instant) {
        throw new JsonbException(Messages.getMessage(MessageKeys.TIME_TO_EPOCH_MILLIS_ERROR, LocalTime.class.getSimpleName()));
    }

    @Override
    protected LocalTime parseDefault(String jsonValue, Locale locale) {
        return LocalTime.parse(jsonValue, DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale));
    }

    @Override
    protected LocalTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return LocalTime.parse(jsonValue, formatter);
    }
}
