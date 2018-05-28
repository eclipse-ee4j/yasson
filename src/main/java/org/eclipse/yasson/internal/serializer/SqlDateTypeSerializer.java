/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.model.customization.Customization;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for {@link java.sql.Date} type.
 * {@link java.sql.Date} has no time portion, so it uses {@code ISO_DATE} format.
 *
 * @author Roman Grigoriadi
 */
public class SqlDateTypeSerializer extends AbstractDateTypeSerializer<Date> {

    public static DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE.withZone(UTC);

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
