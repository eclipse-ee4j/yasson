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

package org.eclipse.yasson.internal.model.customization;

/**
 * Customization for container like types (Maps, Collections, Arrays).
 */
public class ContainerCustomization extends ClassCustomization {

    /**
     * Creates a new instance.
     *
     * @param builder Builder to initialize from.
     */
    public ContainerCustomization(ClassCustomizationBuilder builder) {
        super(builder);
    }

    /**
     * Creates a new instance.
     *
     * @param other Class customization to initialize from.
     */
    public ContainerCustomization(ClassCustomization other) {
        super(other);
    }

    /**
     * Containers (types mapped to JsonArray) are always nillable by spec.
     *
     * @return always true
     */
    @Override
    public final boolean isNillable() {
        return true;
    }
}
