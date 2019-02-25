/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

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
