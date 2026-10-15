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

package org.eclipse.yasson.defaultmapping.specific;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.JsonbException;

/**
 * Tests covering error-handling behaviour during deserialization: verifies that
 * exceptions thrown by setter methods are reported as the cause of a {@link JsonbException},
 * without calling {@code toString()} on the object under deserialization.
 *
 * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/456">Issue #456</a>
 */
public class DeserializationExceptionTest {

    private static final String JSON = "{\"iban\":\"XX\"}";

    /**
     * Tests that a {@link RuntimeException} thrown by a setter during deserialization is the
     * root cause of the {@link JsonbException}.
     *
     * <p>The object is only partially deserialized at that point. Calling its {@code toString()}
     * to build the error message used to replace the setter's exception with any exception
     * thrown by {@code toString()}.
     */
    @Test
    public void setterExceptionIsRootCause() {
        List<Throwable> causes = causesOf(assertThrows(JsonbException.class,
                                                       () -> defaultJsonb.fromJson(JSON, BrokenSetterPojo.class)));

        Throwable rootCause = causes.get(causes.size() - 1);
        assertEquals(IllegalArgumentException.class, rootCause.getClass());
        assertEquals("simulated setter failure", rootCause.getMessage());
    }

    /**
     * Tests that the error message of a failing setter names the class of the object.
     */
    @Test
    public void setterErrorMessageNamesClass() {
        List<Throwable> causes = causesOf(assertThrows(JsonbException.class,
                                                       () -> defaultJsonb.fromJson(JSON, BrokenSetterPojo.class)));

        String className = BrokenSetterPojo.class.getName();
        assertTrue(causes.stream().anyMatch(cause -> cause.getMessage() != null
                           && cause.getMessage().contains(className)),
                   "No exception whose message contains class name: " + className);
    }

    private static List<Throwable> causesOf(Throwable throwable) {
        List<Throwable> causes = new ArrayList<>();
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            causes.add(cause);
        }
        return causes;
    }

    /**
     * A POJO whose setter throws a {@link RuntimeException}, and whose {@code toString()} must not
     * be called while it is deserialized.
     */
    public static class BrokenSetterPojo {

        public void setIban(String iban) {
            throw new IllegalArgumentException("simulated setter failure");
        }

        @Override
        public String toString() {
            throw new IllegalStateException("toString() must not be called during deserialization");
        }
    }
}
