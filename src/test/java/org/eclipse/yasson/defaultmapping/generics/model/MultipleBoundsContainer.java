/*
 * Copyright (c) 2017, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

/*
 * $Id$
 */

package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.List;
import java.util.Queue;

public class MultipleBoundsContainer<T, L extends List<T> & Queue<T>> implements TypeContainer<List<L>> {
    protected List<L> instance;

    @Override
    public List<L> getInstance() {
        return instance;
    }

    @Override
    public void setInstance(List<L> instance) {
        this.instance = instance;
    }
}
