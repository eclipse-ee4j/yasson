/*
 * Copyright (c) 2022, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.JsonbException;

class Issue456Test {

    private Issue456Test() {
    }

    @Test
    public void dontInvokeToString() {
        try {
            defaultJsonb.toJson(new Example());
            fail("JsonbException is expected");
        } catch (JsonbException e) {
            // Expected
        }
    }

    public static class Example {

        protected Example() {
        }

        public String getProperty() {
            throw new RuntimeException("some error");
        }

        @Override
        public String toString() {
            return defaultJsonb.toJson(this);
        }
    }
}
