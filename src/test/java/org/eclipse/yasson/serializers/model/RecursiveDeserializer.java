/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers.model;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Causes {@link StackOverflowError} if recursive calls of user components are not checked by runtime.
 */
public class RecursiveDeserializer implements JsonbDeserializer<Box> {
    @Override
    public Box deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        return deserializationContext.deserialize(type, jsonParser);
    }
}
