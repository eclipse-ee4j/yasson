/*
 * Copyright (c) 2019, 2022 IBM and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import java.util.Map;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.JsonbSerializer;

/**
 * Custom properties for configuring Yasson outside of the specification {@link jakarta.json.bind.JsonbConfig} scope.
 */
public class YassonConfig extends JsonbConfig {
    
    /**
     * @see #withFailOnUnknownProperties(boolean)
     */
    public static final String FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";

    /**
     * @see #withUserTypeMapping(java.util.Map)
     */
    public static final String USER_TYPE_MAPPING = "jsonb.user-type-mapping";
    
    /**
     * @see #withZeroTimeParseDefaulting(boolean)
     */
    public static final String ZERO_TIME_PARSE_DEFAULTING = "jsonb.zero-time-defaulting";

    /**
     * @see #withNullRootSerializer(jakarta.json.bind.serializer.JsonbSerializer)
     */
    public static final String NULL_ROOT_SERIALIZER = "yasson.null-root-serializer";

    /**
     * @see #withEagerParsing(Class...)
     */
    public static final String EAGER_PARSE_CLASSES = "yasson.eager-parse-classes";

    /**
     * @see #withForceMapArraySerializerForNullKeys(boolean)
     */
    public static final String FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS = "yasson.force-map-array-serializer-for-null-keys";

    /**
     * @see #withTimeInMillisAsAString(boolean)
     */
    public static final String DATE_TIME_IN_MILLIS_AS_A_STRING = "yasson.time-in-millis-as-a-string";

    /**
     * Property used to specify behaviour on deserialization when JSON document contains properties
     * which doesn't exist in the target class. Default value is 'false'.
     * @param failOnUnknownProperties Whether or not to fail if unknown properties are encountered
     * @return This YassonConfig instance
     */
    public YassonConfig withFailOnUnknownProperties(boolean failOnUnknownProperties) {
        setProperty(FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        return this;
    }
    
    /**
     * User type mapping for map interface to implementation classes.
     * @param mapping A map of interface to implementation class mappings
     * @return This YassonConfig instance
     */
    public YassonConfig withUserTypeMapping(Map<Class<?>, Class<?>> mapping) {
        setProperty(USER_TYPE_MAPPING, mapping);
        return this;
    }
    
    /**
     * <p>Makes parsing dates defaulting to zero hour, minute and second.
     * This will made available to parse patterns like yyyy.MM.dd to
     * {@link java.util.Date}, {@link java.util.Calendar}, {@link java.time.Instant} {@link java.time.LocalDate}
     * or even {@link java.time.ZonedDateTime}.
     * <p>If time zone is not set in the pattern then UTC time zone is used.
     * So for example json value 2018.01.01 becomes 2018.01.01 00:00:00 UTC when parsed
     * to instant {@link java.time.Instant} or {@link java.time.ZonedDateTime}.
     * @param defaultZeroHour Whether or not to default parsing dates to the zero hour
     * @return This YassonConfig instance
     */
    public YassonConfig withZeroTimeParseDefaulting(boolean defaultZeroHour) {
        setProperty(ZERO_TIME_PARSE_DEFAULTING, defaultZeroHour);
        return this;
    }
    
    /**
     * Serializer to use when object provided to {@link jakarta.json.bind.Jsonb#toJson(Object)} is {@code null} or an empty
     * Optional. Must be instance of {@link jakarta.json.bind.serializer.JsonbSerializer}{@code <Object>}. Its obj value
     * will be respective parameter.
     * @param nullSerializer JsonbSerializer instance to use for serializing null root values
     * @return This YassonConfig instance
     */
    public YassonConfig withNullRootSerializer(JsonbSerializer<?> nullSerializer) {
        setProperty(NULL_ROOT_SERIALIZER, nullSerializer);
        return this;
    }
    
    /**
     * @param classes A list of classes to eagerly parse upon creation of the Jsonb instance used with this configuration. 
     * @return This YassonConfig instance
     */
    public YassonConfig withEagerParsing(Class<?>... classes) {
        setProperty(EAGER_PARSE_CLASSES, classes);
        return this;
    }

    /**
     * Property needed to make MapToEntriesArraySerializer the serializer used
     * when a null key is found in the map. Default value is false.
     * @param value true to force the use of the MapToEntriesArraySerializer if
     *              a null key is detected in the map, false to work as before
     * @return This YassonConfig instance
     */
    public YassonConfig withForceMapArraySerializerForNullKeys(boolean value) {
        setProperty(FORCE_MAP_ARRAY_SERIALIZER_FOR_NULL_KEYS, value);
        return this;
    }

    /**
     * It is required to handle time millisecond format as a number. See
     * {@link jakarta.json.bind.annotation.JsonbDateFormat#TIME_IN_MILLIS}. It is possible to override this and force
     * Yasson to handle it as a String, by using this method.
     *
     * @param value whether to treat dates formatted by {@link jakarta.json.bind.annotation.JsonbDateFormat#TIME_IN_MILLIS}
     *              as a String. Default value is {@code false}.
     * @return This YassonConfig instance
     * @since 3.0.0
     */
    public YassonConfig withTimeInMillisAsAString(boolean value) {
        setProperty(DATE_TIME_IN_MILLIS_AS_A_STRING, value);
        return this;
    }

}
