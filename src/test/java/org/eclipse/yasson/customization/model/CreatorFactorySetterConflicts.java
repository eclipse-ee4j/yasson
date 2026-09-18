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
 * Tests {@code @JsonbCreator} on a factory method when a factory method parameter name (via
 * {@code @JsonbProperty}) conflicts with a public setter's property name.
 *
 * <p>The factory method parameter is named {@code "value"} via {@code @JsonbProperty}, which matches
 * the bean property exposed by {@code setValue}/{@code getValue}. The factory method routes the JSON
 * value to the separate field {@code creatorValue} (uppercased), so the setter must never be called
 * for that key.
 *
 * <p>Expected outcomes after deserializing {@code {"value":"hello"}}:
 * <ul>
 *   <li>{@code getValue()} – {@code null} (the creator claimed the name; the setter is never invoked)</li>
 *   <li>{@code creatorValue} – {@code "HELLO"} (assigned inside the factory method)</li>
 * </ul>
 */
public class CreatorFactorySetterConflicts {

    private String value;
    public String creatorValue;

    private CreatorFactorySetterConflicts() {
    }

    @JsonbCreator
    public static CreatorFactorySetterConflicts create(@JsonbProperty("value") String value) {
        CreatorFactorySetterConflicts instance = new CreatorFactorySetterConflicts();
        instance.creatorValue = value.toUpperCase(Locale.ROOT);
        return instance;
    }

    public String getValue() {
        return value;
    }

    /** Setter whose bean-property name {@code "value"} conflicts with the factory method parameter. */
    public void setValue(String value) {
        this.value = value;
    }
}
