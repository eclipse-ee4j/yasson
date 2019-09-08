/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Gyúróczki Gergő
 ******************************************************************************/
package org.eclipse.yasson.internal.model.customization;

import static java.util.Comparator.comparing;
import static javax.json.bind.config.PropertyNamingStrategy.*;
import static javax.json.bind.config.PropertyOrderStrategy.*;

import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyNamingStrategy;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.properties.MessageKeys;

import java.nio.CharBuffer;
import java.util.function.Consumer;
import java.util.*;

public final class StrategiesProvider {
    private StrategiesProvider() {}
    
    public static final PropertyNamingStrategy CASE_INSENSITIVE_STRATEGY = Objects::requireNonNull;
    
    public static Consumer<List<PropertyModel>> getOrderingFunction(String strategy){
        switch(strategy) {
            case LEXICOGRAPHICAL:
            	return props -> props.sort(comparing(PropertyModel::getWriteName));
            case ANY:
            	return props -> {};
            case REVERSE:
            	return props -> props.sort(comparing(PropertyModel::getWriteName).reversed());
            default:
            	throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
        }
    }
    
    public static PropertyNamingStrategy getPropertyNamingStrategy(String strategy) {
        switch(strategy) {
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
            
            for(int i = 0; i < upperCased.length(); ++i) {
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
            
            for(int i = 0; i < propertyName.length(); ++i) {
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