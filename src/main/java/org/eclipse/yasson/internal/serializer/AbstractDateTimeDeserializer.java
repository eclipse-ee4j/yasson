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

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Abstract class for converting date objects from {@link java.time}.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractDateTimeDeserializer<T> extends AbstractValueTypeDeserializer<T> {

    public static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Creates an instance.
     *
     * @param clazz Class to create deserializer for.
     * @param model Binding model.
     */
    public AbstractDateTimeDeserializer(Class<T> clazz, JsonBindingModel model) {
        super(clazz, model);
    }

    @Override
    public T deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        final JsonbDateFormatter formatter = getJsonbDateFormatter();
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return fromInstant(Instant.ofEpochMilli(Long.parseLong(jsonValue)));
        } else if (formatter.getDateTimeFormatter() != null) {
            return parseWithFormatterInternal(jsonValue, formatter.getDateTimeFormatter());
        }
        final boolean strictIJson = unmarshaller.getJsonbContext().getConfigProperties().isStrictIJson();
        if (strictIJson) {
            return parseWithFormatterInternal(jsonValue, JsonbDateFormatter.IJSON_DATE_FORMATTER);
        }
        try {
            return parseDefault(jsonValue, unmarshaller.getJsonbContext().getConfigProperties().getLocale(formatter.getLocale()));
        } catch (DateTimeParseException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue), e);
        }
    }

    protected JsonbDateFormatter getJsonbDateFormatter() {
        if (getModel() != null && getModel().getCustomization() != null && getModel().getCustomization().getDeserializeDateFormatter() != null) {
            return getModel().getCustomization().getDeserializeDateFormatter();
        }
        return JsonbDateFormatter.getDefault();
    }

    /**
     * Append UTC zone in case zone is not set on formatter.
     *
     * @param formatter formatter
     * @return zoned formatter
     */
    protected DateTimeFormatter getZonedFormatter(DateTimeFormatter formatter) {
        return formatter.getZone() != null ?
                formatter : formatter.withZone(UTC);
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
     * Parse {@link java.time} date object with default formatter.
     * Different default formatter for each date object type is used.
     *
     * @param jsonValue string value to parse from
     * @param locale annotated locale or default
     * @return parsed date object
     */
    protected abstract T parseDefault(String jsonValue, Locale locale);

    /**
     * Parse {@link java.time} date object with provided formatter.
     *
     * @param jsonValue string value to parse from
     * @param formatter a formatter to use
     * @return parsed date object
     */
    protected abstract T parseWithFormatter(String jsonValue, DateTimeFormatter formatter);

    private T parseWithFormatterInternal(String jsonValue, DateTimeFormatter formatter) {
        try {
            return parseWithFormatter(jsonValue, formatter);
        } catch (DateTimeParseException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue), e);
        }
    }
}
