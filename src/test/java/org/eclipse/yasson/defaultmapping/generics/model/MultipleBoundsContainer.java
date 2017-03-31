/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * $Id$
 */

package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.List;
import java.util.Queue;

public class MultipleBoundsContainer<T extends List & Queue> implements TypeContainer<List<T>> {
    protected List<T> instance;

    @Override
    public List<T> getInstance() {
        return instance;
    }

    @Override
    public void setInstance(List<T> instance) {
        this.instance = instance;
    }
}
