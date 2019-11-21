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

package org.eclipse.yasson.spi;

import java.io.Closeable;

/**
 * Creates instances of JsonbComponents such as JsonbAdapter.
 *
 * <p>
 * Yasson attempts to load the implementations using {@link java.util.ServiceLoader} first. If there are multiple
 * implementations found the service provider with the highest priority is used. If there are no service providers found the
 * default implementation is used.
 * </p>
 */
public interface JsonbComponentInstanceCreator extends Closeable {

    /**
     * Default component priority.
     */
    int DEFAULT_PRIORITY = 0;

    /**
     * Returns instance of JsonbComponent for desired class.
     *
     * @param <T>            Jsonb component type
     * @param componentClass component class
     * @return component instance
     */
    <T> T getOrCreateComponent(Class<T> componentClass);

    /**
     * @return the priority of the component
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
