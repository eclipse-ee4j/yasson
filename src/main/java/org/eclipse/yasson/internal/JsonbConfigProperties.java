/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2020 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyOrderStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import jakarta.json.bind.serializer.JsonbSerializer;

import org.eclipse.yasson.YassonConfig;
import org.eclipse.yasson.internal.model.PropertyModel;
import org.eclipse.yasson.internal.model.ReverseTreeMap;
import org.eclipse.yasson.internal.model.customization.PropertyOrdering;
import org.eclipse.yasson.internal.model.customization.StrategiesProvider;
import org.eclipse.yasson.internal.model.customization.VisibilityStrategiesProvider;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Resolved properties from JSONB config.
 */
@SuppressWarnings("rawtypes")
public class JsonbConfigProperties {

    private static final Map<String, Class<? extends Map>> PROPERTY_ORDER_STRATEGY_MAPS =
            Map.of(PropertyOrderStrategy.LEXICOGRAPHICAL, TreeMap.class,
                   PropertyOrderStrategy.REVERSE, ReverseTreeMap.class,
                   PropertyOrderStrategy.ANY, HashMap.class);

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
    private final boolean requiredCreatorParameters;
    private final boolean dateInMillisecondsAsString;
    private final Map<Class<?>, Class<?>> userTypeMapping;
    private final Class<?> defaultMapImplType;
    private final JsonbSerializer<Object> nullSerializer;
    private final Set<Class<?>> eagerInitClasses;
    private final boolean forceMapArraySerializerForNullKeys;

