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
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Abstract class for converting date objects from java.time.
 *
 * @param <T> date type
 */
public abstract class AbstractDateTimeDeserializer<T> extends AbstractValueTypeDeserializer<T> {

    /**
     * Default zone id.
     */
    public static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Creates an instance.
     *
     * @param clazz         Class to create deserializer for.
     * @param customization Model customization.
     */
    public AbstractDateTimeDeserializer(Class<T> clazz, Customization customization) {
        super(clazz, customization);
    }

    @Override
    public T deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonbDateFormatter formatter = getJsonbDateFormatter(unmarshaller.getJsonbContext());
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return fromInstant(Instant.ofEpochMilli(Long.parseLong(jsonValue)));
        } else if (formatter.getDateTimeFormatter() != null) {
            return parseWithFormatterInternal(jsonValue, formatter.getDateTimeFormatter());
        } else {
            DateTimeFormatter configDateTimeFormatter = unmarshaller.getJsonbContext().getConfigProperties()
                    .getConfigDateFormatter().getDateTimeFormatter();
            if (configDateTimeFormatter != null) {
                return parseWithFormatterInternal(jsonValue, configDateTimeFormatter);
            }
        }
        final boolean strictIJson = unmarshaller.getJsonbContext().getConfigProperties().isStrictIJson();
        if (strictIJson) {
            return parseWithFormatterInternal(jsonValue, JsonbDateFormatter.IJSON_DATE_FORMATTER);
        }
        try {
            return parseDefault(jsonValue, unmarshaller.getJsonbContext().getConfigProperties().getLocale(formatter.getLocale()));
        } catch (DateTimeException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue, getPropertyType()), e);
        }
    }

    /**
     * Returns registered deserialization jsonb date formatter.
     *
     * @param context context
     * @return date formatter
     */
    protected JsonbDateFormatter getJsonbDateFormatter(JsonbContext context) {
        if (getCustomization() != null && getCustomization().getDeserializeDateFormatter() != null) {
            return getCustomization().getDeserializeDateFormatter();
        }
        return context.getConfigProperties().getConfigDateFormatter();
    }

    /**
     * Append UTC zone in case zone is not set on formatter.
     *
     * @param formatter formatter
     * @return zoned formatter
     */
    protected DateTimeFormatter getZonedFormatter(DateTimeFormatter formatter) {
        return formatter.getZone() != null
                ? formatter
                : formatter.withZone(UTC);
    }

    /**
     * Construct date object from an instant containing epoch millisecond.
     * If date object supports zone offset / zone id, system default is used and warning is logged.
     *
     * @param instant instant to construct from
     * @return date object
     */
    protected abstract T fromInstant(Instant instant);

    /**
     * Parse java.time date object with default formatter.
     * Different default formatter for each date object type is used.
     *
     * @param jsonValue string value to parse from
     * @param locale    annotated locale or default
     * @return parsed date object
     */
    protected abstract T parseDefault(String jsonValue, Locale locale);

    /**
     * Parse java.time date object with provided formatter.
     *
     * @param jsonValue string value to parse from
     * @param formatter a formatter to use
     * @return parsed date object
     */
    protected abstract T parseWithFormatter(String jsonValue, DateTimeFormatter formatter);

    private T parseWithFormatterInternal(String jsonValue, DateTimeFormatter formatter) {
        try {
            return parseWithFormatter(jsonValue, formatter);
        } catch (DateTimeException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue, getPropertyType()), e);
        }
    }
}
