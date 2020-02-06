/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

/*
 * $Id$
 */

package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbProperty;

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
