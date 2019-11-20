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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link TimeZone} type.
 */
public class TimeZoneTypeDeserializer extends AbstractValueTypeDeserializer<TimeZone> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public TimeZoneTypeDeserializer(Customization customization) {
        super(TimeZone.class, customization);
    }

    @Override
    protected TimeZone deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        try {
            final ZoneId zoneId = ZoneId.of(jsonValue);
            final ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
            return new SimpleTimeZone(zonedDateTime.getOffset().getTotalSeconds() * 1000, zoneId.getId());
        } catch (ZoneRulesException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ZONE_PARSE_ERROR, jsonValue), e);
        }
    }

}
