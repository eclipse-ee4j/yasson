/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;
import java.util.Objects;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;

/**
 * Wrapper holding singleton instances of user defined components - Adapters, (De)Serializers.
 *
 * @param <Original> The type for the @{@link JsonbAdapter} that JSONB doesn't know how to handle.
 *                  Also type for the @{@link JsonbSerializer} to serialize and for the @{@link JsonbDeserializer} to deserialize.
 * @param <Adapted> The type for @{@link JsonbAdapter} that JSONB knows how to handle out of the box.
 */
public class ComponentBindings<Original, Adapted> {

    private final Type bindingType;

    private final SerializerBinding<Original> serializerBinding;

    private final DeserializerBinding<Original> deserializerBinding;

    private final AdapterBinding<Original, Adapted> adapterBinding;

    /**
     * Construct empty bindings for a given type.
     *
     * @param bindingType type components are bound to
     */
    public ComponentBindings(Type bindingType) {
        this(bindingType, null, null, null);
    }

    /**
     * Creates an instance and populates it with bindings for a given type.
     *
     * @param bindingType         Type components are bound to.
     * @param serializerBinding   Serializer.
     * @param deserializerBinding Deserializer.
     * @param adapterBinding      Adapter.
     */
    private ComponentBindings(Type bindingType,
                             SerializerBinding<Original> serializerBinding,
                             DeserializerBinding<Original> deserializerBinding,
                             AdapterBinding<Original, Adapted> adapterBinding) {
        Objects.requireNonNull(bindingType);
        this.bindingType = bindingType;
        this.serializerBinding = serializerBinding;
        this.deserializerBinding = deserializerBinding;
        this.adapterBinding = adapterBinding;
    }

    /**
     * Creates a copy of the given bindings and new serializer.
     *
     * @param bindings           Deserializer and adapter will be copied from this instance.
     * @param serializerBinding  New serializer. The bound type for the copy will be also taken from this serializer.
     */
    public ComponentBindings(ComponentBindings<Original, Adapted> bindings,
                             SerializerBinding<Original> serializerBinding) {
        this(Objects.requireNonNull(serializerBinding).getBindingType(), serializerBinding, bindings.deserializerBinding, bindings.adapterBinding);
    }

    /**
     * Creates a copy of the given bindings and new deserializer.
     *
     * @param bindings             Serializer and adapter will be copied from this instance.
     * @param deserializerBinding  New deserializer. The bound type for the copy will be also taken from this deserializer.
     */
    public ComponentBindings(ComponentBindings<Original, Adapted> bindings,
                             DeserializerBinding<Original> deserializerBinding) {
        this(Objects.requireNonNull(deserializerBinding).getBindingType(), bindings.serializerBinding, deserializerBinding, bindings.adapterBinding);
    }

    /**
     * Creates a copy of the given bindings and new adapter.
     *
     * @param bindings        Serializer and serializer will be copied from this instance.
     * @param adapterBinding  New adapter. The bound type for the copy will be also taken from this adapter.
     */
    public ComponentBindings(ComponentBindings<Original, Adapted> bindings,
                             AdapterBinding<Original, Adapted> adapterBinding) {
        this(Objects.requireNonNull(adapterBinding).getBindingType(), bindings.serializerBinding, bindings.deserializerBinding, adapterBinding);
    }

    /**
     * Type to which components are bound.
     *
     * @return Bound type.
     */
    public Type getBindingType() {
        return bindingType;
    }

    /**
     * Serializer if any.
     *
     * @return serializer
     */
    public SerializerBinding<Original> getSerializerBinding() {
        return serializerBinding;
    }

    /**
     * Deserializer if any.
     *
     * @return deserializer
     */
    public DeserializerBinding<Original> getDeserializerBinding() {
        return deserializerBinding;
    }

    /**
     * Adapter info if any.
     *
     * @return adapterInfo
     */
    public AdapterBinding<Original, Adapted> getAdapterBinding() {
        return adapterBinding;
    }

}
