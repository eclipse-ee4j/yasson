/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;

import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Search for instance creator from other sources.
 * Mainly intended to add extensibility for different java versions and new features.
 */
public class ClassMultiReleaseExtension {

    private ClassMultiReleaseExtension() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    static boolean shouldTransformToPropertyName(Method method) {
        return !method.getDeclaringClass().isRecord();
    }

    static boolean isSpecialAccessorMethod(Method method, Map<String, Property> classProperties) {
        return isRecord(method.getDeclaringClass())
                && method.getParameterCount() == 0
                && !void.class.equals(method.getReturnType())
                && classProperties.containsKey(method.getName());
    }

    static JsonbCreator findCreator(Class<?> clazz,
                                    Constructor<?>[] declaredConstructors,
                                    AnnotationIntrospector introspector,
                                    PropertyNamingStrategy propertyNamingStrategy) {
        if (clazz.isRecord()) {
            if (declaredConstructors.length == 1) {
                return introspector.createJsonbCreator(declaredConstructors[0], null, clazz, propertyNamingStrategy);
            }
        }
        return null;
    }

    public static boolean isRecord(Class<?> clazz) {
        return clazz.isRecord();
    }

    public static Optional<JsonbException> exceptionToThrow(Class<?> clazz) {
        if (clazz.isRecord()) {
            if (clazz.getDeclaredConstructors().length > 1) {
                return Optional.of(new JsonbException(Messages.getMessage(MessageKeys.RECORD_MULTIPLE_CONSTRUCTORS, clazz)));
            }
        }
        return Optional.empty();
    }

}
