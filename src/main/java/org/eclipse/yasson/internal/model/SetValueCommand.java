/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper for setting a value on javabean property.
 */
@FunctionalInterface
interface SetValueCommand {

    /**
     * Sets a value with reflection on {@link java.lang.reflect.Field field} or {@link java.lang.reflect.Method setter}.
     *
     * @param object object to invoke set value on, not null.
     * @param value object to be set, nullable.
     * @throws IllegalAccessException if reflection fails.
     * @throws InvocationTargetException if reflection fails.
     */
    void setValue(Object object, Object value) throws IllegalAccessException, InvocationTargetException;
}
