/*
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

package org.eclipse.yasson.customization.transients.models;

import jakarta.json.bind.annotation.JsonbTransient;

/**
 * Record variant of {@link JsonbTransientValue} for testing {@link JsonbTransient} on record
 * components and record accessor methods.
 *
 * <ul>
 *   <li>{@code plainProperty}          – not transient, always visible</li>
 *   <li>{@code componentTransient}     – {@code @JsonbTransient} on the record component</li>
 *   <li>{@code accessorTransient}      – {@code @JsonbTransient} on the accessor method</li>
 *   <li>{@code componentAndAccessorTransient} – {@code @JsonbTransient} on both</li>
 * </ul>
 */
public record JsonbTransientRecord(
        String plainProperty,

        @JsonbTransient
        String componentTransient,

        String accessorTransient,

        @JsonbTransient
        String componentAndAccessorTransient
) {

    /** Accessor annotated with {@link JsonbTransient} to suppress serialization. */
    @JsonbTransient
    @Override
    public String accessorTransient() {
        return accessorTransient;
    }

    /** Accessor annotated with {@link JsonbTransient} alongside the component annotation. */
    @JsonbTransient
    @Override
    public String componentAndAccessorTransient() {
        return componentAndAccessorTransient;
    }

    @JsonbTransient
    public String virtualAttributeTransient() {
        return "a string";
    }
}
