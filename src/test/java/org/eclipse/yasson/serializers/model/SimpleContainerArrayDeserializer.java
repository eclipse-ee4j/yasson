/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
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
