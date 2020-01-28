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

/**
 * Common parent class for testing date objects.
 * Contains {@link JsonbDateFormat} DEFAULT_FORMAT and TIME_IN_MILLIS annotated date objects.
 *
 * @author Roman Grigoriadi
 */
public class AbstractDateTimePojo<T> {

    public AbstractDateTimePojo() {
    }

    public AbstractDateTimePojo(T dateObj) {
        this.defaultFormatted = dateObj;
        this.millisFormatted = dateObj;
    }

    @JsonbDateFormat(JsonbDateFormat.DEFAULT_FORMAT)
    public T defaultFormatted;

    @JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
    public T millisFormatted;

}
