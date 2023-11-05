/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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
 * Contains resolved binding type and component.
 *
 * @param <C> type of component
 */
public abstract class AbstractComponentBinding<C> {

    private final Type bindingType;

    private final C component;

    /**
     * Creates info.
     *
     * @param bindingType type to which component is bound.
     * @param component   bound component.
     */
    public AbstractComponentBinding(Type bindingType, C component) {
        Objects.requireNonNull(bindingType);
        Objects.requireNonNull(component);
        this.bindingType = bindingType;
        this.component = component;
    }

    /**
     * Resolved binding type of the component.
     *
     * @return binding type
     */
    public Type getBindingType() {
        return bindingType;
    }

    /**
     * Get actual user component.
     *
     * @return user component.
     */
    public C getComponent(){
        return component;
    }

    /**
     * Class of user component.
     *
     * @return component class
     */
    public Class<?> getComponentClass(){
        return component.getClass();
    }
}
