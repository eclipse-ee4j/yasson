/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Common type for all supported type serializers.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractValueTypeSerializer<T> implements JsonbSerializer<T> {

    protected final JsonBindingModel model;

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public AbstractValueTypeSerializer(JsonBindingModel model) {
        this.model = model;
    }

    /**
     * Serializes an object to JSON.
     *
     * @param obj Object to serialize.
     * @param generator JSON generator to use.
     * @param ctx JSON-B mapper context.
     */
    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        Marshaller marshaller = (Marshaller) ctx;
        serialize(obj, generator, marshaller);
    }

    protected abstract void serialize(T obj, JsonGenerator generator, Marshaller marshaller);
}
