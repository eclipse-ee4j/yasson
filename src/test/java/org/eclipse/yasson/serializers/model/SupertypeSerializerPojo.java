/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers.model;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;

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
