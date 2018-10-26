/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.serializers.model;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

public class SupertypeSerializerPojo {


    @JsonbTypeSerializer(NumberSerializer.class)
    @JsonbTypeDeserializer(NumberDeserializer.class)
    private Integer numberInteger;

    //Serializers bound with JsonbConfig for this one.
    private Integer anotherNumberInteger;

    public Integer getNumberInteger() {
        return numberInteger;
    }

    public void setNumberInteger(Integer numberInteger) {
        this.numberInteger = numberInteger;
    }

    public Integer getAnotherNumberInteger() {
        return anotherNumberInteger;
    }

    public void setAnotherNumberInteger(Integer anotherNumberInteger) {
        this.anotherNumberInteger = anotherNumberInteger;
    }
}
