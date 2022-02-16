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

package org.eclipse.yasson.internal.model.customization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Provider of the JSON-B visibility strategies.
 */
public class VisibilityStrategiesProvider {

    private static final PropertyVisibilityStrategy PUBLIC_PROPERTY = new PublicPropertyVisibilityStrategy();
    private static final PropertyVisibilityStrategy PUBLIC_ACCESSOR_METHODS = new PublicAccessorVisibilityStrategy();
    private static final PropertyVisibilityStrategy PUBLIC_FIELDS = new PublicFieldsVisibilityStrategy();
    private static final PropertyVisibilityStrategy ALL_FIELDS_AND_METHODS = new AllFieldsVisibilityStrategy();

    private VisibilityStrategiesProvider() {
        throw new IllegalStateException("This class cannot be instantiated");
    }

    public static PropertyVisibilityStrategy getStrategy(String strategy) {
        switch (strategy) {
        case "PUBLIC_PROPERTY":
            return PUBLIC_PROPERTY;
        case "PUBLIC_ACCESSOR_METHODS":
            return PUBLIC_ACCESSOR_METHODS;
        case "PUBLIC_FIELDS":
            return PUBLIC_FIELDS;
        case "ALL_FIELD_AND_ACCESSORS":
            return ALL_FIELDS_AND_METHODS;
        default:
            throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_VISIBILITY_STRATEGY, strategy));
        }
    }

    private static final class PublicPropertyVisibilityStrategy implements PropertyVisibilityStrategy {
        @Override
        public boolean isVisible(Field field) {
            return Modifier.isPublic(field.getModifiers());
        }

        @Override
        public boolean isVisible(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    }

    private static final class PublicAccessorVisibilityStrategy implements PropertyVisibilityStrategy {

        @Override
        public boolean isVisible(Field field) {
            return false;
        }

        @Override
        public boolean isVisible(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }

    }

    private static final class PublicFieldsVisibilityStrategy implements PropertyVisibilityStrategy {

        @Override
        public boolean isVisible(Field field) {
            return Modifier.isPublic(field.getModifiers());
        }

        @Override
        public boolean isVisible(Method method) {
            return false;
        }

    }

    private static final class AllFieldsVisibilityStrategy implements PropertyVisibilityStrategy {
        @Override
        public boolean isVisible(Field field) {
            return true;
        }

        @Override
        public boolean isVisible(Method method) {
            return true;
        }
    }

}
