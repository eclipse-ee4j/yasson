/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.adapter.JsonbAdapter;

/**
 * Test scope components for testing CDI injection and cleanup.
 *
 * @author Roman Grigoriadi
 */
@ApplicationScoped
public class CdiDependentAdapter implements JsonbAdapter<String, Integer> {

    @Inject
    private CdiTestService cdiTestService;

    @Inject
    @Hello2
    private IHelloService helloService;

    @Override
    public Integer adaptToJson(String obj) throws Exception {
        cdiTestService.runService();
        helloService.sayHello();
        return Integer.valueOf(obj);
    }

    @Override
    public String adaptFromJson(Integer obj) throws Exception {
        return String.valueOf(obj);
    }

    public CdiTestService getCdiTestService() {
        return cdiTestService;
    }

    public IHelloService getHelloService() {
        return helloService;
    }
}
