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

package org.eclipse.yasson.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserialization context implementation.
 */
public class DeserializationContextImpl extends ProcessingContext implements DeserializationContext {
    private final List<Runnable> delayedSetters = new ArrayList<>();
    private JsonParser.Event lastValueEvent;
    private Customization customization = ClassCustomization.empty();
    private Object instance;

    /**
     * Parent instance for marshaller and unmarshaller.
     *
     * @param jsonbContext context of Jsonb
     */
    public DeserializationContextImpl(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    /**
     * Create new instance based on previous context.
     *
     * @param context previous deserialization context
     */
    public DeserializationContextImpl(DeserializationContextImpl context) {
        super(context.getJsonbContext());
        this.lastValueEvent = context.lastValueEvent;
    }

    /**
     * Return instance of currently deserialized type.
     *
     * @return null if instance has not been created yet
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Set currently deserialized type instance.
     *
     * @param instance deserialized type instance
     */
    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /**
     * Return the list of deferred deserializers.
     *
     * @return list of deferred deserializers
     */
    public List<Runnable> getDeferredDeserializers() {
        return delayedSetters;
    }

    /**
     * Return last obtained {@link JsonParser.Event} event.
     *
     * @return last obtained event
     */
    public JsonParser.Event getLastValueEvent() {
        return lastValueEvent;
    }

    /**
     * Set last obtained {@link JsonParser.Event} event.
     *
     * @param lastValueEvent last obtained event
     */
    public void setLastValueEvent(JsonParser.Event lastValueEvent) {
        this.lastValueEvent = lastValueEvent;
    }

    /**
     * Return customization used by currently processed user defined deserializer.
     *
     * @return currently used customization
     */
    public Customization getCustomization() {
        return customization;
    }

    /**
     * Set customization used by currently processed user defined deserializer.
     *
     * @param customization currently used customization
     */
    public void setCustomization(Customization customization) {
        this.customization = customization;
    }

    @Override
    public <T> T deserialize(Class<T> clazz, JsonParser parser) {
        return deserializeItem(clazz, parser);
    }

    @Override
    public <T> T deserialize(Type type, JsonParser parser) {
        return deserializeItem(type, parser);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeItem(Type type, JsonParser parser) {
        try {
            if (lastValueEvent == null) {
                lastValueEvent = parser.next();
                checkState();
            }
            ModelDeserializer<JsonParser> modelDeserializer = getJsonbContext().getChainModelCreator().deserializerChain(type);
            return (T) modelDeserializer.deserialize(parser, this);
        } catch (JsonbException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, e.getMessage()), e);
        }
    }

    private void checkState() {
        if (lastValueEvent == JsonParser.Event.KEY_NAME) {
            throw new JsonbException("JsonParser has incorrect position as the first event: KEY_NAME");
        }
    }

}
