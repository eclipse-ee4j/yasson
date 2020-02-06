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

package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class JsonbPropertyNillable {

    @JsonbProperty(nillable = true)
    private String nullField;

    @JsonbProperty(nillable = false)
    private String nillableOverride;

    public String getNullField() {
        return nullField;
    }

    public void setNullField(String nullField) {
        this.nullField = nullField;
    }

    @JsonbProperty(nillable = true)
    public String getNillableOverride() {
        return nillableOverride;
    }

    public void setNillableOverride(String nillableOverride) {
        this.nillableOverride = nillableOverride;
    }
}
