/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David Kral
 ******************************************************************************/
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
