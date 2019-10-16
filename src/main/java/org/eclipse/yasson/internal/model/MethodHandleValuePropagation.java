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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Propagates values to fields using {@link java.lang.invoke.MethodHandle}
 * <p>
 * Uses field getter / setter implicitly if those are present and public.
 * Uses field direct access otherwise if field is public.
 * </p>
 * Access policy could be modified by {@link javax.json.bind.annotation.JsonbVisibility}
 */
class MethodHandleValuePropagation extends PropertyValuePropagation {

    private MethodHandle getHandle;

    private MethodHandle setHandle;

    MethodHandleValuePropagation(Property property, PropertyVisibilityStrategy propertyVisibilityStrategy) {
        super(property, propertyVisibilityStrategy);
    }

    @Override
    protected void acceptMethod(Method method, OperationMode mode) {
        try {
            switch (mode) {
            case GET:
                getHandle = MethodHandles.lookup().unreflect(method);
                break;
            case SET:
                setHandle = MethodHandles.lookup().unreflect(method);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
            }
        } catch (IllegalAccessException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CREATING_HANDLES), e);
        }
    }

    @Override
    protected void acceptField(Field field, OperationMode mode) {
        try {
            switch (mode) {
            case GET:
                getHandle = MethodHandles.lookup().unreflectGetter(field);
                break;
            case SET:
                setHandle = MethodHandles.lookup().unreflectSetter(field);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
            }
        } catch (IllegalAccessException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CREATING_HANDLES), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object object, Object value) {
        try {
            setHandle.invoke(object, value);
        } catch (Throwable throwable) {
            throw new JsonbException(Messages.getMessage(MessageKeys.SETTING_VALUE_WITH, setHandle), throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object object) {
        try {
            return getHandle.invoke(object);
        } catch (Throwable throwable) {
            throw new JsonbException(Messages.getMessage(MessageKeys.GETTING_VALUE_WITH, getHandle), throwable);
        }
    }

}
