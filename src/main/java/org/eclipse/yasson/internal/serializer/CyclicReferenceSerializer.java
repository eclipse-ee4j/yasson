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

import java.lang.reflect.Type;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;

/**
 * Solution for cyclic references in serialization.
 * This approach helps us to avoid creation of multiple serializers for the same type.
 */
class CyclicReferenceSerializer implements ModelSerializer {

    private final Type type;
    private ModelSerializer delegate;

    CyclicReferenceSerializer(Type type) {
        this.type = type;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        if (delegate == null) {
            delegate = context.getJsonbContext().getSerializationModelCreator().serializerChain(type, true, true);
        }
        delegate.serialize(value, generator, context);
    }
}
