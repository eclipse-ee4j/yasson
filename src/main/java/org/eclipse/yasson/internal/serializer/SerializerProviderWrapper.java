/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
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

/**
 * Wraps serializer and deserializer providers.
 */
public class SerializerProviderWrapper {

    private ISerializerProvider serializerProvider;
    private IDeserializerProvider deserializerProvider;

    /**
     * Creates a new instance.
     *
     * @param serializerProvider   Serializer provider.
     * @param deserializerProvider Deserializer provider.
     */
    public SerializerProviderWrapper(ISerializerProvider serializerProvider, IDeserializerProvider deserializerProvider) {
        this.serializerProvider = serializerProvider;
        this.deserializerProvider = deserializerProvider;
    }

    /**
     * Gets serializer provider.
     *
     * @return Serializer provider.
     */
    public ISerializerProvider getSerializerProvider() {
        return serializerProvider;
    }

    /**
     * Gets deserializer provider.
     *
     * @return Deserializer provider.
     */
    public IDeserializerProvider getDeserializerProvider() {
        return deserializerProvider;
    }
}
