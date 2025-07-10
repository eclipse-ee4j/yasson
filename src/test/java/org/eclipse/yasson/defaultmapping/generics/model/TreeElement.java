/*
 * Copyright (c) 2025 Red Hat, Inc. and/or its affiliates.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TreeElement implements TreeTypeContainer<TreeElement> {

    private final String name;
    private List<TreeElement> children = new ArrayList<>();

    public TreeElement(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<TreeElement> getChildren() {
        return children;
    }

    public void setChildren(final List<TreeElement> children) {
        this.children = children;
    }
}
