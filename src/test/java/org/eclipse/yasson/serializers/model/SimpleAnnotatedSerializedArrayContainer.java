/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.serializers.model;



import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
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
