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

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

/**
 * Regular class with an explicit {@link JsonbCreator} constructor that exercises every
 * meaningful combination of {@link JsonbTransient} placement (field, getter, setter).
 *
 * <p>Expected deserialization semantics:
 * <ul>
 *   <li>{@code plainProperty}          – not transient; always populated.</li>
 *   <li>{@code fieldTransient}         – {@code @JsonbTransient} on field → both directions
 *                                        suppressed; constructor receives {@code null}.</li>
 *   <li>{@code getterTransient}        – {@code @JsonbTransient} on getter only → serialization
 *                                        suppressed, deserialization still populates the field.</li>
 *   <li>{@code setterTransient}        – {@code @JsonbTransient} on setter only → deserialization
 *                                        suppressed; constructor receives {@code null}.</li>
 *   <li>{@code fieldAndGetterTransient}     – field + getter → both directions suppressed.</li>
 *   <li>{@code fieldAndSetterTransient}     – field + setter → both directions suppressed.</li>
 *   <li>{@code getterAndSetterTransient}    – getter + setter → both directions suppressed.</li>
 *   <li>{@code allTransient}               – field + getter + setter → both directions suppressed.</li>
 * </ul>
 */
public class JsonbTransientWithCreator {

    /** No annotation — always visible in both directions. */
    public String plainProperty;

    /** {@code @JsonbTransient} on the backing field. */
    @JsonbTransient
    public String fieldTransient;

    /** {@code @JsonbTransient} on the getter only. */
    public String getterTransient;

    /** {@code @JsonbTransient} on the setter only. */
    public String setterTransient;

    /** {@code @JsonbTransient} on both field and getter. */
    @JsonbTransient
    public String fieldAndGetterTransient;

    /** {@code @JsonbTransient} on both field and setter. */
    @JsonbTransient
    public String fieldAndSetterTransient;

    /** {@code @JsonbTransient} on both getter and setter. */
    public String getterAndSetterTransient;

    /** {@code @JsonbTransient} on all three: field, getter, and setter. */
    @JsonbTransient
    public String allTransient;

    @JsonbCreator
    public JsonbTransientWithCreator(@JsonbProperty("plainProperty")         String plainProperty,
                                     @JsonbProperty("fieldTransient")         String fieldTransient,
                                     @JsonbProperty("getterTransient")        String getterTransient,
                                     @JsonbProperty("setterTransient")        String setterTransient,
                                     @JsonbProperty("fieldAndGetterTransient") String fieldAndGetterTransient,
                                     @JsonbProperty("fieldAndSetterTransient") String fieldAndSetterTransient,
                                     @JsonbProperty("getterAndSetterTransient") String getterAndSetterTransient,
                                     @JsonbProperty("allTransient")           String allTransient) {
        this.plainProperty          = plainProperty;
        this.fieldTransient         = fieldTransient;
        this.getterTransient        = getterTransient;
        this.setterTransient        = setterTransient;
        this.fieldAndGetterTransient = fieldAndGetterTransient;
        this.fieldAndSetterTransient = fieldAndSetterTransient;
        this.getterAndSetterTransient = getterAndSetterTransient;
        this.allTransient           = allTransient;
    }

    @JsonbTransient
    public String getGetterTransient() { return getterTransient; }

    @JsonbTransient
    public void setSetterTransient(String setterTransient) { this.setterTransient = setterTransient; }

    @JsonbTransient
    public String getFieldAndGetterTransient() { return fieldAndGetterTransient; }

    @JsonbTransient
    public void setFieldAndSetterTransient(String fieldAndSetterTransient) { this.fieldAndSetterTransient = fieldAndSetterTransient; }

    @JsonbTransient
    public String getGetterAndSetterTransient() { return getterAndSetterTransient; }

    @JsonbTransient
    public void setGetterAndSetterTransient(String getterAndSetterTransient) { this.getterAndSetterTransient = getterAndSetterTransient; }

    @JsonbTransient
    public String getAllTransient() { return allTransient; }

    @JsonbTransient
    public void setAllTransient(String allTransient) { this.allTransient = allTransient; }
}
