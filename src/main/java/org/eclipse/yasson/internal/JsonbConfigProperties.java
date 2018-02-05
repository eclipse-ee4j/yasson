/*******************************************************************************
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.model.customization.naming.DefaultNamingStrategies;
import org.eclipse.yasson.internal.model.customization.naming.IdentityStrategy;
import org.eclipse.yasson.internal.model.customization.ordering.AnyOrderStrategy;
import org.eclipse.yasson.internal.model.customization.ordering.LexicographicalOrderStrategy;
import org.eclipse.yasson.internal.model.customization.ordering.PropOrderStrategy;
import org.eclipse.yasson.internal.model.customization.ordering.PropertyOrdering;
import org.eclipse.yasson.internal.model.customization.ordering.ReverseOrderStrategy;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Resolved properties from JSONB config.
 *
 * @author Roman Grigoriadi
 */
public class JsonbConfigProperties {

    /**
     * Property used to specify behaviour on deserialization when JSON document contains properties
     * which doesn't exist in the target class. Default value is 'true'.
     */
    public static final String FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";

    public static final String USER_TYPE_MAPPING = "jsonb.user-type-mapping";

    /**
     * <p>Makes parsing dates defaulting to zero hour, minute and second.
     * This will made available to parse patterns like yyyy.MM.dd to
     * {@link java.util.Date}, {@link java.util.Calendar}, {@link java.time.Instant} {@link java.time.LocalDate}
     * or even {@link java.time.ZonedDateTime}.
     * <p>If time zone is not set in the pattern than UTC time zone is used.
     * So for example json value 2018.01.01 becomes 2018.01.01 00:00:00 UTC when parsed
     * to instant {@link java.time.Instant} or {@link java.time.ZonedDateTime}.
     */
    public static final String ZERO_TIME_DEFAULTING = "jsonb.zero-time-defaulting";

    private final JsonbConfig jsonbConfig;

    private final PropertyVisibilityStrategy propertyVisibilityStrategy;

    private final PropertyNamingStrategy propertyNamingStrategy;

    private final PropertyOrdering propertyOrdering;

    private final JsonbDateFormatter dateFormatter;

    private final Locale locale;

    private final String binaryDataStrategy;

    private final boolean nullable;

    private final boolean failOnUnknownProperties;

    private final boolean strictIJson;

    private final boolean zeroTimeDefaulting;

    private final Map<Class<?>, Class<?>> userTypeMapping;

    public JsonbConfigProperties(JsonbConfig jsonbConfig) {
        this.jsonbConfig = jsonbConfig;
        this.binaryDataStrategy = initBinaryDataStrategy();
        this.propertyNamingStrategy = initPropertyNamingStrategy();
        this.propertyVisibilityStrategy = initPropertyVisibilityStrategy();
        this.propertyOrdering = new PropertyOrdering(initOrderStrategy());
        this.locale = initConfigLocale();
        this.dateFormatter = initDateFormatter(this.locale);
        this.nullable = initConfigNullable();
        this.failOnUnknownProperties = initConfigFailOnUnknownProperties();
        this.strictIJson = initStrictJson();
        this.userTypeMapping = initUserTypeMapping();
        this.zeroTimeDefaulting = initZeroTimeDefaultingForJavaTime();
    }

