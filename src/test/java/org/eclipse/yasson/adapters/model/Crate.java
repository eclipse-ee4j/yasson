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
public class Crate {

    private String crateStrField;

    private Integer crateIntField;

    public Crate() {
    }

    public Crate(String crateStrField, Integer crateIntField) {
        this.crateStrField = crateStrField;
        this.crateIntField = crateIntField;
    }

    public String getCrateStrField() {
        return crateStrField;
    }

    public void setCrateStrField(String crateStrField) {
        this.crateStrField = crateStrField;
    }

    public Integer getCrateIntField() {
        return crateIntField;
    }

    public void setCrateIntField(Integer crateIntField) {
        this.crateIntField = crateIntField;
    }
}
