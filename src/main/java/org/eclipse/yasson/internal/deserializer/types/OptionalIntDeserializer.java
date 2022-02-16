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

import java.util.OptionalInt;

import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

/**
 * Deserializer of the {@link OptionalInt} type.
 */
class OptionalIntDeserializer implements ModelDeserializer<JsonParser> {

    private final ModelDeserializer<JsonParser> extractor;
    private final ModelDeserializer<Object> delegate;

    OptionalIntDeserializer(ModelDeserializer<JsonParser> extractor, ModelDeserializer<Object> delegate) {
        this.extractor = extractor;
        this.delegate = delegate;
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        if (context.getLastValueEvent() == JsonParser.Event.VALUE_NULL) {
            return delegate.deserialize(OptionalInt.empty(), context);
        }
        OptionalInt optional = OptionalInt.of((Integer) extractor.deserialize(value, context));
        return delegate.deserialize(optional, context);
    }
}
