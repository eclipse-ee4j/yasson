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
public class JsonbPropertyName {

    @JsonbProperty("fieldAnnotatedNameCustomized")
    private String fieldAnnotatedName;

    private String methodAnnotName;

    @JsonbProperty("fieldAnnotatedNameToOverride")
    private String fieldOverriddenWithMethodAnnot;

    public String getFieldAnnotatedName() {
        return fieldAnnotatedName;
    }

    public void setFieldAnnotatedName(String fieldAnnotatedName) {
        this.fieldAnnotatedName = fieldAnnotatedName;
    }

    @JsonbProperty("getterAnnotatedName")
    public String getMethodAnnotName() {
        return methodAnnotName;
    }

    @JsonbProperty("setterAnnotatedName")
    public void setMethodAnnotName(String methodAnnotName) {
        this.methodAnnotName = methodAnnotName;
    }

    @JsonbProperty("getterOverriddenName")
    public String getFieldOverriddenWithMethodAnnot() {
        return fieldOverriddenWithMethodAnnot;
    }

    @JsonbProperty("setterOverriddenName")
    public void setFieldOverriddenWithMethodAnnot(String fieldOverriddenWithMethodAnnot) {
        this.fieldOverriddenWithMethodAnnot = fieldOverriddenWithMethodAnnot;
    }
}
