/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.adapters.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * @author David Kral
 */
public class BoxWithSerializerSerializer implements JsonbSerializer<BoxWithSerializer> {

    @Override
    public void serialize(BoxWithSerializer boxWithSerializer, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        jsonGenerator
                .writeStartObject()
                .writeKey("boxInteger").write(boxWithSerializer.getBoxIntegerField())
                .writeKey("boxStr").write(boxWithSerializer.getBoxStrField())
                .writeEnd();
    }
}
