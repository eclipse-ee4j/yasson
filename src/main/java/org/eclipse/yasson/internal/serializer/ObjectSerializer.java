/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.util.LinkedHashMap;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Object container serializer.
 */
class ObjectSerializer implements ModelSerializer {

    private final LinkedHashMap<String, ModelSerializer> propertySerializers;

    ObjectSerializer(LinkedHashMap<String, ModelSerializer> propertySerializers) {
        this.propertySerializers = propertySerializers;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        generator.writeStartObject();
        propertySerializers.forEach((key, serializer) -> {
            try {
                context.setKey(key);
                serializer.serialize(value, generator, context);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.SERIALIZE_PROPERTY_ERROR, key,
                                                             value.getClass().getCanonicalName()), e);
            }
        });
        generator.writeEnd();
    }
}
