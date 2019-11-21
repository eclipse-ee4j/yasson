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

package org.eclipse.yasson.internal.serializer;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ProcessingContext;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Serializes an object with user defined serializer.
 *
 * @param <T> type of serializer
 */
public class UserSerializerSerializer<T> implements JsonbSerializer<T> {

    private final JsonbSerializer<T> userSerializer;

    private final ClassModel classModel;

    /**
     * Create instance of current item with its builder.
     *
     * @param classModel     model
     * @param userSerializer user serializer
     */
    public UserSerializerSerializer(ClassModel classModel, JsonbSerializer<T> userSerializer) {
        this.classModel = classModel;
        this.userSerializer = userSerializer;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        ProcessingContext context = (Marshaller) ctx;
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
