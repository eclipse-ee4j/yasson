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

import org.eclipse.yasson.internal.JsonbContext;

import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        switch (mode) {
            case GET:
                getValueCommand = new GetFromGetter(method);
                break;
            case SET:
                setValueCommand = new SetWithSetter(method);
                break;
            default: throw new IllegalStateException("Unknown mode");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void acceptField(Field field, OperationMode mode) {
        switch (mode) {
            case GET:
                getValueCommand = new GetFromField(field);
                break;
            case SET:
                setValueCommand = new SetWithField(field);
                break;
            default: throw new IllegalStateException("Unknown mode");
        }
    }

    @Override
    void setValue(Object object, Object value) {
        setValueCommand.setValue(object, value);
    }

    @Override
    Object getValue(Object object) {
        return getValueCommand.getValue(object);
    }
}
