/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link java.sql.Date} type.
 * {@link java.sql.Date} has no time portion, so it uses {@code ISO_DATE} format.
 */
public class SqlDateTypeSerializer extends AbstractDateTypeSerializer<Date> {

    /**
     * Default date time formatter.
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public SqlDateTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected DateTimeFormatter getDefaultFormatter() {
        return DEFAULT_FORMATTER;
    }
}
