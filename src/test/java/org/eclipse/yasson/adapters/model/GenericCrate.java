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

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class GenericCrate<T> {

    private String crateStrField;

    @JsonbProperty("adaptedT")
    private T t;

    public GenericCrate(String crateStrField, T t) {
        this.crateStrField = crateStrField;
        this.t = t;
    }

    public GenericCrate() {
    }

    public String getCrateStrField() {
        return crateStrField;
    }

    public T getT() {
        return t;
    }

    public void setCrateStrField(String crateStrField) {
        this.crateStrField = crateStrField;
    }

    public void setT(T t) {
        this.t = t;
    }
}
