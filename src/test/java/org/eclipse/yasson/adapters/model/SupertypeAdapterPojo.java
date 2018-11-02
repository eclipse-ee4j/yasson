/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.yasson.adapters.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

public class SupertypeAdapterPojo {

    @JsonbTypeAdapter(NumberAdapter.class)
    private Integer numberInteger;

    @JsonbTypeAdapter(SerializableAdapter.class)
    private Integer serializableInteger;

    public Integer getNumberInteger() {
        return numberInteger;
    }

    public void setNumberInteger(Integer numberInteger) {
        this.numberInteger = numberInteger;
    }

    public Integer getSerializableInteger() {
        return serializableInteger;
    }

    public void setSerializableInteger(Integer serializableInteger) {
        this.serializableInteger = serializableInteger;
    }
}
