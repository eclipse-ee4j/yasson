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

package org.eclipse.yasson.internal.serializer.types;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.serializer.ModelSerializer;
import org.eclipse.yasson.internal.serializer.SerializationModelCreator;

/**
 * Object type serializer. Dynamically resolves the serialized type based on the serialized instance class.
 */
public class ObjectTypeSerializer extends TypeSerializer<Object> {

    private final Customization customization;

    private final Map<Class<?>, ModelSerializer> cache;
    private final List<Type> chain;
    private final boolean isKey;

    ObjectTypeSerializer(TypeSerializerBuilder serializerBuilder) {
        super(serializerBuilder);
        this.customization = serializerBuilder.getCustomization();
        this.cache = new ConcurrentHashMap<>();
        this.chain = new LinkedList<>(serializerBuilder.getChain());
        this.isKey = serializerBuilder.isKey();
    }

    @Override
    void serializeValue(Object value, JsonGenerator generator, SerializationContextImpl context) {
        //Dynamically resolved type during runtime. Cached in SerializationModelCreator.
        findSerializer(value, generator, context);
    }

    @Override
    void serializeKey(Object key, JsonGenerator generator, SerializationContextImpl context) {
        if (key == null) {
            super.serializeKey(null, generator, context);
            return;
        }
        //Dynamically resolved type during runtime. Cached in SerializationModelCreator.
        findSerializer(key, generator, context);
    }

    private void findSerializer(Object key, JsonGenerator generator, SerializationContextImpl context) {
        Class<?> clazz = key.getClass();
        cache.computeIfAbsent(clazz, aClass -> {
            SerializationModelCreator serializationModelCreator = context.getJsonbContext().getSerializationModelCreator();
            return serializationModelCreator.serializerChainRuntime(new LinkedList<>(chain), clazz, customization, false, isKey);
        }).serialize(key, generator, context);
    }

    /**
     * Add serializer to the cache.
     *
     * @param clazz           class of the serializer
     * @param modelSerializer model serializer bound to the class
     */
    public void addSpecificSerializer(Class<?> clazz, ModelSerializer modelSerializer) {
        cache.put(clazz, modelSerializer);
    }
}
