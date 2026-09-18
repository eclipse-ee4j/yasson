/*
 * Copyright (c) 2015, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.Collection;

/**
 * @author Alessandro Moscatelli
 */
public class LowerBoundTypeVariableWithCollectionAttributeClass<T extends Shape> {
    
    private Collection<AnotherGenericTestClass<Integer, T>> value;

    public Collection<AnotherGenericTestClass<Integer, T>> getValue() {
        return value;
    }

    public void setValue(Collection<AnotherGenericTestClass<Integer, T>> value) {
        this.value = value;
    }
    
}
