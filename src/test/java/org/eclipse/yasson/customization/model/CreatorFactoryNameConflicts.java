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

import java.util.Locale;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * This class is used to test the behavior of {@code @JsonbCreator} on a factory method when a factory
 * method parameter name (via {@code @JsonbProperty}) conflicts with the name of a public field.
 *
 * <p>The factory method parameter is named {@code "value"} via {@code @JsonbProperty}, which matches
 * the public field {@code value}. The factory method routes that JSON value to the field
 * {@code creatorValue} (converted to uppercase), leaving the public field {@code value} null.
 *
 * <p>Expected outcomes after deserializing {@code {"value":"hello"}}:
 * <ul>
 *   <li>{@code value} – {@code null} (the creator claimed the name; it is never set via field injection)</li>
 *   <li>{@code creatorValue} – {@code "HELLO"} (assigned inside the factory method)</li>
 * </ul>
 */
public class CreatorFactoryNameConflicts {

    /** Public field whose JSON name conflicts with the factory method parameter. */
    public String value;

    /** Receives the transformed value from the factory method. */
    public String creatorValue;

    private CreatorFactoryNameConflicts() {
    }

    @JsonbCreator
    public static CreatorFactoryNameConflicts create(@JsonbProperty("value") String value) {
        CreatorFactoryNameConflicts instance = new CreatorFactoryNameConflicts();
        instance.creatorValue = value.toUpperCase(Locale.ROOT);
        return instance;
    }
}
