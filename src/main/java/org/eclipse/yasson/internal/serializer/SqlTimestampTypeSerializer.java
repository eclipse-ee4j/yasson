/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Andy Guibert
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.model.customization.Customization;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serializer for {@link java.sql.Timestamp} type.
 */
public class SqlTimestampTypeSerializer extends AbstractDateTimeSerializer<Timestamp> {

    public static DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(UTC);

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public SqlTimestampTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected Instant toInstant(Timestamp value) {
        return value.toInstant();
    }

	@Override
	protected String formatDefault(Timestamp value, Locale locale) {
		return DEFAULT_FORMATTER.withLocale(locale).format(toInstant(value));
	}
}
