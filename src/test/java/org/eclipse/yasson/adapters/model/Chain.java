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

package org.eclipse.yasson.adapters.model;

public class Chain {
    
    private String name;
    private Chain linksTo;
    private Foo has;
    
    public Chain(String name) {
        this.name = name;
    }
    
    public Chain() {
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Chain getLinksTo() {
        return linksTo;
    }
    public void setLinksTo(Chain linksTo) {
        this.linksTo = linksTo;
    }
    public Foo getHas() {
        return has;
    }
    public void setHas(Foo has) {
        this.has = has;
    }
    
}
