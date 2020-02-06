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

package org.eclipse.yasson.defaultmapping.dates.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import java.util.Calendar;

/**
 * @author Roman Grigoriadi
 */
public class CalendarPojo extends AbstractDateTimePojo<Calendar> {

    public CalendarPojo() {
    }

    public CalendarPojo(Calendar dateObj) {
        super(dateObj);
        this.customCalendar = dateObj;
    }

    @JsonbDateFormat("HH:mm:ss | dd-MM-yyyy, Z")
    public Calendar customCalendar;
}
