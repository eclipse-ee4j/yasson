/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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
public class FirstLevelGeneric<F, Z> extends AbstractZeroLevelGeneric<Z, String> {

    private F inFirstLevel;

    @Override
    public Z getInZeroOverriddenInFirst() {
        return inZeroOverriddenInFirst;
    }

    @Override
    public void setInZeroOverriddenInFirst(Z value) {
        inZeroOverriddenInFirst = value;
    }

    public F getInFirstLevel() {
        return inFirstLevel;
    }

    public void setInFirstLevel(F inFirstLevel) {
        this.inFirstLevel = inFirstLevel;
    }
}
