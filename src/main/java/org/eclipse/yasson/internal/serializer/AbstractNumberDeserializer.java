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
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

/**
 * Common serializer for numbers, using number format.
 *
 * @author Roman Grigoriadi
 * @param <T> Type to deserialize.
 */
public abstract class AbstractNumberDeserializer<T extends Number> extends AbstractValueTypeDeserializer<T> {

    /**
     * Creates an instance of AbstractNumberDeserializer.
     *
     * @param clazz Class to create deserializer for.
     * @param model Binding model.
     */
    public AbstractNumberDeserializer(Class<T> clazz, JsonBindingModel model) {
        super(clazz, model);
    }

    protected final Optional<Number> deserializeForamtted(String jsonValue, boolean integerOnly, JsonbContext jsonbContext) {
        final JsonbNumberFormatter numberFormat = model.getCustomization().getDeserializeNumberFormatter();
        if (numberFormat != null) {
            //TODO perf consider synchronizing on format instance or per thread cache.
            final NumberFormat format = NumberFormat.getInstance(jsonbContext.getLocale(numberFormat.getLocale()));
            ((DecimalFormat)format).applyPattern(numberFormat.getFormat());
            format.setParseIntegerOnly(integerOnly);
            try {
                return Optional.of(format.parse(jsonValue));
            } catch (ParseException e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.PARSING_NUMBER, jsonValue, numberFormat.getFormat()));
            }
        }
        return Optional.empty();
    }
}
