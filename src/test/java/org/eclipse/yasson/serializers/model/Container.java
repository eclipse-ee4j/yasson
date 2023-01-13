/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Container {

    private List<Containee> containees;

    public Container() {
        containees = new ArrayList<>();
    }

    public Container(List<Containee> containees) {
        this.containees = containees;
    }

    public void setContainees(List<Containee> containees) {
        this.containees = containees;
    }

    public List<Containee> getContainees() {
        return containees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Container container = (Container) o;
        return Objects.equals(containees, container.containees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containees);
    }
}
