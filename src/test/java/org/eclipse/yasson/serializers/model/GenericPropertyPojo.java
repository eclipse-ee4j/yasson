/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.serializers.model;

import javax.json.bind.annotation.JsonbTypeSerializer;

@JsonbTypeSerializer(GenericPropertyPojoSerializer.class)
public class GenericPropertyPojo<T> {
    private T property;

    public T getProperty() {
        return property;
    }

    public void setProperty(T property) {
        this.property = property;
    }
}
