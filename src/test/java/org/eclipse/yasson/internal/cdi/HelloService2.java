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

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Dependant scoped bean, should be destroyed with its wrapper bean.
 *
 * @author Roman Grigoriadi
 */
@Hello2
@Dependent
public class HelloService2 implements IHelloService {

    @Inject
    private Event<MethodCalledEvent> calledEvent;

    @Override
    public void sayHello() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".sayHello"));
    }

    @PreDestroy
    public void preDestroy() {
        calledEvent.fire(new MethodCalledEvent(getClass().getName() + ".preDestroy"));
    }


}
