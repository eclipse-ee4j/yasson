/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Optional;

/**
 * @author Roman Grigoriadi
 */
public class OptionalWrapper {

    private Optional<Street> streetOptional;

    public Optional<Street> getStreetOptional() {
        return streetOptional;
    }

    public void setStreetOptional(Optional<Street> streetOptional) {
        this.streetOptional = streetOptional;
    }
}
