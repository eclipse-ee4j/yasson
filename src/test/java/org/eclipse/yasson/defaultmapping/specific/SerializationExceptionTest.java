/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.defaultmapping.specific;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

/**
 * Tests covering error-handling behaviour during serialization: verifies that
 * exceptions thrown by getter methods are wrapped and reported as {@link JsonbException}
 * rather than triggering any fallback path such as {@code toString()}.
 */
public class SerializationExceptionTest {

    /**
     * Tests that a {@link RuntimeException} thrown by a getter during serialization is
     * propagated to the caller as a {@link JsonbException}, and that the serializer does
     * <em>not</em> fall back to invoking {@code toString()} on the object under serialization.
     *
     * <p>A previous bug caused the serializer to silently call {@code toString()} when a
     * getter threw, which could produce incorrect output or trigger infinite recursion when
     * {@code toString()} itself called {@code toJson()}.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/456">Issue #456</a>
     */
    @Test
    public void getterExceptionPropagatesAsJsonbException() {
        assertThrows(JsonbException.class, () -> JsonbBuilder.create().toJson(new BrokenGetterPojo()));
    }

    /**
     * A POJO whose getter throws a {@link RuntimeException}.
     *
     * <p>Its {@code toString()} intentionally calls {@code toJson(this)} so that, if the
     * serializer were to fall back to {@code toString()} on error, an infinite loop or
     * secondary exception would be triggered — making the mis-behaviour observable.
     */
    static class BrokenGetterPojo {

        public String getProperty() {
            throw new RuntimeException("simulated getter failure");
        }

        @Override
        public String toString() {
            return JsonbBuilder.create().toJson(this);
        }
    }
}
