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
 * Holds runtime type info of the class. Used for generic type resolution, especially during unmarshalling.
 */
public interface RuntimeTypeInfo {

    /**
     * Runtime type holder of a wrapper class of this runtime type.
     *
     * @return Runtime type info
     */
    RuntimeTypeInfo getWrapper();

    /**
     * Returns a runtime type. It can be a class, {@link java.lang.reflect.ParameterizedType} or
     * {@link java.lang.reflect.TypeVariable}.
     *
     * @return Runtime type or null if not defined.
     */
    Type getRuntimeType();
}
