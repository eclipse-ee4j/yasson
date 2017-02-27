/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/

package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbNumberFormat;

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
