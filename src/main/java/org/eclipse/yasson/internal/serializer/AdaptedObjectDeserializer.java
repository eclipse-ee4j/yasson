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

import java.lang.reflect.Type;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Decorator for an item which builds adapted type instance by a {@link JsonbAdapter}.
 * After adapted item is finished building its instance is converted to field type object by calling components.
 *
 * @param <A> adapted type, type to deserialize JSON into
 * @param <T> required type, typically type of the field, which is adapted to another type
 */
public class AdaptedObjectDeserializer<A, T> implements CurrentItem<T>, JsonbDeserializer<T> {

    private JsonbDeserializer<A> adaptedTypeDeserializer;

    private final AdapterBinding adapterInfo;

    private final AbstractContainerDeserializer<?> wrapperItem;

    /**
     * Creates decoration instance wrapping real adapted object item.
     *
     * @param adapterInfo components type info
     * @param wrapperItem wrapper item to get instance from
     */
    public AdaptedObjectDeserializer(AdapterBinding adapterInfo, AbstractContainerDeserializer<?> wrapperItem) {
        this.adapterInfo = adapterInfo;
        this.wrapperItem = wrapperItem;
    }

    @Override
    public ClassModel getClassModel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapperItem;
    }

    @Override
    public Type getRuntimeType() {
        if (adaptedTypeDeserializer instanceof AbstractContainerDeserializer) {
            return ((AbstractContainerDeserializer) adaptedTypeDeserializer).getRuntimeType();
        }
        throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR,
                                                     "Deserialization propagation is not allowed for:" + adaptedTypeDeserializer));
    }

    /**
     * Sets adapted item.
     *
     * @param adaptedTypeDeserializer Adapted item to set.
     */
    public void setAdaptedTypeDeserializer(JsonbDeserializer<A> adaptedTypeDeserializer) {
        this.adaptedTypeDeserializer = adaptedTypeDeserializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser parser, DeserializationContext context, Type rtType) {
        try {
            final A result = adaptedTypeDeserializer.deserialize(parser, context, rtType);
            final T adapted = ((JsonbAdapter<T, A>) adapterInfo.getAdapter()).adaptFromJson(result);
            return adapted;
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION,
                                                         adapterInfo.getBindingType(),
                                                         adapterInfo.getToType(),
                                                         adapterInfo.getAdapter().getClass()), e);
        }
    }
}
