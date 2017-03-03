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

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.model.PropertyModel;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Abstract class for converting date objects from {@link java.time}.
 *
 * @author Roman Grigoriadi
 * @param <T> Type to serialize.
 */
public abstract class AbstractDateTimeSerializer<T extends TemporalAccessor> extends AbstractValueTypeSerializer<T> {

    /**
     * Construct serializer with its class.
     *
     * @param model Binding model.
     */
    public AbstractDateTimeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        final JsonbContext jsonbContext = ((Marshaller) ctx).getJsonbContext();
        final JsonbDateFormatter formatter = jsonbContext.getComponentMatcher().getSerializeDateFormatter(model);
        if (model instanceof PropertyModel) {
            generator.write(((PropertyModel)model).getPropertyName(), toJson(obj, formatter, jsonbContext));
        } else {
            generator.write(toJson(obj, formatter, jsonbContext));
        }
    }

    /**
     * Converts to JSON string.
     *
     * @param object Object to convert.
     * @param formatter Formatter to use.
     * @param jsonbContext JSON-B context.
     * @return JSON representation of given object.
     */
    public String toJson(T object, JsonbDateFormatter formatter, JsonbContext jsonbContext) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(formatter.getFormat())) {
            return String.valueOf(toInstant(object).toEpochMilli());
        } else if (formatter.getDateTimeFormatter() != null) {
            return formatter.getDateTimeFormatter().format(object);
        }
        return formatDefault(object, jsonbContext.getLocale(formatter.getLocale()));
    }

    /**
     * Convert java.time object to epoch milliseconds instant. Discards zone offset and zone id information.
     *
     * @param value date object to convert
     * @return instant
     */
    protected abstract Instant toInstant(T value);

    /**
     * Format with default formatter for a given {@link java.time} date object.
     * Different default formatter for each date object type is used.
     *
     * @param value date object
     * @param locale locale from annotation / default not null
     * @return formatted date obj as string
     */
    protected abstract String formatDefault(T value, Locale locale);

    @Override
    protected void serialize(T obj, JsonGenerator generator, String key, Marshaller marshaller) {
        throw new UnsupportedOperationException("Not supported in DateTimeSerializer");
    }

    @Override
    protected void serialize(T obj, JsonGenerator generator, Marshaller marshaller) {
        throw new UnsupportedOperationException("Not supported in DateTimeSerializer");
    }
}
