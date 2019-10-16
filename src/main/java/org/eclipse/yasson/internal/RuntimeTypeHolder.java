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

package org.eclipse.yasson.internal;

import java.lang.reflect.Type;

/**
 * Holds runtime type and wrapper runtime type info if any.
 */
public class RuntimeTypeHolder implements RuntimeTypeInfo {

    private final RuntimeTypeInfo wrapper;

    private final Type runtimeType;

    /**
     * Creates a new instance.
     *
     * @param wrapper     runtime info about class
     * @param runtimeType class type
     */
    public RuntimeTypeHolder(RuntimeTypeInfo wrapper, Type runtimeType) {
        this.wrapper = wrapper;
        this.runtimeType = runtimeType;
    }

    /**
     * Wrapper containing property of this type.
     *
     * @return wrapper
     */
    @Override
    public RuntimeTypeInfo getWrapper() {
        return wrapper;
    }

    /**
     * Runtime type of this item.
     *
     * @return runtime type
     */
    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }
}
