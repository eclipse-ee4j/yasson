/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

/**
 * @author Roman Grigoriadi
 */
public class GenericBox<X> {

    private String strField;

    private X x;

    public GenericBox(String strField, X x) {
        this.strField = strField;
        this.x = x;
    }

    public GenericBox() {
    }

    public X getX() {
        return x;
    }

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public void setX(X x) {
        this.x = x;
    }
}
