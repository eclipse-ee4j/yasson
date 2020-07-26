/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Serializes an object with user defined serializer.
 *
 * @param <T> type of serializer
 */
public class UserSerializerSerializer<T> implements JsonbSerializer<T> {

    private final JsonbSerializer<T> userSerializer;

    /**
     * Create instance of current item with its builder.
     *
     * @param userSerializer user serializer
     */
    public UserSerializerSerializer(JsonbSerializer<T> userSerializer) {
        this.userSerializer = userSerializer;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        Marshaller context = (Marshaller) ctx;
        try {
            if (context.addProcessedObject(obj)) {
                userSerializer.serialize(obj, generator, ctx);
            } else {
                throw new JsonbException(Messages.getMessage(MessageKeys.RECURSIVE_REFERENCE, obj.getClass()));
            }
        } finally {
            context.removeProcessedObject(obj);
        }
    }
}
