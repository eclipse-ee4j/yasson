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

import jakarta.json.bind.annotation.JsonbDateFormat;
import java.util.Date;

/**
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class DateFormatPojo {

    public Date plainDateField;

    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date formattedDateField;

    public Date getterFormattedDateField;

    public Date setterFormattedDateField;

    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getterAndFieldFormattedDateField;

    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date setterAndFieldFormattedDateField;

    public Date getterAndSetterFormattedDateField;

    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getterAndSetterAndFieldFormattedDateField;



    public Date getPlainDateField() {
        return plainDateField;
    }

    public void setPlainDateField(Date plainDateField) {
        this.plainDateField = plainDateField;
    }



    public Date getFormattedDateField() {
        return formattedDateField;
    }

    public void setFormattedDateField(Date formattedDateField) {
        this.formattedDateField = formattedDateField;
    }



    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getGetterFormattedDateField() {
        return getterFormattedDateField;
    }

    public void setGetterFormattedDateField(Date getterFormattedDateField) {
        this.getterFormattedDateField = getterFormattedDateField;
    }



    public Date getSetterFormattedDateField() {
        return setterFormattedDateField;
    }

    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public void setSetterFormattedDateField(Date setterFormattedDateField) {
        this.setterFormattedDateField = setterFormattedDateField;
    }



    @JsonbDateFormat(value = "HH:mm:ss <> dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getGetterAndFieldFormattedDateField() {
        return getterAndFieldFormattedDateField;
    }

    public void setGetterAndFieldFormattedDateField(Date getterAndFieldFormattedDateField) {
        this.getterAndFieldFormattedDateField = getterAndFieldFormattedDateField;
    }



    public Date getSetterAndFieldFormattedDateField() {
        return setterAndFieldFormattedDateField;
    }

    @JsonbDateFormat(value = "HH:mm:ss <> dd-MM-yyyy", locale = "Europe/Copenhagen")
    public void setSetterAndFieldFormattedDateField(Date setterAndFieldFormattedDateField) {
        this.setterAndFieldFormattedDateField = setterAndFieldFormattedDateField;
    }



    @JsonbDateFormat(value = "HH:mm:ss ^^ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getGetterAndSetterFormattedDateField() {
        return getterAndSetterFormattedDateField;
    }

    @JsonbDateFormat(value = "HH:mm:ss <> dd-MM-yyyy", locale = "Europe/Copenhagen")
    public void setGetterAndSetterFormattedDateField(Date getterAndSetterFormattedDateField) {
        this.getterAndSetterFormattedDateField = getterAndSetterFormattedDateField;
    }



    @JsonbDateFormat(value = "HH:mm:ss <> dd-MM-yyyy", locale = "Europe/Copenhagen")
    public Date getGetterAndSetterAndFieldFormattedDateField() {
        return getterAndSetterAndFieldFormattedDateField;
    }

    @JsonbDateFormat(value = "HH:mm:ss $$ dd-MM-yyyy", locale = "Europe/Copenhagen")
    public void setGetterAndSetterAndFieldFormattedDateField(Date getterAndSetterAndFieldFormattedDateField) {
        this.getterAndSetterAndFieldFormattedDateField = getterAndSetterAndFieldFormattedDateField;
    }
}
