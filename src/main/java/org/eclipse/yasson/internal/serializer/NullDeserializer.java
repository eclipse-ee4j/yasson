/*
 * Copyright (c) 2019, 2020 Payara Services and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

/**
 * Deserializer of null value.
 */
public enum NullDeserializer implements JsonbDeserializer<Object> {
    /**
     * Singleton of null deserializer.
     */
    INSTANCE;

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        return null;
    }
}
