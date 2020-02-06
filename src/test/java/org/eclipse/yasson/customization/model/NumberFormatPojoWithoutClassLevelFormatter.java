/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.annotation.JsonbNumberFormat;

/**
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class NumberFormatPojoWithoutClassLevelFormatter {
    private Double doubleGetterFormatted;

    private Double doubleSetterFormatted;

    @JsonbNumberFormat(value = "000.000", locale = "en-us")
    private Double doubleSetterAndPropertyFormatter;

    @JsonbNumberFormat("000.00000000")
    public Double getDoubleGetterFormatted() {
        return doubleGetterFormatted;
    }

    public void setDoubleGetterFormatted(Double doubleGetterFormatted) {
        this.doubleGetterFormatted = doubleGetterFormatted;
    }

    public Double getDoubleSetterFormatted() {
        return doubleSetterFormatted;
    }

    @JsonbNumberFormat(value = "000,000", locale = "da-da")
    public void setDoubleSetterFormatted(Double doubleSetterFormatted) {
        this.doubleSetterFormatted = doubleSetterFormatted;
    }

    public Double getDoubleSetterAndPropertyFormatter() {
        return doubleSetterAndPropertyFormatter;
    }

    @JsonbNumberFormat(value = "000,000", locale = "da-da")
    public void setDoubleSetterAndPropertyFormatter(Double doubleSetterAndPropertyFormatter) {
        this.doubleSetterAndPropertyFormatter = doubleSetterAndPropertyFormatter;
    }
}
