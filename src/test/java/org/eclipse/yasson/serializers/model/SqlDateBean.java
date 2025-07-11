/*
 * Copyright (c) 2025 Red Hat, Inc. and/or its affiliates.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers.model;

import jakarta.json.bind.annotation.JsonbDateFormat;

import java.sql.Date;

public class SqlDateBean {

    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private java.sql.Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
