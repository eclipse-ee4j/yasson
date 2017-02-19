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

import java.lang.reflect.Type;

/**
 * Parameter for creator constructor / method.
 *
 * @author Roman Grigoriadi
 */
public class CreatorParam {
    private final String name;

    private final Type type;

    /**
     * Creates a new instance.
     *
     * @param name Parameter name.
     * @param type Parameter type.
     */
    public CreatorParam(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets parameter type.
     *
     * @return Parameter type.
     */
    public Type getType() {
        return type;
    }
}
