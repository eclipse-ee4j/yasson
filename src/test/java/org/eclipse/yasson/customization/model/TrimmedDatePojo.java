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

package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class TrimmedDatePojo {

    @JsonbDateFormat(value = "yyyy.MM.dd")
    private Date date;

    @JsonbDateFormat(value = "yyyy.MM.dd")
    private Calendar calendar;

    @JsonbDateFormat(value = "yyyy.MM.dd")
    private LocalDateTime localDateTime;

    @JsonbDateFormat(value = "yyyy.MM.dd")
    private ZonedDateTime zonedDateTime;


    @JsonbDateFormat(value = "yyyy.MM.dd HH:ss")
    private ZonedDateTime zonedDateTimeHoursAndSeconds;

    @JsonbDateFormat(value = "yyyy.MM.dd N")
    private ZonedDateTime zonedDateTimeNanosOfDay;

    @JsonbDateFormat(value = "yyyy.MM.dd VV")
    private ZonedDateTime zonedDateTimeOverriddenZone;

    @JsonbDateFormat(value = "yyyy.MM.dd VV")
    private Instant zonedInstant;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public ZonedDateTime getZonedDateTimeNanosOfDay() {
        return zonedDateTimeNanosOfDay;
    }

    public void setZonedDateTimeNanosOfDay(ZonedDateTime zonedDateTimeNanosOfDay) {
        this.zonedDateTimeNanosOfDay = zonedDateTimeNanosOfDay;
    }

    public ZonedDateTime getZonedDateTimeHoursAndSeconds() {
        return zonedDateTimeHoursAndSeconds;
    }

    public void setZonedDateTimeHoursAndSeconds(ZonedDateTime zonedDateTimeHoursAndSeconds) {
        this.zonedDateTimeHoursAndSeconds = zonedDateTimeHoursAndSeconds;
    }

    public ZonedDateTime getZonedDateTimeOverriddenZone() {
        return zonedDateTimeOverriddenZone;
    }

    public void setZonedDateTimeOverriddenZone(ZonedDateTime zonedDateTimeOverriddenZone) {
        this.zonedDateTimeOverriddenZone = zonedDateTimeOverriddenZone;
    }

    public Instant getZonedInstant() {
        return zonedInstant;
    }

    public void setZonedInstant(Instant zonedInstant) {
        this.zonedInstant = zonedInstant;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
