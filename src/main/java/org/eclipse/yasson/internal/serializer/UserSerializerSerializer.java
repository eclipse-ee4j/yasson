/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates and others.
 * All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  Roman Grigoriadi
 *  Payara Services - Added default serializer
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;

/**
 * Serializes an object with user defined serializer.
 *
 * @author Roman Grigoriadi
 * @param <T> type of serializer
 */
public class UserSerializerSerializer<T> implements JsonbSerializer<T> {

    private final JsonbSerializer<T> userSerializer;

    private final JsonbSerializer<T> defaultSerializer;

    /**
     * Create instance of current item with its builder.
     *
     * @param userSerializer user serializer
     * @param defaultSerializer serializer to use if the object has already been processed by the user serializer
     */
    public UserSerializerSerializer(JsonbSerializer<T> userSerializer, JsonbSerializer<T> defaultSerializer) {
        this.userSerializer = userSerializer;
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        Marshaller context = (Marshaller) ctx;
        try {
            if (context.addProcessedObject(obj)) {
                userSerializer.serialize(obj, generator, ctx);
            } else {
                defaultSerializer.serialize(obj, generator, ctx);
            }
        } finally {
            context.removeProcessedObject(obj);
        }
    }
}
