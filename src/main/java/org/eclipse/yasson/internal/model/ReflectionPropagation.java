/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.yasson.internal.model;

import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.bind.JsonbException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;
import java.util.Objects;

/**
 * @author Roman Grigoriadi
 */
public class ReflectionPropagation extends PropertyValuePropagation {

    private GetValueCommand getValueCommand;

    private SetValueCommand setValueCommand;

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
            default: throw new IllegalStateException("Unknown mode");
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
            default: throw new IllegalStateException("Unknown mode");
        }
    }

    /**
     * Sets a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method setter}.
     *
     * @param object object to invoke set value on, not null.
     * @param value object to be set, nullable.
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
     * @throws JsonbException if reflection fails.
     * @return value
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
