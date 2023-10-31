/*
 * Copyright (c) 2018, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.cdi;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.json.bind.adapter.JsonbAdapter;
import java.util.Set;

/**
 * For JNDI Bean Manager resolution testing purposes.
 */
public class MockInjectionTarget implements InjectionTarget<JsonbAdapter<String, Integer>> {
    @Override
    public void inject(JsonbAdapter<String, Integer> instance, CreationalContext<JsonbAdapter<String, Integer>> ctx) {

    }

    @Override
    public void postConstruct(JsonbAdapter<String, Integer> instance) {

    }

    @Override
    public void preDestroy(JsonbAdapter<String, Integer> instance) {

    }

    @Override
    public JsonbAdapter<String, Integer> produce(CreationalContext<JsonbAdapter<String, Integer>> ctx) {
        // for the test we only need this adapter; so it's OK to always return just this one
        return new NonCdiAdapter();
    }

    @Override
    public void dispose(JsonbAdapter<String, Integer> instance) {

    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return null;
    }
}
