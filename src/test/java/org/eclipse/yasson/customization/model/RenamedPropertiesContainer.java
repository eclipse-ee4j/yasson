/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbProperty;

public class RenamedPropertiesContainer {
    @JsonbProperty("first")
    private int intInstance;
    @JsonbProperty("second")
    private String stringInstance;
    @JsonbProperty("third")
    private long longInstance;

    public String getStringInstance() {
        return stringInstance;
    }

    public void setStringInstance(String stringInstance) {
        this.stringInstance = stringInstance;
        if (intInstance == 1) {
            intInstance = 2;
        }
    }

    public int getIntInstance() {
        return intInstance;
    }

    public void setIntInstance(int intInstance) {
        this.intInstance = intInstance;
    }

    public long getLongInstance() {
        return longInstance;
    }

    public void setLongInstance(long longInstance) {
        this.longInstance = longInstance;
        if (intInstance == 2) {
            intInstance = 3;
        }
    }
}
