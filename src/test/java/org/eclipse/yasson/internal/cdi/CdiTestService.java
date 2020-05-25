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

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * CDI test service
 * @author Roman Grigoriadi
 */
@ApplicationScoped
public class CdiTestService {

    @Inject
    private Event<MethodCalledEvent> calledEvent;

    @Inject
    @Hello1
    private IHelloService helloService;

    public CdiTestService() {
    }

    public void runService() {
        helloService.sayHello();
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".runService"));
    }

    @PreDestroy
    public void preDestroy() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".preDestroy"));
    }

    public IHelloService getHelloService() {
        return helloService;
    }
}
