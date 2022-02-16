/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.json.JsonValue;

/**
 * Types which are supported by the Yasson by default.
 */
public class BuiltInTypes {

    private static final Set<Class<?>> BUILD_IN_SUPPORT;

    static {
        Set<Class<?>> buildInTypes = new HashSet<>();
        buildInTypes.add(Byte.class);
        buildInTypes.add(Byte.TYPE);
        buildInTypes.add(BigDecimal.class);
        buildInTypes.add(BigInteger.class);
        buildInTypes.add(Boolean.class);
        buildInTypes.add(Boolean.TYPE);
        buildInTypes.add(Calendar.class);
        buildInTypes.add(Character.class);
        buildInTypes.add(Character.TYPE);
        buildInTypes.add(Date.class);
        buildInTypes.add(Double.class);
        buildInTypes.add(Double.TYPE);
        buildInTypes.add(Duration.class);
        buildInTypes.add(Float.class);
        buildInTypes.add(Float.TYPE);
        buildInTypes.add(Integer.class);
        buildInTypes.add(Integer.TYPE);
        buildInTypes.add(Instant.class);
        buildInTypes.add(LocalDateTime.class);
        buildInTypes.add(LocalDate.class);
        buildInTypes.add(LocalTime.class);
        buildInTypes.add(Long.class);
        buildInTypes.add(Long.TYPE);
        buildInTypes.add(Number.class);
        buildInTypes.add(OffsetDateTime.class);
        buildInTypes.add(OffsetTime.class);
        buildInTypes.add(OptionalDouble.class);
        buildInTypes.add(OptionalInt.class);
        buildInTypes.add(OptionalLong.class);
        buildInTypes.add(Path.class);
        buildInTypes.add(Period.class);
        buildInTypes.add(Short.class);
        buildInTypes.add(Short.TYPE);
        buildInTypes.add(String.class);
        buildInTypes.add(TimeZone.class);
        buildInTypes.add(URI.class);
        buildInTypes.add(URL.class);
        buildInTypes.add(UUID.class);
        if (isClassAvailable("javax.xml.datatype.XMLGregorianCalendar")) {
            buildInTypes.add(XMLGregorianCalendar.class);
        }
        buildInTypes.add(ZonedDateTime.class);
        buildInTypes.add(ZoneId.class);
        buildInTypes.add(ZoneOffset.class);
        if (isClassAvailable("java.sql.Date")) {
            buildInTypes.add(java.sql.Date.class);
            buildInTypes.add(java.sql.Timestamp.class);
        }
        BUILD_IN_SUPPORT = Set.copyOf(buildInTypes);
    }

    private BuiltInTypes() {
        throw new IllegalStateException("Util class cannot be instantiated");
    }

    /**
     * Check whether the class is available.
     *
     * @param className name of the checked class
     * @return true if available, otherwise false
     */
    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    /**
     * Whether the type is a supported type by default.
     *
     * @param clazz type to check
     * @return whether is supported
     */
    public static boolean isKnownType(Class<?> clazz) {
        boolean knownContainerValueType = Collection.class.isAssignableFrom(clazz)
                || Map.class.isAssignableFrom(clazz)
                || JsonValue.class.isAssignableFrom(clazz)
                || Optional.class.isAssignableFrom(clazz)
                || clazz.isArray();

        return knownContainerValueType || findIfClassIsSupported(clazz);
    }

    private static boolean findIfClassIsSupported(Class<?> clazz) {
        Class<?> current = clazz;
        do {
            if (BUILD_IN_SUPPORT.contains(current)) {
                return true;
            }
            current = current.getSuperclass();
        } while (current != null);
        return false;
    }
}
