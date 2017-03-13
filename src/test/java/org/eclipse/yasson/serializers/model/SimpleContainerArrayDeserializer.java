/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.serializers.model;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SimpleContainerArrayDeserializer implements JsonbDeserializer<SimpleContainer[]> {
    public SimpleContainer[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        List<SimpleContainer> containers = new ArrayList<>();

        while (jsonParser.hasNext()) {
            JsonParser.Event event = jsonParser.next();
            if (event == JsonParser.Event.START_OBJECT) {
                containers.add(deserializationContext.deserialize(new SimpleContainer(){}.getClass().getGenericSuperclass(), jsonParser));
            }
            if (event == JsonParser.Event.END_OBJECT) {
                break;
            }
        }

        return containers.toArray(new SimpleContainer[0]);
    }
}