    /**
     * Creates new resolved JSONB config.
     *
     * @param jsonbConfig jsonb config
     */
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
        this.defaultMapImplType = initDefaultMapImplType();
        this.nullSerializer = initNullSerializer();
        this.eagerInitClasses = initEagerInitClasses();
        this.requiredCreatorParameters = initRequiredCreatorParameters();
        this.forceMapArraySerializerForNullKeys = initForceMapArraySerializerForNullKeys();
        this.dateInMillisecondsAsString = initDateInMillisecondsAsString();
    }

    private Class<? extends Map> initDefaultMapImplType() {
        //We need to get PropertyOrderStrategy again. This time, if was not set, use ANY to get proper map implementation.
        //This is intentional!
        String propertyOrder = getConfigProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY, String.class, PropertyOrderStrategy.ANY);
        return PROPERTY_ORDER_STRATEGY_MAPS.getOrDefault(propertyOrder, HashMap.class);
    }

    private boolean initZeroTimeDefaultingForJavaTime() {
        return getConfigProperty(YassonConfig.ZERO_TIME_PARSE_DEFAULTING, Boolean.class, false);
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>, Class<?>> initUserTypeMapping() {
        return getConfigProperty(YassonConfig.USER_TYPE_MAPPING, Map.class, Collections.emptyMap());
    }

    private JsonbDateFormatter initDateFormatter(Locale locale) {
        final String dateFormat = getGlobalConfigJsonbDateFormat();
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat) || JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat)) {
            return new JsonbDateFormatter(dateFormat, locale.toLanguageTag());
        }
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendPattern(dateFormat);
        if (isZeroTimeDefaulting()) {
            builder.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0);
            builder.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0);
            builder.parseDefaulting(ChronoField.HOUR_OF_DAY, 0);
        }
        return new JsonbDateFormatter(builder.toFormatter(locale), dateFormat, locale.toLanguageTag());
    }

    private String getGlobalConfigJsonbDateFormat() {
        return getConfigProperty(JsonbConfig.DATE_FORMAT, String.class, JsonbDateFormat.DEFAULT_FORMAT);
    }

    private Consumer<List<PropertyModel>> initOrderStrategy() {
        return StrategiesProvider.getOrderingFunction(getPropertyOrderStrategy());
    }

    private String getPropertyOrderStrategy() {
        return getConfigProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY, String.class, PropertyOrderStrategy.LEXICOGRAPHICAL);
    }

    private PropertyNamingStrategy initPropertyNamingStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY);
        if (property.isEmpty()) {
            return StrategiesProvider.getPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY);
        }
        Object propertyNamingStrategy = property.get();
        if (propertyNamingStrategy instanceof String) {
            return StrategiesProvider.getPropertyNamingStrategy((String) propertyNamingStrategy);
        } else if (!(propertyNamingStrategy instanceof PropertyNamingStrategy)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_NAMING_STRATEGY_INVALID));
        }
        return (PropertyNamingStrategy) property.get();
    }

    private PropertyVisibilityStrategy initPropertyVisibilityStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY);
        if (property.isEmpty()) {
            return null;
        }
        final Object propertyVisibilityStrategy = property.get();
        if (propertyVisibilityStrategy instanceof String) {
            return VisibilityStrategiesProvider.getStrategy((String) propertyVisibilityStrategy);
        } else if (!(propertyVisibilityStrategy instanceof PropertyVisibilityStrategy)) {
            throw new JsonbException("JsonbConfig.PROPERTY_VISIBILITY_STRATEGY must be instance of " + PropertyVisibilityStrategy.class);
        }
        return (PropertyVisibilityStrategy) propertyVisibilityStrategy;
    }

    private String initBinaryDataStrategy() {
        if (getConfigProperty(JsonbConfig.STRICT_IJSON, Boolean.class, false)) {
            return BinaryDataStrategy.BASE_64_URL;
        }
        return getConfigProperty(JsonbConfig.BINARY_DATA_STRATEGY, String.class, BinaryDataStrategy.BYTE);
    }

    private boolean initConfigNullable() {
        return getConfigProperty(JsonbConfig.NULL_VALUES, Boolean.class, false);
    }

    private boolean initConfigFailOnUnknownProperties() {
        return getConfigProperty(YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.class, false);
    }

    private boolean initRequiredCreatorParameters() {
        String sysProp = AccessController.doPrivileged((PrivilegedAction<String>)
                () -> System.getProperty(JsonbConfig.CREATOR_PARAMETERS_REQUIRED));

        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        return getConfigProperty(JsonbConfig.CREATOR_PARAMETERS_REQUIRED, Boolean.class, false);
    }

    private boolean initDateInMillisecondsAsString() {
        String sysProp = AccessController.doPrivileged((PrivilegedAction<String>)
                () -> System.getProperty(YassonConfig.DATE_TIME_IN_MILLIS_AS_A_STRING));

        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        return getConfigProperty(YassonConfig.DATE_TIME_IN_MILLIS_AS_A_STRING, Boolean.class, false);
    }

    @SuppressWarnings("unchecked")
    private JsonbSerializer<Object> initNullSerializer() {
        return jsonbConfig.getProperty(YassonConfig.NULL_ROOT_SERIALIZER)
                .map(o -> {
                    if (!(o instanceof JsonbSerializer)) {
                        throw new JsonbException("YassonConfig.NULL_ROOT_SERIALIZER must be instance of " + JsonbSerializer.class
                                                         + "<Object>");
                    }
                    return (JsonbSerializer<Object>) o;
                }).orElse(null);
    }

    private Set<Class<?>> initEagerInitClasses() {
        Optional<Object> property = jsonbConfig.getProperty(YassonConfig.EAGER_PARSE_CLASSES);
        if (property.isEmpty()) {
            return Collections.emptySet();
        }
        Object eagerInitClasses = property.get();
        if (!(eagerInitClasses instanceof Class<?>[])) {
            throw new JsonbException("YassonConfig.EAGER_PARSE_CLASSES must be instance of Class<?>[]");
        }
        return new HashSet<>(Arrays.asList((Class<?>[]) eagerInitClasses));
    }

    private boolean initForceMapArraySerializerForNullKeys() {
        return getConfigProperty(YassonConfig.FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS, Boolean.class, false);
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
     * @return {@link JsonbException} is risen on unknown property. Default is true even if
     * not set in json config.
     */
    public boolean getConfigFailOnUnknownProperties() {
        return failOnUnknownProperties;
    }

    private <T> T getConfigProperty(String propertyName, Class<T> propertyType, T defaultValue) {
        Objects.requireNonNull(defaultValue, "Default value cannot be null");
        return jsonbConfig.getProperty(propertyName)
                .or(() -> Optional.of(defaultValue))
                .filter(propertyType::isInstance)
                .map(propertyType::cast)
                .orElseThrow(() -> new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE,
                                                                          propertyName,
                                                                          propertyType.getSimpleName())));
    }

    /**
     * Checks for binary data strategy to use.
     *
     * @return Binary data strategy.
     */
    public String getBinaryDataStrategy() {
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
        return getConfigProperty(JsonbConfig.LOCALE, Locale.class, Locale.getDefault());
    }

    private boolean initStrictJson() {
        return getConfigProperty(JsonbConfig.STRICT_IJSON, Boolean.class, false);
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

    /**
     * Default {@link java.util.Map} implementation to use, based on order strategy.
     *
     * @return map impl type
     */
    public Class<?> getDefaultMapImplType() {
        return defaultMapImplType;
    }

    public JsonbSerializer<Object> getNullSerializer() {
        return nullSerializer;
    }

    public boolean hasRequiredCreatorParameters() {
        return requiredCreatorParameters;
    }

    public Set<Class<?>> getEagerInitClasses() {
        return eagerInitClasses;
    }

    /**
     * Whether the MapToEntriesArraySerializer is selected when a null key
     * is detected in a map.
     *
     * @return false or true
     */
    public boolean isForceMapArraySerializerForNullKeys() {
        return forceMapArraySerializerForNullKeys;
    }

    public boolean isDateInMillisecondsAsString() {
        return dateInMillisecondsAsString;
    }
}
