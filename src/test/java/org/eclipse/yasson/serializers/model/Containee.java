/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Objects;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;

@JsonbTypeDeserializer(ContaineeDeserializer.class)
@JsonbTypeSerializer(ContaineeSerializer.class)
public class Containee {
    final String key;
    final String value;

    public Containee(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Containee containee = (Containee) o;
        return Objects.equals(key, containee.key) && Objects.equals(value, containee.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
