/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2024 Payara Foundation and/or its affiliates. All rights reserved.
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
 * @deprecated Use {@link YassonConfig} instead
 */
@Deprecated
public class YassonProperties {

    private YassonProperties() {
        throw new IllegalStateException("Util classes cannot be instantiated.");
    }

    /**
     * @deprecated Use {@link YassonConfig#FAIL_ON_UNKNOWN_PROPERTIES} instead
     * @see YassonConfig#withFailOnUnknownProperties(boolean)
     */
    @Deprecated
    public static final String FAIL_ON_UNKNOWN_PROPERTIES = YassonConfig.FAIL_ON_UNKNOWN_PROPERTIES;

    /**
     * @deprecated Use {@link YassonConfig#USER_TYPE_MAPPING} instead
     * @see YassonConfig#withUserTypeMapping(java.util.Map)
     */
    @Deprecated
    public static final String USER_TYPE_MAPPING = YassonConfig.USER_TYPE_MAPPING;
    
    /**
     * @deprecated Use {@link YassonConfig#ZERO_TIME_PARSE_DEFAULTING} instead
     * @see YassonConfig#withZeroTimeParseDefaulting(boolean)
     */
    @Deprecated
    public static final String ZERO_TIME_PARSE_DEFAULTING = YassonConfig.ZERO_TIME_PARSE_DEFAULTING;

    /**
     * @deprecated Use {@link YassonConfig#NULL_ROOT_SERIALIZER} instead
     * @see YassonConfig#withNullRootSerializer(jakarta.json.bind.serializer.JsonbSerializer)
     */
    @Deprecated
    public static final String NULL_ROOT_SERIALIZER = YassonConfig.NULL_ROOT_SERIALIZER;

}
