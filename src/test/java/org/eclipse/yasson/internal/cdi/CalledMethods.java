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
import jakarta.enterprise.event.Observes;
import java.util.HashMap;
import java.util.Map;

/**
 * Mockito like method call counter for CDI beans.
 *
 * @author Roman Grigoriadi
 */
@ApplicationScoped
public class CalledMethods {

    /**
     * Maps method name to called count.
     */
    private Map<String, Integer> results = new HashMap<>();

    public void registerCall(@Observes MethodCalledEvent methodCalledEvent) {
        results.compute(methodCalledEvent.getMethodName(), (s, c) -> c == null ? 1 : c + 1);
    }

    public Map<String, Integer> getResults() {
        return results;
    }
}
