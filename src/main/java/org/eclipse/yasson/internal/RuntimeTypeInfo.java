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
