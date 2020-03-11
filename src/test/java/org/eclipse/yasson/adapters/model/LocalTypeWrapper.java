/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 08/06/2017.
 */
public class LocalTypeWrapper<E> {
    private String className;
    private E instance;

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
    public E getInstance() {
        return instance;
    }

    /**
     * Sets instance.
     *
     * @param instance Instance to set.
     */
    public void setInstance(E instance) {
        this.instance = instance;
    }

}
