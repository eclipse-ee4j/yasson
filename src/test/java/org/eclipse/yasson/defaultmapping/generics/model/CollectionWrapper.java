/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.Map;

public class CollectionWrapper<T> {

    public CollectionWrapper() {
    }

    private Collection<T> collection;

    private Collection<Collection<T>> wrappedCollection;

    private Map<String, Map<String,String>> wrappedMap;

    public Collection<T> getCollection() {
        return collection;
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }

    public Collection<Collection<T>> getWrappedCollection() {
        return wrappedCollection;
    }

    public void setWrappedCollection(Collection<Collection<T>> wrappedCollection) {
        this.wrappedCollection = wrappedCollection;
    }

    public Map<String, Map<String, String>> getWrappedMap() {
        return wrappedMap;
    }

    public void setWrappedMap(Map<String, Map<String, String>> wrappedMap) {
        this.wrappedMap = wrappedMap;
    }
}
