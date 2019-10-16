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

package org.eclipse.yasson.internal.model.customization;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyNamingStrategy;

import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import static java.util.Comparator.comparing;

import static javax.json.bind.config.PropertyNamingStrategy.CASE_INSENSITIVE;
import static javax.json.bind.config.PropertyNamingStrategy.IDENTITY;
import static javax.json.bind.config.PropertyNamingStrategy.LOWER_CASE_WITH_DASHES;
import static javax.json.bind.config.PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES;
import static javax.json.bind.config.PropertyNamingStrategy.UPPER_CAMEL_CASE;
import static javax.json.bind.config.PropertyNamingStrategy.UPPER_CAMEL_CASE_WITH_SPACES;
import static javax.json.bind.config.PropertyOrderStrategy.ANY;
import static javax.json.bind.config.PropertyOrderStrategy.LEXICOGRAPHICAL;
import static javax.json.bind.config.PropertyOrderStrategy.REVERSE;

/**
 * Provides strategies for {@link javax.json.bind.config.PropertyNamingStrategy} and
 * {@link javax.json.bind.config.PropertyOrderStrategy}.
 */
public final class StrategiesProvider {
    private StrategiesProvider() {
    }

    /**
     * Case insensitive naming strategy.
     */
    public static final PropertyNamingStrategy CASE_INSENSITIVE_STRATEGY = Objects::requireNonNull;

    /**
     * Returns an ordering strategy which corresponds to the ordering strategy name.
     *
     * @param strategy ordering strategy name
     * @return ordering strategy
     */
    public static Consumer<List<PropertyModel>> getOrderingFunction(String strategy) {
        switch (strategy) {
        case LEXICOGRAPHICAL:
            return props -> props.sort(comparing(PropertyModel::getWriteName));
        case ANY:
            return props -> {
            };
        case REVERSE:
            return props -> props.sort(comparing(PropertyModel::getWriteName).reversed());
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
        }
    }

    /**
     * Returns a naming strategy which corresponds to the naming strategy name.
     *
     * @param strategy naming strategy name
     * @return naming strategy
     */
    public static PropertyNamingStrategy getPropertyNamingStrategy(String strategy) {
        switch (strategy) {
        case LOWER_CASE_WITH_UNDERSCORES:
            return createLowerCaseStrategyWithSeparator('_');
        case LOWER_CASE_WITH_DASHES:
            return createLowerCaseStrategyWithSeparator('-');
        case UPPER_CAMEL_CASE:
            return createUpperCamelCaseStrategy();
        case UPPER_CAMEL_CASE_WITH_SPACES:
            return createUpperCamelCaseWithSpaceStrategy();
        case IDENTITY:
            return Objects::requireNonNull;
        case CASE_INSENSITIVE:
            return CASE_INSENSITIVE_STRATEGY;
        default:
            throw new JsonbException("No property naming strategy was found for: " + strategy);
        }
    }

    private static PropertyNamingStrategy createUpperCamelCaseStrategy() {
        return propertyName -> {
            Objects.requireNonNull(propertyName);
            char first = Character.toUpperCase(propertyName.charAt(0));

            return first + propertyName.substring(1);
        };
    }

    private static PropertyNamingStrategy createUpperCamelCaseWithSpaceStrategy() {
        return propertyName -> {
            String upperCased = createUpperCamelCaseStrategy().translateName(propertyName);
            CharBuffer buffer = CharBuffer.allocate(upperCased.length() * 2);
            char last = Character.MIN_VALUE;

            for (int i = 0; i < upperCased.length(); ++i) {
                char current = upperCased.charAt(i);

                if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
                    buffer.append(' ');
                }
                last = current;
                buffer.append(current);
            }
            return new String(buffer.array(), 0, buffer.position());
        };
    }

    private static PropertyNamingStrategy createLowerCaseStrategyWithSeparator(char separator) {
        return propertyName -> {
            Objects.requireNonNull(propertyName);
            CharBuffer charBuffer = CharBuffer.allocate(propertyName.length() * 2);
            char last = Character.MIN_VALUE;

            for (int i = 0; i < propertyName.length(); ++i) {
                char current = propertyName.charAt(i);

                if (i > 0 && Character.isUpperCase(current) && isLowerCaseCharacter(last)) {
                    charBuffer.append(separator);
                }
                last = current;
                charBuffer.append(Character.toLowerCase(current));
            }
            return new String(charBuffer.array(), 0, charBuffer.position());
        };
    }

    private static boolean isLowerCaseCharacter(char character) {
        return Character.isAlphabetic(character) && Character.isLowerCase(character);
    }
}
