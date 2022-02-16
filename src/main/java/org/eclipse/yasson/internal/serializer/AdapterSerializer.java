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

package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * User defined adapter invoker.
 */
class AdapterSerializer extends AbstractSerializer {

    private final JsonbAdapter<Object, Object> adapter;
    private final AdapterBinding adapterBinding;

    @SuppressWarnings("unchecked")
    AdapterSerializer(AdapterBinding adapterBinding,
                      ModelSerializer delegate) {
        super(delegate);
        this.adapter = (JsonbAdapter<Object, Object>) adapterBinding.getAdapter();
        this.adapterBinding = adapterBinding;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        try {
            delegate.serialize(adapter.adaptToJson(value), generator, context);
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION,
                                                         adapterBinding.getBindingType(),
                                                         adapterBinding.getToType(),
                                                         adapter.getClass()), e);
        }
    }

}
