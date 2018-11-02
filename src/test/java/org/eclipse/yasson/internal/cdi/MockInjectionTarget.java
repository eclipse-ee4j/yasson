/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.internal.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.json.bind.adapter.JsonbAdapter;
import java.util.Set;

/**
 * For JNDI Bean Manager resolution testing purposes.
 */
public class MockInjectionTarget implements InjectionTarget<JsonbAdapter> {
    @Override
    public void inject(JsonbAdapter instance, CreationalContext<JsonbAdapter> ctx) {

    }

    @Override
    public void postConstruct(JsonbAdapter instance) {

    }

    @Override
    public void preDestroy(JsonbAdapter instance) {

    }

    @Override
    public JsonbAdapter produce(CreationalContext<JsonbAdapter> ctx) {
        return new NonCdiAdapter();
    }

    @Override
    public void dispose(JsonbAdapter instance) {

    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return null;
    }
}
