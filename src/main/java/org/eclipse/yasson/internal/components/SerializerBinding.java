/*
 * Copyright (c) 2016, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;

import jakarta.json.bind.serializer.JsonbSerializer;

/**
 * Binding for user Serializer component.
 *
 * @param <T> type of jsonb serializer
 */
public class SerializerBinding<T> extends AbstractComponentBinding<JsonbSerializer<T>> {

    /**
     * Creates a new instance.
     *
     * @param bindingType     Generic type argument of serializer. Not null.
     * @param jsonbSerializer Serializer. Can be null.
     */
    public SerializerBinding(Type bindingType, JsonbSerializer<T> jsonbSerializer) {
        super(bindingType, jsonbSerializer);
    }
}
