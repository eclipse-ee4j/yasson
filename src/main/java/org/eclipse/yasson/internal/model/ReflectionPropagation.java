/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * Property value propagation by reflection.
 */
public class ReflectionPropagation extends PropertyValuePropagation {

    private GetValueCommand getValueCommand;

    private SetValueCommand setValueCommand;

    /**
     * Creates new instance of reflection propagation.
     *
     * @param property target property
     * @param strategy visibility strategy
     */
    public ReflectionPropagation(Property property, PropertyVisibilityStrategy strategy) {
        super(property, strategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void acceptMethod(Method method, OperationMode mode) {
        Objects.requireNonNull(method);

        switch (mode) {
        case GET:
            getValueCommand = method::invoke;
            break;
        case SET:
            setValueCommand = method::invoke;
            break;
        default:
            throw new IllegalStateException("Unknown mode");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void acceptField(Field field, OperationMode mode) {
        Objects.requireNonNull(field);

        switch (mode) {
        case GET:
            getValueCommand = field::get;
            break;
        case SET:
            setValueCommand = field::set;
            break;
        default:
            throw new IllegalStateException("Unknown mode");
        }
    }

    /**
     * Sets a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method setter}.
     *
     * @param object object to invoke set value on, not null.
     * @param value  object to be set, nullable.
     * @throws JsonbException if reflection fails.
     */
    @Override
    void setValue(Object object, Object value) {
        Objects.requireNonNull(object);

        try {
            setValueCommand.setValue(object, value);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JsonbException("Error getting value on: " + object, e);
        }
    }

    /**
     * Get a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method getter}.
     *
     * @param object object to invoke get value on, not null.
     * @return value
     * @throws JsonbException if reflection fails.
     */
    @Override
    Object getValue(Object object) {
        Objects.requireNonNull(object);

        try {
            return getValueCommand.getValue(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new JsonbException("Error getting value on: " + object, e);
        }
    }
}
