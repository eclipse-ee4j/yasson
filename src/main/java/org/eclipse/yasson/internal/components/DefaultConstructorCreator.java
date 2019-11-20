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

package org.eclipse.yasson.internal.components;

import java.io.IOException;

import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

/**
 * Creates components instance with default constructor.
 */
public class DefaultConstructorCreator implements JsonbComponentInstanceCreator {

    private final InstanceCreator creator;

    /**
     * Constructs default constructor creator.
     *
     * @param creator instance creator
     */
    DefaultConstructorCreator(InstanceCreator creator) {
        this.creator = creator;
    }

    @Override
    public <T> T getOrCreateComponent(Class<T> componentClass) {
        return creator.createInstance(componentClass);
    }

    @Override
    public void close() throws IOException {

    }
}
