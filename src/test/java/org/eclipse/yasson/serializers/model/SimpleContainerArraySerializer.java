/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class SimpleContainerArraySerializer implements JsonbSerializer<SimpleContainer[]> {
    @Override
    public void serialize(SimpleContainer[] containers, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        jsonGenerator.writeStartArray();
        for (SimpleContainer container : containers) {
            serializationContext.serialize(container, jsonGenerator);
        }
        jsonGenerator.writeEnd();
    }
}
