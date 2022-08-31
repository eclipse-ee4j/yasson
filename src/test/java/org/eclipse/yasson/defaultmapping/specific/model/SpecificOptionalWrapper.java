/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class SpecificOptionalWrapper {

    private OptionalInt optionalInt;
    private OptionalLong optionalLong;
    private OptionalDouble optionalDouble;

    public OptionalInt optionalInt() {
        return optionalInt;
    }

    public void setOptionalInt(OptionalInt optionalInt) {
        this.optionalInt = optionalInt;
    }

    public OptionalLong optionalLong() {
        return optionalLong;
    }

    public void setOptionalLong(OptionalLong optionalLong) {
        this.optionalLong = optionalLong;
    }

    public OptionalDouble optionalDouble() {
        return optionalDouble;
    }

    public void setOptionalDouble(OptionalDouble optionalDouble) {
        this.optionalDouble = optionalDouble;
    }

}
