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

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class GenericPropertyPojoSerializer implements JsonbSerializer<GenericPropertyPojo<Number>> {
    @Override
    public void serialize(GenericPropertyPojo<Number> obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.writeKey("propertyByUserSerializer");
        generator.write("Number value [" + obj.getProperty() + "]");
        generator.writeEnd();
    }
}
