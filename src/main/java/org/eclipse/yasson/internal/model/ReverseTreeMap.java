/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * TreeMap with a reverse ordering by default.
 *
 * @param <K> comparable key
 * @param <V> value
 */
public class ReverseTreeMap<K extends Comparable<? super K>, V> extends TreeMap<K, V> {

    /**
     * Default constructor of a TreeMap with reverse order.
     */
    public ReverseTreeMap() {
        super(Comparator.reverseOrder());
    }
}
