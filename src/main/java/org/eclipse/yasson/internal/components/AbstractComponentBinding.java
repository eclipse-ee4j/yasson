/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Wrapper for user components, components, (de)serializer.
 * Contains resolved binding type an component.
 */
public abstract class AbstractComponentBinding {

    private final Type bindingType;

    /**
     * Creates info.
     *
     * @param bindingType type to which component is bound.
     */
    public AbstractComponentBinding(Type bindingType) {
        Objects.requireNonNull(bindingType);
        this.bindingType = bindingType;
    }

    /**
     * Resolved binding type of a component.
     *
     * @return binding type
     */
    public Type getBindingType() {
        return bindingType;
    }

    /**
     * Class of user component.
     *
     * @return component class
     */
    public abstract Class<?> getComponentClass();
}