    private boolean initZeroTimeDefaultingForJavaTime() {
        return getBooleanConfigProperty(ZERO_TIME_DEFAULTING, false);
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>,Class<?>> initUserTypeMapping() {
        Optional<Object> property = jsonbConfig.getProperty(USER_TYPE_MAPPING);
        if (!property.isPresent()) {
            return Collections.emptyMap();
        }
        Object result = property.get();
        if (!(result instanceof Map)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, USER_TYPE_MAPPING, Map.class.getSimpleName()));
        }
        return (Map<Class<?>, Class<?>>) result;
    }

    private JsonbDateFormatter initDateFormatter(Locale locale) {
        final String dateFormat = getGlobalConfigJsonbDateFormat();
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat) || JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat)) {
            return new JsonbDateFormatter(dateFormat, locale.toLanguageTag());
        }
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendPattern(dateFormat);
        if (isZeroTimeDefaulting()) {
            builder.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0);
            builder.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0);
            builder.parseDefaulting(ChronoField.HOUR_OF_DAY, 0);
        }
        DateTimeFormatter dateTimeFormatter = builder.toFormatter(locale);
        return new JsonbDateFormatter(dateTimeFormatter, dateFormat, locale.toLanguageTag());
    }

    private String getGlobalConfigJsonbDateFormat() {
        final Optional<Object> formatProperty = jsonbConfig.getProperty(JsonbConfig.DATE_FORMAT);
        return formatProperty.map(f -> {
            if (!(f instanceof String)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.DATE_FORMAT, String.class.getSimpleName()));
            }
            return (String) f;
        }).orElse(JsonbDateFormat.DEFAULT_FORMAT);
    }

    private PropOrderStrategy initOrderStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY);
        if (property.isPresent()) {
            final Object strategy = property.get();
            if (!(strategy instanceof String)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
            }
            switch ((String) strategy) {
                case PropertyOrderStrategy.LEXICOGRAPHICAL:
                    return new LexicographicalOrderStrategy();
                case PropertyOrderStrategy.REVERSE:
                    return new ReverseOrderStrategy();
                case PropertyOrderStrategy.ANY:
                    return new AnyOrderStrategy();
                default:
                    throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_ORDER, strategy));
            }
        }
        //default by spec
        return new LexicographicalOrderStrategy();
    }

    private PropertyNamingStrategy initPropertyNamingStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY);
        if (!property.isPresent()) {
            return new IdentityStrategy();
        }
        Object propertyNamingStrategy = property.get();
        if (propertyNamingStrategy instanceof String) {
            String namingStrategyName = (String) propertyNamingStrategy;
            final PropertyNamingStrategy foundNamingStrategy = DefaultNamingStrategies.getStrategy(namingStrategyName);
            if (foundNamingStrategy == null) {
                throw new JsonbException("No property naming strategy was found for: " + namingStrategyName);
            }
            return foundNamingStrategy;
        }
        if (!(propertyNamingStrategy instanceof PropertyNamingStrategy)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_NAMING_STRATEGY_INVALID));
        }
        return (PropertyNamingStrategy) property.get();
    }

    private PropertyVisibilityStrategy initPropertyVisibilityStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY);
        if (!property.isPresent()) {
            return null;
        }
        final Object propertyVisibilityStrategy = property.get();
        if (!(propertyVisibilityStrategy instanceof PropertyVisibilityStrategy)) {
            throw new JsonbException("JsonbConfig.PROPERTY_VISIBILITY_STRATEGY must be instance of " + PropertyVisibilityStrategy.class);
        }
        return (PropertyVisibilityStrategy) propertyVisibilityStrategy;
    }

    private String initBinaryDataStrategy() {
        final Optional<Boolean> iJson = jsonbConfig.getProperty(JsonbConfig.STRICT_IJSON).map((obj->(Boolean)obj));
        if (iJson.isPresent() && iJson.get()) {
            return BinaryDataStrategy.BASE_64_URL;
        }
        final Optional<String> strategy = jsonbConfig.getProperty(JsonbConfig.BINARY_DATA_STRATEGY).map((obj) -> (String) obj);
        return strategy.orElse(BinaryDataStrategy.BYTE);
    }

    private boolean initConfigNullable() {
        return getBooleanConfigProperty(JsonbConfig.NULL_VALUES, false);
    }

    private boolean initConfigFailOnUnknownProperties() {
        return getBooleanConfigProperty(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Gets nullable from {@link JsonbConfig}.
     * If true null values are serialized to json.
     *
     * @return Configured nullable
     */
    public boolean getConfigNullable() {
        return nullable;
    }

    /**
     * Gets unknown properties flag from {@link JsonbConfig}.
     * If false, {@link JsonbException} is not thrown for deserialization, when json key
     * cannot be mapped to class property.
     *
     * @return
     *      {@link JsonbException} is risen on unknown property. Default is true even if
     *      not set in json config.
     */
    public boolean getConfigFailOnUnknownProperties() {
        return failOnUnknownProperties;
    }

    private boolean getBooleanConfigProperty(String propertyName, boolean defaultValue) {
        final Optional<Object> property = jsonbConfig.getProperty(propertyName);
        if (property.isPresent()) {
            final Object result = property.get();
            if (!(result instanceof Boolean)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, propertyName, Boolean.class.getSimpleName()));
            }
            return (boolean) result;
        }
        return defaultValue;
    }

    /**
     * Checks for binary data strategy to use.
     *
     * @return Binary data strategy.
     */
    public  String getBinaryDataStrategy() {
        return binaryDataStrategy;
    }

    /**
     * Converts string locale to {@link Locale}.
     *
     * @param locale Locale to convert.
     * @return {@link Locale} instance.
     */
    public Locale getLocale(String locale) {
        if (locale.equals(JsonbDateFormat.DEFAULT_LOCALE)) {
            return this.locale;
        }
        return Locale.forLanguageTag(locale);
    }

    /**
     * Gets locale from {@link JsonbConfig}.
     *
     * @return Configured locale.
     */
    private Locale initConfigLocale() {
        final Optional<Object> localeProperty = jsonbConfig.getProperty(JsonbConfig.LOCALE);
        return  localeProperty.map(loc -> {
            if (!(loc instanceof Locale)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.LOCALE, Locale.class.getSimpleName()));
            }
            return (Locale) loc;
        }).orElseGet(Locale::getDefault);
    }

    private boolean initStrictJson() {
        return getBooleanConfigProperty(JsonbConfig.STRICT_IJSON, false);
    }

    /**
     * Gets property visibility strategy.
     *
     * @return Property visibility strategy.
     */
    public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
        return propertyVisibilityStrategy;
    }

    /**
     * Gets property naming strategy.
     *
     * @return Property naming strategy.
     */
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return propertyNamingStrategy;
    }

    /**
     * Gets instantiated shared config date formatter.
     *
     * @return Date formatter.
     */
    public JsonbDateFormatter getConfigDateFormatter() {
        return dateFormatter;
    }

    /**
     * Gets property ordering component.
     *
     * @return Component for ordering properties.
     */
    public PropertyOrdering getPropertyOrdering() {
        return propertyOrdering;
    }

    /**
     * If strict IJSON patterns should be used.
     *
     * @return if IJSON is enabled
     */
    public boolean isStrictIJson() {
        return strictIJson;
    }

    /**
     * User type mapping for map interface to implementation classes.
     *
     * @return User type mapping.
     */
    public Map<Class<?>, Class<?>> getUserTypeMapping() {
        return userTypeMapping;
    }

    /**
     * <p>Makes parsing dates defaulting to zero hour, minute and second.
     * This will made available to parse patterns like yyyy.MM.dd to
     * {@link java.util.Date}, {@link java.util.Calendar}, {@link java.time.Instant} {@link java.time.LocalDate}
     * or even {@link java.time.ZonedDateTime}.
     * <p>If time zone is not set in the pattern than UTC time zone is used.
     * So for example json value 2018.01.01 becomes 2018.01.01 00:00:00 UTC when parsed
     * to instant {@link java.time.Instant}.
     *
     * @return true if time should be defaulted to zero.
     */
    public boolean isZeroTimeDefaulting() {
        return zeroTimeDefaulting;
    }
}
