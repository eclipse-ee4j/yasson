/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.inheritance.model.generics;

/**
 * @author Roman Grigoriadi
 */
public abstract class AbstractZeroLevelGeneric<ZF, Z> {

    private Z inZero;

    protected ZF inZeroOverriddenInFirst;

    abstract ZF getInZeroOverriddenInFirst();

    abstract void setInZeroOverriddenInFirst(ZF value);

    public Z getInZero() {
        return inZero;
    }

    public void setInZero(Z inZero) {
        this.inZero = inZero;
    }
}
