/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderZero {

    private String zeroOverriddenInSecond;

    //only setter is overridden
    private String zeroPartiallyOverriddenInFirst;

    private String zero;

    public String getZero() {
        return zero;
    }

    public void setZero(String zero) {
        this.zero = zero;
    }

    public String getZeroOverriddenInSecond() {
        return zeroOverriddenInSecond;
    }

    public void setZeroOverriddenInSecond(String zeroOverriddenInSecond) {
        this.zeroOverriddenInSecond = zeroOverriddenInSecond;
    }

    public String getZeroPartiallyOverriddenInFirst() {
        return zeroPartiallyOverriddenInFirst;
    }

    public void setZeroPartiallyOverriddenInFirst(String zeroPartiallyOverriddenInFirst) {
        this.zeroPartiallyOverriddenInFirst = zeroPartiallyOverriddenInFirst;
    }
}
