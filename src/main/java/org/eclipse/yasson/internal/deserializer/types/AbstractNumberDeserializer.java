/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Function;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Base deserializer for all the number types.
 */
abstract class AbstractNumberDeserializer<T extends Number> extends TypeDeserializer {

    private final ModelDeserializer<String> actualDeserializer;
    private final boolean integerOnly;

    AbstractNumberDeserializer(TypeDeserializerBuilder builder, boolean integerOnly) {
        super(builder);
        this.actualDeserializer = actualDeserializer(builder);
        this.integerOnly = integerOnly;
    }

    private ModelDeserializer<String> actualDeserializer(TypeDeserializerBuilder builder) {
        Customization customization = builder.getCustomization();
        if (customization.getDeserializeNumberFormatter() == null) {
            return (value, context) -> {
                try {
                    return parseNumberValue(value);
                } catch (NumberFormatException e) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, getType()), e);
                }
            };
        }

        final JsonbNumberFormatter numberFormat = customization.getDeserializeNumberFormatter();
        //consider synchronizing on format instance or per thread cache.
        Locale locale = builder.getConfigProperties().getLocale(numberFormat.getLocale());
        final NumberFormat format = NumberFormat.getInstance(locale);
        ((DecimalFormat) format).applyPattern(numberFormat.getFormat());
        format.setParseIntegerOnly(integerOnly);
        Function<String, String> valueChanger = createCompatibilityValueChanger(locale);
        return (value, context) -> {
            try {
                String updated = valueChanger.apply(value);
                return parseNumberValue(String.valueOf(format.parse(updated)));
            } catch (ParseException e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.PARSING_NUMBER, value, numberFormat.getFormat()), e);
            }
        };
    }

    private Function<String, String> createCompatibilityValueChanger(Locale locale) {
        char beforeJdk13GroupSeparator = '\u00A0';
        char frenchGroupingSeparator = DecimalFormatSymbols.getInstance(Locale.FRENCH).getGroupingSeparator();
        if (locale.getLanguage().equals(Locale.FRENCH.getLanguage()) && beforeJdk13GroupSeparator != frenchGroupingSeparator) {
            //JDK-8225245
            return value -> value.replace(beforeJdk13GroupSeparator, frenchGroupingSeparator);
        }
        return value -> value;
    }

    abstract T parseNumberValue(String value);

    @Override
    Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
        return actualDeserializer.deserialize(value, context);
    }

}
