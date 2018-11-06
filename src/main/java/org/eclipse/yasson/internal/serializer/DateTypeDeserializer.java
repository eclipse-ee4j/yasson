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
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Deserializer for {@link Date} type.
 *
 * @author David Kral
 */
public class DateTypeDeserializer extends AbstractDateTimeDeserializer<Date> {

	private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	/**
	 * Creates an instance.
	 *
	 * @param customization Model customization.
	 */
	public DateTypeDeserializer(Customization customization) {
		super(Date.class, customization);
	}

	@Override
	protected Date fromInstant(Instant instant) {
		return new Date(instant.toEpochMilli());
	}

	@Override
    protected Date parseDefault(String jsonValue, Locale locale) {
		TemporalAccessor parsed = parseWithOrWithoutZone(jsonValue, DEFAULT_DATE_TIME_FORMATTER.withLocale(locale), UTC);
		
        return new Date(Instant.from(parsed).toEpochMilli());
    }

	@Override
	protected Date parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
		TemporalAccessor parsed = parseWithOrWithoutZone(jsonValue, formatter, UTC);
		
		return new Date(Instant.from(parsed).toEpochMilli());
	}

	/**
	 * Parses the jsonValue as an Date.<br>
	 * At first the Date is parsed with an Offset/ZoneId.<br>
	 * If no Offset/ZoneId is present and the parsing fails, it will be parsed again with the fixed ZoneId that was passed as defaultZone.
	 * 
	 * @param jsonValue Value from json
	 * @param formatter DateFormat-Options
	 * @param defaultZone This Zone will be used if no other Zone was found in the jsonValue
	 * @return Parsed Date
	 */
	private TemporalAccessor parseWithOrWithoutZone(String jsonValue, DateTimeFormatter formatter, ZoneId defaultZone) {
		try {
			return ZonedDateTime.parse(jsonValue, formatter);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			// Exception occures possibly because no Offset/ZoneId was found
			return ZonedDateTime.parse(jsonValue, formatter.withZone(defaultZone));
		}
	}
}
