/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2020 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.AbstractValueTypeSerializer;
import org.eclipse.yasson.internal.serializer.ContainerSerializerProvider;
import org.eclipse.yasson.internal.serializer.SerializerBuilder;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 */
public final class Marshaller implements SerializationContext {

    private static final Logger LOGGER = Logger.getLogger(Marshaller.class.getName());

    private final Type runtimeType;

    private final JsonbContext jsonbContext;

    /**
     * Used to avoid StackOverflowError, when adapted / serialized object
     * contains contains instance of its type inside it or when object has recursive reference.
     */
    private final Set<Object> currentlyProcessedObjects = new HashSet<>();
    
    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext    Current context.
     * @param rootRuntimeType Type of root object.
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        this.jsonbContext = jsonbContext;
        this.runtimeType = rootRuntimeType;
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext Current context.
     */
    public Marshaller(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
        this.runtimeType = null;
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object        object to marshall
     * @param jsonGenerator generator to use
     * @param close         if generator should be closed
     */
    public void marshall(Object object, JsonGenerator jsonGenerator, boolean close) {
        try {
            serializeRoot(object, jsonGenerator);
        } catch (JsonbException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, e.getMessage()), e);
        } finally {
            try {
                if (close) {
                    jsonGenerator.close();
                } else {
                    jsonGenerator.flush();
                }
            } catch (JsonGenerationException jge) {
                LOGGER.severe(jge.getMessage());
            }
        }
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     * Closes the generator on completion.
     *
     * @param object        object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        marshall(object, jsonGenerator, true);
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     * Leaves generator open for further interaction after completion.
     *
     * @param object        object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshallWithoutClose(Object object, JsonGenerator jsonGenerator) {
        marshall(object, jsonGenerator, false);
    }

    @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        generator.writeKey(key);
        serializeRoot(object, generator);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        serializeRoot(object, generator);
    }

    /**
     * Serializes root element.
     *
     * @param <T>       Root type
     * @param root      Root.
     * @param generator JSON generator.
     */
    @SuppressWarnings("unchecked")
    public <T> void serializeRoot(T root, JsonGenerator generator) {
        if (root == null) {
            jsonbContext.getConfigProperties().getNullSerializer().serialize(null, generator, this);
            return;
        }
        final JsonbSerializer<T> rootSerializer = (JsonbSerializer<T>) getRootSerializer(root.getClass());
        if (jsonbContext.getConfigProperties().isStrictIJson()
                && rootSerializer instanceof AbstractValueTypeSerializer) {
            throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
        }
        rootSerializer.serialize(root, generator, this);
    }

    JsonbSerializer<?> getRootSerializer(Class<?> rootClazz) {
        final ContainerSerializerProvider serializerProvider = jsonbContext.getMappingContext().getSerializerProvider(rootClazz);
        if (serializerProvider != null) {
            return serializerProvider.provideSerializer(runtimeType, null);
        }
        SerializerBuilder serializerBuilder = new SerializerBuilder(jsonbContext)
                .withObjectClass(rootClazz)
                .withType(runtimeType);

        ClassModel classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rootClazz);
        serializerBuilder.withCustomization(classModel.getClassCustomization());
        return serializerBuilder.build();
    }
    
    /**
     * Jsonb context.
     *
     * @return jsonb context
     */
    public JsonbContext getJsonbContext() {
        return jsonbContext;
    }
    
    /**
     * Adds currently processed object to the {@link Set}.
     *
     * @param object processed object
     * @return if object was added
     */
    public boolean addProcessedObject(Object object) {
        return this.currentlyProcessedObjects.add(object);
    }

    /**
     * Removes processed object from the {@link Set}.
     *
     * @param object processed object
     * @return if object was removed
     */
    public boolean removeProcessedObject(Object object) {
        return currentlyProcessedObjects.remove(object);
    }
}
