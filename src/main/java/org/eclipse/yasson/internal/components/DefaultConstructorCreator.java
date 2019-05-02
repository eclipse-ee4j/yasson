/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.internal.components;

import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

import java.io.IOException;

/**
 * Creates components instance with default constructor.
 *
 * @author Roman Grigoriadi
 */
public class DefaultConstructorCreator implements JsonbComponentInstanceCreator {

    private final InstanceCreator creator;

    public DefaultConstructorCreator(InstanceCreator creator) {
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
