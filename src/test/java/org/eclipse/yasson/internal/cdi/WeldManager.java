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

import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Starts and Shutdowns Weld container, for CDI testing purposes.
 *
 * @author Roman Grigoriadi
 */
public class WeldManager {

    private Weld weld;

    private InitialContext initialContext;


    public void startWeld(Class... scannedClasses) throws NamingException {
        weld = new Weld().beanClasses(scannedClasses).disableDiscovery();
        WeldContainer container = weld.initialize();
        initialContext = new InitialContext();
        initialContext.bind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME, container.getBeanManager());
    }

    public void shutdownWeld() throws NamingException {
        weld.shutdown();
        initialContext.unbind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME);
        initialContext.close();
    }

}
