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

import java.lang.invoke.MethodHandle;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;

/**
 * Extractor of the serialized value from the instance.
 */
class ValueGetterSerializer implements ModelSerializer {

    private final MethodHandle valueGetter;
    private final ModelSerializer delegate;

    ValueGetterSerializer(MethodHandle valueGetter, ModelSerializer delegate) {
        this.valueGetter = valueGetter;
        this.delegate = delegate;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        Object object;
        try {
            object = valueGetter.invoke(value);
        } catch (Throwable e) {
            throw new JsonbException("Error getting value on: " + value.getClass().getName(), e);
        }
        delegate.serialize(object, generator, context);
    }
}
