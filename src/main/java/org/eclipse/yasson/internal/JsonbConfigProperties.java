package org.eclipse.yasson.internal;

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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
    }

    private JsonbDateFormatter initDateFormatter(Locale locale) {
        final String dateFormat = getGlobalConfigJsonbDateFormat();
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
}
