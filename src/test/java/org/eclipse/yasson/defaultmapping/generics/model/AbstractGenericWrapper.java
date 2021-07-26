/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

public abstract class AbstractGenericWrapper<T extends AbstractMember> {

    T member;

    public T getMember() {
        return member;
    }

    public void setMember(T member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractGenericWrapper<?> that = (AbstractGenericWrapper<?>) o;
        return Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member);
    }
}
