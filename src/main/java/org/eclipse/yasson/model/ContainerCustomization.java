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
package org.eclipse.yasson.model;

/**
 * Customization for container like types (Maps, Collections, Arrays).
 *
 * @author Roman Grigoriadi
 */
public class ContainerCustomization extends ClassCustomization {

    /**
     * Creates a new instance.
     *
     * @param builder Builver to initialize from.
     */
    public ContainerCustomization(CustomizationBuilder builder) {
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
