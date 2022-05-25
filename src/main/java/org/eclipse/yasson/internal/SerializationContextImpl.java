/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 */
public class SerializationContextImpl extends ProcessingContext implements SerializationContext {

    private static final Logger LOGGER = Logger.getLogger(SerializationContextImpl.class.getName());

    /**
     * Used to avoid StackOverflowError, when adapted / serialized object
     * contains instance of its type inside it or when object has recursive reference.
     */
    private final Set<Object> currentlyProcessedObjects = new HashSet<>();

    private final Type runtimeType;
    private String key = null;
    private boolean containerWithNulls = true;
    private boolean root = true;

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext    Current context.
     * @param rootRuntimeType Type of root object.
     */
    public SerializationContextImpl(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        this.runtimeType = rootRuntimeType;
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext Current context.
     */
    public SerializationContextImpl(JsonbContext jsonbContext) {
        this(jsonbContext, null);
    }

    /**
     * Set new current property key name.
     *
     * @param key key name
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Current property key name.
     *
     * @return current property key name
     */
    public String getKey() {
        return key;
    }

    /**
     * Serialized value is a root value.
     *
     * @return is root value
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Set whether serialized value is root value.
     *
     * @param root is root value
     */
    public void setRoot(boolean root) {
        this.root = root;
    }

    /**
     * Value from this property is only used in {@link org.eclipse.yasson.internal.serializer.NullSerializer}.
     * It should not be used anywhere else.
     *
     * @return if container supports nulls
     */
    public boolean isContainerWithNulls() {
        return containerWithNulls;
    }

    /**
     * Set if container supports null values.
     *
     * @param writeNulls should write nulls in container
     */
    public void setContainerWithNulls(boolean writeNulls) {
        this.containerWithNulls = writeNulls;
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
            serializeObject(object, jsonGenerator);
        } catch (JsonbException e) {
            throw e;
        } catch (RuntimeException e) {
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
        setKey(key);
        serializeObject(object, generator);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        serializeObject(object, generator);
    }

    /**
     * Serializes root element.
     *
     * @param <T>       Root type
     * @param root      Root.
     * @param generator JSON generator.
     */
    public <T> void serializeObject(T root, JsonGenerator generator) {
        Type type = determineSerializationType(root);
        final ModelSerializer rootSerializer = getRootSerializer(type);
        rootSerializer.serialize(root, generator, this);
    }

    private <T> Type determineSerializationType(T root) {
        if (isRoot() && runtimeType != null) {
            return runtimeType;
        }
        return root == null ? Object.class : root.getClass();
    }

    public ModelSerializer getRootSerializer(Type type) {
        return getJsonbContext().getSerializationModelCreator().serializerChain(type, true, true);
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
