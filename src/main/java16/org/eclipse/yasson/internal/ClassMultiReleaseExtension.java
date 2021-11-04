/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Set;

import org.eclipse.yasson.internal.model.JsonbCreator;

/**
 * Search for instance creator from other sources.
 * Mainly intended to add extensibility for different java versions and new features.
 */
class ClassMultiReleaseExtension {

    private static final Set<String> NOT_VALID_ACCESSOR_METHODS = Set.of("equals", "hashCode", "toString");

    private ClassMultiReleaseExtension() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    static boolean shouldTransformToPropertyName(Method method) {
        return !method.getDeclaringClass().isRecord();
    }

    static boolean isGetAccessorMethod(Method method) {
        if (method.getDeclaringClass().isRecord()) {
            return !NOT_VALID_ACCESSOR_METHODS.contains(method.getName());
        }
        return false;
    }

    static JsonbCreator findJsonbCreator(Class<?> clazz, Constructor<?>[] declaredConstructors, AnnotationIntrospector introspector) {
        if (clazz.isRecord()) {
            return introspector.createJsonbCreator(declaredConstructors[0], null, clazz);
        }
        return null;
    }

}
