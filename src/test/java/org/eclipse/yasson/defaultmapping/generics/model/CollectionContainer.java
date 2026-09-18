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

import java.util.Collection;
import java.util.Objects;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class CollectionContainer {

    private CollectionWrapper<CollectionElement<?>> collection;

    public CollectionWrapper<CollectionElement<?>> getCollection() {
        return collection;
    }

    public void setCollection(final CollectionWrapper<CollectionElement<?>> collection) {
        this.collection = collection;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CollectionContainer)) {
            return false;
        }
        final CollectionContainer other = (CollectionContainer) obj;
        final Collection<CollectionElement<?>> thisCollection = collection.getCollection();
        final Collection<CollectionElement<?>> otherCollection = other.collection.getCollection();
        if (thisCollection == null && otherCollection == null) {
            return true;
        }
        if (thisCollection == null || otherCollection == null) {
            return false;
        }
        return thisCollection.containsAll(otherCollection) && otherCollection.containsAll(thisCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collection);
    }
}
