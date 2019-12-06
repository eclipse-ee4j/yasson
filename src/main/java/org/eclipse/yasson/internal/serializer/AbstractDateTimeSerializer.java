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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Abstract class for converting date objects.
 *
 * @param <T> Type to serialize.
 */
public abstract class AbstractDateTimeSerializer<T> extends AbstractValueTypeSerializer<T> {

    /**
     * Default zone id.
     */
    public static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public AbstractDateTimeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        final JsonbContext jsonbContext = ((Marshaller) ctx).getJsonbContext();
        final JsonbDateFormatter formatter = getJsonbDateFormatter(jsonbContext);
        generator.write(toJson(obj, formatter, jsonbContext));
    }

    /**
     * Converts to JSON string.
     *
     * @param object       Object to convert.
     * @param formatter    Formatter to use.
     * @param jsonbContext JSON-B context.
     * @return JSON representation of given object.
     */
    public String toJson(T object, JsonbDateFormatter formatter, JsonbContext jsonbContext) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(toInstant(object).toEpochMilli());
        } else if (formatter.getDateTimeFormatter() != null) {
            return formatWithFormatter(object, formatter.getDateTimeFormatter());
        } else {
            DateTimeFormatter configDateTimeFormatter = jsonbContext.getConfigProperties().getConfigDateFormatter()
                    .getDateTimeFormatter();
            if (configDateTimeFormatter != null) {
                return formatWithFormatter(object, configDateTimeFormatter);
            }
        }
        if (jsonbContext.getConfigProperties().isStrictIJson()) {
            return formatStrictIJson(object);
        }
        return formatDefault(object, jsonbContext.getConfigProperties().getLocale(formatter.getLocale()));
    }

    /**
     * Returns registered serialization jsonb date formatter.
     *
     * @param context context
     * @return jsonb formatter
     */
    protected JsonbDateFormatter getJsonbDateFormatter(JsonbContext context) {
        Customization customization = getCustomization();
        if (customization != null && customization.getSerializeDateFormatter() != null) {
            return customization.getSerializeDateFormatter();
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
     * Convert date object to {@link TemporalAccessor}
     *
     * Only for legacy dates.
     *
     * @param object date object
     * @return converted {@link TemporalAccessor}
     */
    protected TemporalAccessor toTemporalAccessor(T object) {
        return (TemporalAccessor) object;
    }

    /**
     * Convert java.time object to epoch milliseconds instant. Discards zone offset and zone id information.
     *
     * @param value date object to convert
     * @return instant
     */
    protected abstract Instant toInstant(T value);

    /**
     * Format with default formatter for a given java.time date object.
     * Different default formatter for each date object type is used.
     *
     * @param value  date object
     * @param locale locale from annotation / default not null
     * @return formatted date obj as string
     */
    protected abstract String formatDefault(T value, Locale locale);

    /**
     * Format date object with given formatter.
     *
     * @param value     date object to format
     * @param formatter formatter to format with
     * @return formatted result
     */
    protected String formatWithFormatter(T value, DateTimeFormatter formatter) {
        return formatter.format(toTemporalAccessor(value));
    }

    /**
     * Format date object as strict IJson date format.
     *
     * @param value value to format
     * @return formatted result
     */
    protected String formatStrictIJson(T value) {
        return JsonbDateFormatter.IJSON_DATE_FORMATTER.format(toTemporalAccessor(value));
    }

    @Override
    protected void serialize(T obj, JsonGenerator generator, Marshaller marshaller) {
        throw new UnsupportedOperationException("Not supported in DateTimeSerializer");
    }
}
