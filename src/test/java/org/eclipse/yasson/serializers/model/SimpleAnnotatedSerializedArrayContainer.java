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

package org.eclipse.yasson.serializers.model;



import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import java.util.List;

public class SimpleAnnotatedSerializedArrayContainer {
    @JsonbTypeSerializer(SimpleContainerArraySerializer.class)
    @JsonbTypeDeserializer(SimpleContainerArrayDeserializer.class)
    private SimpleContainer[] arrayInstance;

    private List<SimpleContainer> listInstance;

    public SimpleContainer[] getArrayInstance() {
        return arrayInstance;
    }

    public void setArrayInstance(SimpleContainer[] arrayInstance) {
        this.arrayInstance = arrayInstance;
    }

    public List<SimpleContainer> getListInstance() {
        return listInstance;
    }

    public void setListInstance(List<SimpleContainer> listInstance) {
        this.listInstance = listInstance;
    }
}
