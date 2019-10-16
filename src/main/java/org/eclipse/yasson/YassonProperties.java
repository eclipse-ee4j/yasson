/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
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

/**
 * Custom properties for configuring Yasson outside of the specification {@link javax.json.bind.JsonbConfig} scope.
 */
public class YassonProperties {

    private YassonProperties() {
        throw new IllegalStateException("Util classes cannot be instantiated.");
    }

    /**
     * Property used to specify behaviour on deserialization when JSON document contains properties
     * which doesn't exist in the target class. Default value is 'true'.
     */
    public static final String FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";

    /**
     * User type mapping for map interface to implementation classes.
     */
    public static final String USER_TYPE_MAPPING = "jsonb.user-type-mapping";
    /**
     * <p>Makes parsing dates defaulting to zero hour, minute and second.
     * This will made available to parse patterns like yyyy.MM.dd to
     * {@link java.util.Date}, {@link java.util.Calendar}, {@link java.time.Instant} {@link java.time.LocalDate}
     * or even {@link java.time.ZonedDateTime}.
     * <p>If time zone is not set in the pattern then UTC time zone is used.
     * So for example json value 2018.01.01 becomes 2018.01.01 00:00:00 UTC when parsed
     * to instant {@link java.time.Instant} or {@link java.time.ZonedDateTime}.
     */
    public static final String ZERO_TIME_PARSE_DEFAULTING = "jsonb.zero-time-defaulting";

    /**
     * Serializer to use when object provided to {@link javax.json.bind.Jsonb#toJson(Object)} is {@code null} or an empty
     * Optional. Must be instance of {@link javax.json.bind.serializer.JsonbSerializer}{@code <Object>}. Its obj value
     * will be respective parameter.
     */
    public static final String NULL_ROOT_SERIALIZER = "yasson.null-root-serializer";
}
