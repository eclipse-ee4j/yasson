/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import org.glassfish.json.JsonProviderImpl;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.*;

public class JsonBindingBuilderTest {

    @Test
    public void testMultipleCallsToBuildWithoutChangesReturnTheSameInstance() {
        JsonBindingBuilder builder = new JsonBindingBuilder();

        Jsonb jsonb1 = builder.build();
        Jsonb jsonb2 = builder.build();

        assertSame(jsonb1, jsonb2);
    }

    @Test
    public void testMultipleCallsToBuildWithEqualConfigReturnTheSameInstance() {
        JsonBindingBuilder builder = new JsonBindingBuilder();
        JsonbConfig config = new JsonbConfig();

        Jsonb jsonb1 = builder.build();
        builder.withConfig(config);
        Jsonb jsonb2 = builder.build();

        assertSame(jsonb1, jsonb2);
    }


    @Test
    public void testMultipleCallsToBuildWithChangedConfigReturnNotTheSameInstance() {
        JsonBindingBuilder builder = new JsonBindingBuilder();
        JsonbConfig config = new JsonbConfig();
        builder.withConfig(config);

        Jsonb jsonb1 = builder.build();
        config.withStrictIJSON(true);
        Jsonb jsonb2 = builder.build();

        assertNotSame(jsonb1, jsonb2);
    }


    @Test
    public void testMultipleCallsToBuildWithChangedProviderReturnNotTheSameInstance() {
        JsonBindingBuilder builder = new JsonBindingBuilder();

        Jsonb jsonb1 = builder.build();
        builder.withProvider(new JsonProviderImpl());
        Jsonb jsonb2 = builder.build();

        assertNotSame(jsonb1, jsonb2);
    }
}
