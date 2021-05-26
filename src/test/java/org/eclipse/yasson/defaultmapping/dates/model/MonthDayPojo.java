/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.time.MonthDay;
import java.util.Objects;

import jakarta.json.bind.annotation.JsonbDateFormat;

/**
 * Pojo object of the {@link MonthDay}.
 */
public class MonthDayPojo {

    public MonthDay monthDay;

    @JsonbDateFormat("dd-MM")
    public MonthDay monthDayWithFormatter;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonthDayPojo that = (MonthDayPojo) o;
        return Objects.equals(monthDay, that.monthDay) && Objects
                .equals(monthDayWithFormatter, that.monthDayWithFormatter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthDay, monthDayWithFormatter);
    }
}
