/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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

/**
 * Type wrapper used to represent polymorphic types during serialization/deserialization.
 *
 * @param <T> Type to wrap.
 * @author Roman Grigoriadi
 */
public class TypeWrapper<T> {

    private String className;
    private T instance;

    /**
     * Gets class name.
     *
     * @return Class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets class name.
     *
     * @param className Class name to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets instance.
     *
     * @return Instance.
     */
    public T getInstance() {
        return instance;
    }

    /**
     * Sets instance.
     *
     * @param instance Instance to set.
     */
    public void setInstance(T instance) {
        this.instance = instance;
    }
}
