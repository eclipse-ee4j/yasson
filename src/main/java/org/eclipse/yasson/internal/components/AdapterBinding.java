/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.adapter.JsonbAdapter;

/**
 * Wrapper for JsonbAdapter generic information and an components itself.
 */
public class AdapterBinding extends AbstractComponentBinding {

    private final Type toType;

    private final JsonbAdapter<?, ?> adapter;

    /**
     * Adapter info with type to "adapt from", type to "adapt to" and an components itself.
     *
     * @param fromType from not null
     * @param toType   to not null
     * @param adapter  components not null
     */
    public AdapterBinding(Type fromType, Type toType, JsonbAdapter<?, ?> adapter) {
        super(fromType);
        Objects.requireNonNull(toType);
        Objects.requireNonNull(adapter);
        this.toType = toType;
        this.adapter = adapter;
    }

    /**
     * Represents a type to which to adapt into.
     *
     * During marshalling object property is adapted to this type and result is marshalled.
     * During unmarshalling object is unmarshalled into this type first, than converted to field type and set.
     *
     * @return Type from which to adapt
     */
    public Type getToType() {
        return toType;
    }

    /**
     * Get actual components to adapt object value.
     *
     * @return components
     */
    public JsonbAdapter<?, ?> getAdapter() {
        return adapter;
    }

    @Override
    public Class<?> getComponentClass() {
        return adapter.getClass();
    }
}
