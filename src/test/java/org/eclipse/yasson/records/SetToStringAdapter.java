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

package org.eclipse.yasson.records;

import jakarta.json.bind.adapter.JsonbAdapter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter used to reproduce issue #607: {@code @JsonbTypeAdapter} on a record component
 * must be recognised and invoked for both serialisation and deserialisation.
 * <p>
 * Converts a {@code Set<String>} to/from a comma-separated {@code String}.
 */
public class SetToStringAdapter implements JsonbAdapter<Set<String>, String> {

    @Override
    public String adaptToJson(Set<String> obj) {
        return obj.stream().sorted().collect(Collectors.joining(","));
    }

    @Override
    public Set<String> adaptFromJson(String obj) {
        return new LinkedHashSet<>(Arrays.asList(obj.split(",")));
    }
}
