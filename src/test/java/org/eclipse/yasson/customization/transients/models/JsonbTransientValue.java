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

package org.eclipse.yasson.customization.transients.models;

import jakarta.json.bind.annotation.JsonbTransient;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTransientValue {

    private String plainProperty;

    @JsonbTransient
    public String publicTransient = "publicTransient";

    @JsonbTransient
    private String transientFieldWithGetterNoSetter = "transientFieldWithGetterNoSetter";

    @JsonbTransient
    private String propertyTransient;

    private String getterTransient;

    private String setterTransient;

    @JsonbTransient
    private String getterAndPropertyTransient;

    @JsonbTransient
    private String setterAndPropertyTransient;

    private String setterAndGetterTransient;

    @JsonbTransient
    private String setterAndGetterAndPropertyTransient;


    public String getPropertyTransient() {
        return propertyTransient;
    }

    public void setPropertyTransient(String propertyTransient) {
        this.propertyTransient = propertyTransient;
    }



    public String getPlainProperty() {
        return plainProperty;
    }

    public void setPlainProperty(String plainProperty) {
        this.plainProperty = plainProperty;
    }



    @JsonbTransient
    public String getGetterTransient() {
        return getterTransient;
    }

    public void setGetterTransient(String getterTransient) {
        this.getterTransient = getterTransient;
    }



    public String getSetterTransient() {
        return setterTransient;
    }

    @JsonbTransient
    public void setSetterTransient(String setterTransient) {
        this.setterTransient = setterTransient;
    }



    @JsonbTransient
    public String getGetterAndPropertyTransient() {
        return getterAndPropertyTransient;
    }

    public void setGetterAndPropertyTransient(String getterAndPropertyTransient) {
        this.getterAndPropertyTransient = getterAndPropertyTransient;
    }



    public String getSetterAndPropertyTransient() {
        return setterAndPropertyTransient;
    }

    @JsonbTransient
    public void setSetterAndPropertyTransient(String setterAndPropertyTransient) {
        this.setterAndPropertyTransient = setterAndPropertyTransient;
    }



    @JsonbTransient
    public String getSetterAndGetterTransient() {
        return setterAndGetterTransient;
    }

    @JsonbTransient
    public void setSetterAndGetterTransient(String setterAndGetterTransient) {
        this.setterAndGetterTransient = setterAndGetterTransient;
    }



    @JsonbTransient
    public String getSetterAndGetterAndPropertyTransient() {
        return setterAndGetterAndPropertyTransient;
    }

    @JsonbTransient
    public void setSetterAndGetterAndPropertyTransient(String setterAndGetterAndPropertyTransient) {
        this.setterAndGetterAndPropertyTransient = setterAndGetterAndPropertyTransient;
    }


    public String getTransientFieldWithGetterNoSetter() {
        return transientFieldWithGetterNoSetter;
    }
}
