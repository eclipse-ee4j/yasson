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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link OffsetDateTime} type.
 */
public class OffsetDateTimeTypeDeserializer extends AbstractDateTimeDeserializer<OffsetDateTime> {

    private static final Logger LOGGER = Logger.getLogger(OffsetDateTimeTypeDeserializer.class.getName());

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public OffsetDateTimeTypeDeserializer(Customization customization) {
        super(OffsetDateTime.class, customization);
    }

    /**
     * fromInstant is called only in case {@link javax.json.bind.annotation.JsonbDateFormat} is TIME_IN_MILLIS,
     * which doesn't make much sense for usage with OffsetDateTime.
     */
    @Override
    protected OffsetDateTime fromInstant(Instant instant) {
        LOGGER.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, OffsetDateTime.class.getSimpleName(), UTC));
        return OffsetDateTime.ofInstant(instant, UTC);
    }

    @Override
    protected OffsetDateTime parseDefault(String jsonValue, Locale locale) {
        return OffsetDateTime.parse(jsonValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale));
    }

    @Override
    protected OffsetDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return OffsetDateTime.parse(jsonValue, formatter);
    }
}
