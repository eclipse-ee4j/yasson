/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.customization.transients.models;


import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

public class TransientSetterPlusCustomizationAnnotatedGetterContainer {
    private String instance = "INSTANCE";

    @JsonbProperty("instance")
    public String getInstance() {
        return instance;
    }

    @JsonbTransient
    public void setInstance(String instance) {
        this.instance = instance;
    }
}
