/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.serializers.model;



public class SimpleContainer {
    private String instance = "Test";

    public SimpleContainer() {
    }

    public SimpleContainer(String instance) {
        this.instance = instance;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
