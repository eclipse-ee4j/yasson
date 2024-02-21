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
import java.util.Objects;

import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * Wrapper for JsonbAdapter generic information and an components itself.
 *
 * @param <Original> The type for the @{@link JsonbAdapter} that JSONB doesn't know how to handle.
 * @param <Adapted> The type for the @{@link JsonbAdapter} that JSONB knows how to handle out of the box.
 */
public class AdapterBinding<Original, Adapted> extends AbstractComponentBinding<JsonbAdapter<Original, Adapted>> {

    private final Type toType;

    /**
     * Adapter info with type to "adapt from", type to "adapt to" and an components itself.
     *
     * @param fromType from not null
     * @param toType   to not null
     * @param adapter  components not null
     */
    public AdapterBinding(Type fromType, Type toType, JsonbAdapter<Original, Adapted> adapter) {
        super(fromType, adapter);
        Objects.requireNonNull(toType);
        this.toType = toType;
    }

    /**
     * Represents a type to which to adapt into.
     * <p>
     * During marshalling object property is adapted to this type and result is marshalled.
     * During unmarshalling object is unmarshalled into this type first, than converted to field type and set.
     *
     * @return Type from which to adapt
     */
    public Type getToType() {
        return toType;
    }
}
