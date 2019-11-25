package org.eclipse.yasson;

import javax.json.bind.JsonbConfig;
import javax.json.bind.serializer.JsonbSerializer;

import static org.eclipse.yasson.YassonProperties.*;

import java.util.Map;

public class YassonConfig extends JsonbConfig {
    
    /**
     * @param Whether or not to fail if unknown properties are encountered
     * @return This YassonConfig instance
     * @see YassonProperties#FAIL_ON_UNKNOWN_PROPERTIES
     */
    public YassonConfig withFailOnUnknownProperties(boolean failOnUnknownProperties) {
        setProperty(FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        return this;
    }
    
    /**
     * @param mapping A map of interface class -> implementation class mappings
     * @return This YassonConfig instance
     * @see YassonProperties#USER_TYPE_MAPPING
     */
    public YassonConfig withUserTypeMapping(Map<Class<?>, Class<?>> mapping) {
        setProperty(USER_TYPE_MAPPING, mapping);
        return this;
    }
    
    /**
     * @param defaultZeroHour Whether or not to default parsing dates to the zero hour
     * @return This YassonConfig instance
     * @see YassonProperties#ZERO_TIME_PARSE_DEFAULTING
     */
    public YassonConfig withZeroTimeParseDefaulting(boolean defaultZeroHour) {
        setProperty(ZERO_TIME_PARSE_DEFAULTING, defaultZeroHour);
        return this;
    }
    
    /**
     * @param nullSerializer JsonbSerializer instance to use for serializing null root values
     * @return This YassonConfig instance
     * @see YassonProperties#NULL_ROOT_SERIALIZER
     */
    public YassonConfig withNullRootSerializer(JsonbSerializer<?> nullSerializer) {
        setProperty(NULL_ROOT_SERIALIZER, nullSerializer);
        return this;
    }
    
}
