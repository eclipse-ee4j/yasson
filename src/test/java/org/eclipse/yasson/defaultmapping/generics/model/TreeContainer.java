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

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TreeContainer<T extends TreeTypeContainer<T>> {

    private TreeTypeContainer<T> tree;

    public TreeTypeContainer<T> getTree() {
        return tree;
    }

    public void setTree(final TreeTypeContainer<T> tree) {
        this.tree = tree;
    }
}
