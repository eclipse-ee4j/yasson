/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.specific.model;

import java.util.Objects;
import java.util.Optional;

/**
 * @author David Kral.
 */
public class NotMatchingGettersAndSetters {

    private String firstName = "Person";
    private String lastName = "Correct";

    public void setFirstName(Integer firstName) {
        this.firstName = Integer.toString(firstName);
    }

    public Optional<Integer> getFirstName() {
        return Optional.of(1);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotMatchingGettersAndSetters that = (NotMatchingGettersAndSetters) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
