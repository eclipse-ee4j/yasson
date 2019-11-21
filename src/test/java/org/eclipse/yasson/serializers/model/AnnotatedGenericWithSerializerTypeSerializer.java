/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class AnnotatedGenericWithSerializerTypeSerializer implements JsonbSerializer<AnnotatedGenericWithSerializerType<?>> {

    @Override
    public void serialize(AnnotatedGenericWithSerializerType<?> obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        ctx.serialize("generic", obj.value, generator);
        generator.writeEnd();
    }
}
