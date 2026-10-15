/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.lang.invoke.MethodHandle;
import java.util.Objects;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Value setter. Invokes created {@link MethodHandle} to set deserialized value to the instance.
 */
class ValueSetterDeserializer implements ModelDeserializer<Object> {

    private final MethodHandle valueSetter;

    ValueSetterDeserializer(MethodHandle valueSetter) {
        this.valueSetter = Objects.requireNonNull(valueSetter);
    }

    @Override
    public Object deserialize(Object value, DeserializationContextImpl context) {
        Object instance = context.getInstance();
        try {
            valueSetter.invoke(instance, value);
            return value;
        } catch (Throwable e) {
            // Do not call toString() of the instance, which is only partially deserialized
            String typeName = instance == null ? "null" : instance.getClass().getName();
            throw new JsonbException("Error setting value on: " + typeName, e);
        }
    }

}
