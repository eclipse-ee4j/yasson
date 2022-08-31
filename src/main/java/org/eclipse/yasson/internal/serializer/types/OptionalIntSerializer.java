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

package org.eclipse.yasson.internal.serializer.types;

import java.util.OptionalInt;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

/**
 * Serializer of the {@link OptionalInt} type.
 */
class OptionalIntSerializer implements ModelSerializer {

    private final ModelSerializer typeSerializer;

    OptionalIntSerializer(ModelSerializer typeSerializer) {
        this.typeSerializer = typeSerializer;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        OptionalInt optionalInt = (OptionalInt) value;
        if (optionalInt != null && optionalInt.isPresent()) {
            typeSerializer.serialize(optionalInt.getAsInt(), generator, context);
        } else {
            typeSerializer.serialize(null, generator, context);
        }
    }
}
