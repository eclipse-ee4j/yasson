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

import java.util.Objects;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class CollectionElement<T> {

    private T wrapped;

    public T getWrapped() {
        return wrapped;
    }

    public void setWrapped(T wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrapped);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CollectionElement)) {
            return false;
        }
        final CollectionElement<?> other = (CollectionElement<?>) obj;
        return Objects.equals(wrapped, other.wrapped);
    }
}
