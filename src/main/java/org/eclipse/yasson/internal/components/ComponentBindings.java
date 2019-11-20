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

package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;

/**
 * Wrapper holding singleton instances of user defined components - Adapters, (De)Serializers.
 */
public class ComponentBindings {

    private final Type bindingType;

    private final SerializerBinding serializer;

    private final DeserializerBinding deserializer;

    private final AdapterBinding adapterInfo;

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
     * @param bindingType  Type components are bound to.
     * @param serializer   Serializer.
     * @param deserializer Deserializer.
     * @param adapter      Adapter.
     */
    public ComponentBindings(Type bindingType,
                             SerializerBinding serializer,
                             DeserializerBinding deserializer,
                             AdapterBinding adapter) {
        this.bindingType = bindingType;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.adapterInfo = adapter;
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
    public SerializerBinding getSerializer() {
        return serializer;
    }

    /**
     * Deserializer if any.
     *
     * @return deserializer
     */
    public DeserializerBinding getDeserializer() {
        return deserializer;
    }

    /**
     * Adapter info if any.
     *
     * @return adapterInfo
     */
    public AdapterBinding getAdapterInfo() {
        return adapterInfo;
    }

}
