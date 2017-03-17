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

package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.cdi.JsonbComponentInstanceCreator;
import org.eclipse.yasson.internal.cdi.JsonbComponentInstanceCreatorFactory;
import org.eclipse.yasson.internal.internalOrdering.AnyOrderStrategy;
import org.eclipse.yasson.internal.internalOrdering.LexicographicalOrderStrategy;
import org.eclipse.yasson.internal.internalOrdering.PropOrderStrategy;
import org.eclipse.yasson.internal.internalOrdering.ReverseOrderStrategy;
import org.eclipse.yasson.internal.naming.DefaultNamingStrategies;
import org.eclipse.yasson.internal.naming.IdentityStrategy;
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
import javax.json.spi.JsonProvider;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Jsonb context holding central components and configuration of jsonb runtime. Scoped to instance of Jsonb runtime.
 * Thread safe.
 *
 * @author Roman Grigoriadi
 */
public class JsonbContext {

    private final JsonbConfig jsonbConfig;

    private final MappingContext mappingContext;

    private final JsonbComponentInstanceCreator componentInstanceCreator;

    private final PropertyVisibilityStrategy propertyVisibilityStrategy;

    private final PropertyNamingStrategy propertyNamingStrategy;

    private final JsonProvider jsonProvider;

    private final ComponentMatcher componentMatcher;

    private final JsonbDateFormatter dateFormatter;

    private final AnnotationIntrospector annotationIntrospector;

    private final PropertyOrdering propertyOrdering;

    private boolean genericComponents;

    /**
     * Creates and initialize context.
     *
     * @param jsonbConfig jsonb jsonbConfig not null
     * @param jsonProvider provider of JSONP
     */
    public JsonbContext(JsonbConfig jsonbConfig, JsonProvider jsonProvider) {
        Objects.requireNonNull(jsonbConfig);
        this.jsonbConfig = jsonbConfig;
        this.mappingContext = new MappingContext(this);
        this.componentInstanceCreator = JsonbComponentInstanceCreatorFactory.getComponentInstanceCreator();
        this.componentMatcher = new ComponentMatcher(this);
        this.annotationIntrospector = new AnnotationIntrospector(this);
        this.propertyNamingStrategy = resolvePropertyNamingStrategy();
        this.propertyVisibilityStrategy = resolvePropertyVisibilityStrategy();
        this.jsonProvider = jsonProvider;
        this.propertyOrdering = new PropertyOrdering(initOrderStrategy());
        this.dateFormatter = initDateFormatter();
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

    private PropertyNamingStrategy resolvePropertyNamingStrategy() {
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

    private PropertyVisibilityStrategy resolvePropertyVisibilityStrategy() {
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

    /**
     * Gets {@link JsonbConfig}.
     *
     * @return Configuration.
     */
    public JsonbConfig getConfig() {
        return jsonbConfig;
    }

    /**
     * Gets mapping context.
     *
     * @return Mapping context.
     */
    public MappingContext getMappingContext() {
        return mappingContext;
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
     * Gets JSONP provider.
     *
     * @return JSONP provider.
     */
    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    /**
     * Implementation creating instances of user components used by JSONB, such as adapters and strategies.
     *
     * @return Instance creator.
     */
    public JsonbComponentInstanceCreator getComponentInstanceCreator() {
        return componentInstanceCreator;
    }

    /**
     * Component matcher for lookup of (de)serializers and adapters.
     *
     * @return Component matcher.
     */
    public ComponentMatcher getComponentMatcher() {
        return componentMatcher;
    }

    /**
     * Checks for binary data strategy to use.
     *
     * @return Binary data strategy.
     */
    public  String getBinaryDataStrategy() {
        final Optional<Boolean> iJson = jsonbConfig.getProperty(JsonbConfig.STRICT_IJSON).map((obj->(Boolean)obj));
        if (iJson.isPresent() && iJson.get()) {
            return BinaryDataStrategy.BASE_64_URL;
        }
        final Optional<String> strategy = jsonbConfig.getProperty(JsonbConfig.BINARY_DATA_STRATEGY).map((obj) -> (String) obj);
        return strategy.orElse(BinaryDataStrategy.BYTE);
    }

    private JsonbDateFormatter initDateFormatter() {
        final String dateFormat = getGlobalConfigJsonbDateFormat();
        final Locale locale = getConfigLocale();
        //In case of java.time singleton formats will be used inside related (de)serializers,
        //in case of java.util.Date and Calendar new instances will be created TODO PERF consider synchronization
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat) || JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat)) {
            return new JsonbDateFormatter(dateFormat, locale.toLanguageTag());
        }
        //if possible create shared instance of java.time formatter.
        return new JsonbDateFormatter(DateTimeFormatter.ofPattern(dateFormat, locale), dateFormat, locale.toLanguageTag());
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

    /**
     * Converts string locale to {@link Locale}.
     *
     * @param locale Locale to convert.
     * @return {@link Locale} instance.
     */
    public Locale getLocale(String locale) {
        if (locale.equals(JsonbDateFormat.DEFAULT_LOCALE)) {
            return getConfigLocale();
        }
        return Locale.forLanguageTag(locale);
    }

    /**
     * Gets locale from {@link JsonbConfig}.
     *
     * @return Configured locale.
     */
    public Locale getConfigLocale() {
        final Optional<Object> localeProperty = jsonbConfig.getProperty(JsonbConfig.LOCALE);
        return  localeProperty.map(loc -> {
            if (!(loc instanceof Locale)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.LOCALE, Locale.class.getSimpleName()));
            }
            return (Locale) loc;
        }).orElseGet(Locale::getDefault);
    }

    /**
     * Gets nullable from {@link JsonbConfig}.
     * If true null values are serialized to json.
     *
     * @return Configured nullable
     */
    public boolean getConfigNullable() {
        return getBooleanConfigProperty(JsonbConfig.NULL_VALUES, false);
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
        return getBooleanConfigProperty(JsonbConfig.FAIL_ON_UNKNOWN_PROPERTIES, true);
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
     * Gets instantiated shared config date formatter.
     *
     * @return Date formatter.
     */
    public JsonbDateFormatter getConfigDateFormatter() {
        return dateFormatter;
    }

    /**
     * Gets component for annotation parsing.
     *
     * @return Annotation introspector.
     */
    public AnnotationIntrospector getAnnotationIntrospector() {
        return annotationIntrospector;
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
     * Flag for searching for generic serializers and adapters in runtime.
     *
     * @return True if generic components are present.
     */
    public boolean genericComponentsPresent() {
        return genericComponents;
    }

    /**
     * Set flag for searching for generic serializers and adapters in runtime.
     */
    public void registerGenericComponentFlag() {
        this.genericComponents = true;
    }
}
