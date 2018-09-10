/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David Kral
 ******************************************************************************/
package org.eclipse.yasson.internal.model;

import java.lang.reflect.Type;

/**
 * This class is used to hold method getter return type or setter parameter type
 *
 * @author David kral
 */
public class AccessMethodType {

    private final Type methodType;

    public AccessMethodType(Type methodType) {
        this.methodType = methodType;
    }

    public Type getMethodType() {
        return methodType;
    }
}
