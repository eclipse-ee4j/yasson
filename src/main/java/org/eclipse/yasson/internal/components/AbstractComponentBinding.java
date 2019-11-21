/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

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
