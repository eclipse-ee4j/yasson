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

package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * User defined type adapter executor.
 */
class AdapterDeserializer implements ModelDeserializer<Object> {

    private final JsonbAdapter<Object, Object> adapter;
    private final AdapterBinding adapterBinding;
    private final ModelDeserializer<Object> delegate;

    @SuppressWarnings("unchecked")
    AdapterDeserializer(AdapterBinding adapterBinding,
                        ModelDeserializer<Object> delegate) {
        this.adapterBinding = adapterBinding;
        this.adapter = (JsonbAdapter<Object, Object>) adapterBinding.getAdapter();
        this.delegate = delegate;
    }

    @Override
    public Object deserialize(Object value, DeserializationContextImpl context) {
        try {
            return delegate.deserialize(adapter.adaptFromJson(value), context);
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION,
                                                         adapterBinding.getBindingType(),
                                                         adapterBinding.getToType(),
                                                         adapterBinding.getAdapter().getClass()), e);
        }
    }

}
