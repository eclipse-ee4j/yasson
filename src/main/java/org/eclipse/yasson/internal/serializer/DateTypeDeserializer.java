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
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Deserializer for {@link Date} type.
 *
 * @author David Kral
 */
public class DateTypeDeserializer extends AbstractValueTypeDeserializer<Date> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public DateTypeDeserializer(JsonBindingModel model) {
        super(Date.class, model);
    }

    private DateFormat getDateFormat(JsonbDateFormatter formatter, Locale locale) {
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(formatter.getFormat())) {
            return new SimpleDateFormat(JsonbDateFormatter.ISO_8601_DATE_TIME_FORMAT, locale);
        }
        return new SimpleDateFormat(formatter.getFormat(), locale);
    }

    @Override
    protected Date deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {

        final JsonbDateFormatter dateFormatter = unmarshaller.getJsonbContext().getComponentMatcher().getDeserializeDateFormatter(getModel());
        if(JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormatter.getFormat())) {
            return new Date(Long.parseLong(jsonValue));
        }
        final DateFormat dateFormat = getDateFormat(dateFormatter, unmarshaller.getJsonbContext().getLocale(dateFormatter.getLocale()));
        try {
            return dateFormat.parse(jsonValue);
        } catch (ParseException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATE_PARSE_ERROR, jsonValue, dateFormat));
        }
    }
}
