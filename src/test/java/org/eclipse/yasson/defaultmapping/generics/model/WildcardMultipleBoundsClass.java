/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.io.Serializable;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class WildcardMultipleBoundsClass<T extends Number & Serializable & Comparable<? extends T>> {

    public T wildcardField;

    public GenericTestClass<String, T> genericTestClassPropagatedWildCard;

    public List<? extends T> propagatedWildcardList;
}
