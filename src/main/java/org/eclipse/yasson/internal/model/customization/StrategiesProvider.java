package org.eclipse.yasson.internal.model.customization;

import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;
import static javax.json.bind.config.PropertyNamingStrategy.*;
import static javax.json.bind.config.PropertyOrderStrategy.*;

import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyNamingStrategy;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.properties.MessageKeys;

import java.nio.CharBuffer;
import java.util.function.Function;
import java.util.*;

public final class StrategiesProvider {
    private StrategiesProvider() {}
    
    public static final PropertyNamingStrategy CASE_INSENSITIVE_STRATEGY = Objects::requireNonNull;
    
    public static Function<Collection<PropertyModel>, List<PropertyModel>> getOrderingFunction(String strategy){
        switch(strategy) {
            case LEXICOGRAPHICAL: return createSortingOrdererFunction(comparing(PropertyModel::getWriteName));
            case ANY:               return ArrayList::new;
            case REVERSE:           return createSortingOrdererFunction(comparing(PropertyModel::getWriteName).reversed());
            default:               throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
        }
    }
    
    public static PropertyNamingStrategy getPropertyNamingStrategy(String strategy) {
        switch(strategy) {
            case LOWER_CASE_WITH_UNDERSCORES:  return createLowerCaseStrategyWithSeparator('_');
            case LOWER_CASE_WITH_DASHES:        return createLowerCaseStrategyWithSeparator('-');
            case UPPER_CAMEL_CASE:                return createUpperCamelCaseStrategy();
            case UPPER_CAMEL_CASE_WITH_SPACES: return createUpperCamelCaseWithSpaceStrategy();
            case IDENTITY:                        return Objects::requireNonNull;
            case CASE_INSENSITIVE:                return CASE_INSENSITIVE_STRATEGY;
            default:                            throw new JsonbException("No property naming strategy was found for: " + strategy);
        }
    }
    
    
    private static Function<Collection<PropertyModel>, List<PropertyModel>> createSortingOrdererFunction(Comparator<PropertyModel> comparator){
        return props -> props.stream().sorted(comparator).collect(toList());
    }
    
    private static PropertyNamingStrategy createUpperCamelCaseStrategy() {
        return propertyName -> {
            Objects.requireNonNull(propertyName);
            char first = Character.toUpperCase(propertyName.charAt(0));
            
            return propertyName.length() == 1 ? String.valueOf(first)
                                              : first + propertyName.substring(1, propertyName.length());
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