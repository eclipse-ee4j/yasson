/*
 * Copyright (c) 2026 Eclipse and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * This class is used to test the behavior of @JsonbCreator when there are incomplete set of parameters in the constructor.
 * The constructor parameter "declaredField" is annotated with @JsonbProperty("declaredField"), 
 * which should map the JSON property "declaredField" to the constructor parameter, 
 * and then assign it to the field "declaredField".
 * 
 * The field "declaredField" should be set to the value of "declaredField" from the JSON, converted to uppercase.
 * The field "notDeclaredField" should be set via the normal deserialization pathway since it is not provided in the constructor.
 * The field "notDeclaredSetter" should be set via the normal deserialization pathway since it is not provided in the constructor and has a setter method.
 * 
 * CreatorIncompleteParameters
 */
public class CreatorIncompleteParameters {
    public String declaredField;
    public String notDeclaredField;
    private String notDeclaredSetter;

    @JsonbCreator
    public CreatorIncompleteParameters(@JsonbProperty("declaredField") String declaredField) {
        this.declaredField = declaredField.toUpperCase();
    }

    public void setNotDeclaredSetter(String notDeclaredSetter) {
        this.notDeclaredSetter = notDeclaredSetter;
    }

    public String getNotDeclaredSetter() {
        return notDeclaredSetter;
    }
}
