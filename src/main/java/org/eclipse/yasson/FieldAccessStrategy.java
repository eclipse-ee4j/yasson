/*
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * <p>Strategy that can be used to force always using fields instead of getters setters for getting / setting value.</p>
 * <p>Suggested approach is to use default visibility strategy, which will use public getters / setters, or field
 * if it is public.</p>
 *
 * <p>Please consider, that forcing accessing fields will in most cases (when field is not public)
 * result in calling {@link Field#setAccessible(boolean)} to break into clients code.
 * This may cause problems if client code is loaded as JPMS (Java Platform Module System) module, as OSGi module or
 * when SecurityManager is turned on.</p>
 */
public class FieldAccessStrategy implements PropertyVisibilityStrategy {
    @Override
    public boolean isVisible(Field field) {
        return true;
    }

    @Override
    public boolean isVisible(Method method) {
        return false;
    }
}
