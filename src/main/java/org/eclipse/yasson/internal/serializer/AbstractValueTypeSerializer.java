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

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Common type for all supported type serializers.
 *
 * @param <T> value type
 */
public abstract class AbstractValueTypeSerializer<T> implements JsonbSerializer<T> {

    private final Customization customization;

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public AbstractValueTypeSerializer(Customization customization) {
        this.customization = customization;
    }

    /**
     * Serializes an object to JSON.
     *
     * @param obj       Object to serialize.
     * @param generator JSON generator to use.
     * @param ctx       JSON-B mapper context.
     */
    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        Marshaller marshaller = (Marshaller) ctx;
        serialize(obj, generator, marshaller);
    }

    /**
     * Serializes an object to JSON.
     *
     * @param obj        Object to serialize.
     * @param generator  JSON generator to use.
     * @param marshaller Marshaller.
     */
    protected abstract void serialize(T obj, JsonGenerator generator, Marshaller marshaller);

    /**
     * Returns value type customization.
     *
     * @return customization
     */
    public Customization getCustomization() {
        return customization;
    }
}
