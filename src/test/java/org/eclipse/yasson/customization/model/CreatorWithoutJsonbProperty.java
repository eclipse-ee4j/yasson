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

package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class CreatorWithoutJsonbProperty {

    private final String par1;
    private final String par2;
    private double par3;

    @JsonbCreator
    public CreatorWithoutJsonbProperty(@JsonbProperty("s1") String par1, String par2, double d1) {
        this.par1 = par1;
        this.par2 = par2;
        this.par3 = d1;
    }

    public String getPar1() {
        return par1;
    }

    public String getPar2() {
        return par2;
    }

    public double getPar3() {
        return par3;
    }
}
