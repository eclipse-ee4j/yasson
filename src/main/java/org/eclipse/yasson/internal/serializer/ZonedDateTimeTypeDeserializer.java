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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link ZonedDateTime} type.
 */
public class ZonedDateTimeTypeDeserializer extends AbstractDateTimeDeserializer<ZonedDateTime> {

    private static final Logger LOGGER = Logger.getLogger(ZonedDateTimeTypeDeserializer.class.getName());

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public ZonedDateTimeTypeDeserializer(Customization customization) {
        super(ZonedDateTime.class, customization);
    }

    /**
     * fromInstant is called only in case {@link javax.json.bind.annotation.JsonbDateFormat} is TIME_IN_MILLIS,
     * which doesn't make much sense for usage with ZonedDateTime.
     */
    @Override
    protected ZonedDateTime fromInstant(Instant instant) {
        LOGGER.warning(Messages.getMessage(MessageKeys.OFFSET_DATE_TIME_FROM_MILLIS, ZonedDateTime.class.getSimpleName(), UTC));
        return ZonedDateTime.ofInstant(instant, UTC);
    }

    @Override
    protected ZonedDateTime parseDefault(String jsonValue, Locale locale) {
        return ZonedDateTime.parse(jsonValue, DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale));
    }

    @Override
    protected ZonedDateTime parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        return ZonedDateTime.parse(jsonValue, getZonedFormatter(formatter));
    }
}
