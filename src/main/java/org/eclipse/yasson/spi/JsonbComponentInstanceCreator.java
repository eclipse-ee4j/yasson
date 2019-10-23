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
