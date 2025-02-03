/*
 * Copyright (c) 2025 Red Hat, Inc. and/or its affiliates.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.Objects;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class StaticCreatorContainer<T> {
    private final T value;

    private StaticCreatorContainer(T value) {
        this.value = value;
    }

    @JsonbCreator
    public static <T> StaticCreatorContainer<T> create(@JsonbProperty("value") final T value) {
        return new StaticCreatorContainer<>(value);
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StaticCreatorContainer[value=" + value + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StaticCreatorContainer)) {
            return false;
        }
        final StaticCreatorContainer<?> other = (StaticCreatorContainer<?>) obj;
        return Objects.equals(value, other.value);
    }

}
