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

package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;

import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Deserializer of the {@link Integer} type.
 */
class IntegerDeserializer extends AbstractNumberDeserializer<Integer> {

    IntegerDeserializer(TypeDeserializerBuilder builder) {
        super(builder, true);
    }

    @Override
    Integer parseNumberValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    Object deserializeNumberValue(JsonParser value, DeserializationContextImpl context, Type rType) {
        return value.getInt();
    }
}
