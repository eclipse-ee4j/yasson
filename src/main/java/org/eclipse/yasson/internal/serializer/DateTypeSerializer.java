/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link Date} type.
 *
 * @param <T> date type
 */
public class DateTypeSerializer<T extends Date> extends AbstractDateTypeSerializer<T> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public DateTypeSerializer(Customization customization) {
        super(customization);
    }

    protected DateTimeFormatter getDefaultFormatter() {
        return DEFAULT_FORMATTER;
    }
}
