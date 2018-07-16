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
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import javax.json.bind.JsonbException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Deserializer for {@link XMLGregorianCalendar} type.
 *
 * @author David Kral
 */
public class XMLGregorianCalendarTypeDeserializer extends AbstractDateTimeDeserializer<XMLGregorianCalendar> {

    private final Calendar calendarTemplate;
    private final DatatypeFactory datatypeFactory;
    private final LocalTime ZERO_LOCAL_TIME = LocalTime.parse("00:00:00");

    /**
     * Creates an instance.
     *
     * @param customization Model customization.
     */
    public XMLGregorianCalendarTypeDeserializer(Customization customization) {
        super(XMLGregorianCalendar.class, customization);
        this.calendarTemplate = new GregorianCalendar();
        this.calendarTemplate.clear();
        this.calendarTemplate.setTimeZone(TimeZone.getTimeZone(UTC));
        try {
            this.datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DATATYPE_FACTORY_CREATION_FAILED), e);
        }
    }


    @Override
    protected XMLGregorianCalendar fromInstant(Instant instant) {
        final GregorianCalendar calendar = (GregorianCalendar) calendarTemplate.clone();
        calendar.setTimeInMillis(instant.toEpochMilli());
        return datatypeFactory.newXMLGregorianCalendar(calendar);
    }

    @Override
    protected XMLGregorianCalendar parseDefault(String jsonValue, Locale locale) {
        DateTimeFormatter formatter = jsonValue.contains("T") ?
                DateTimeFormatter.ISO_DATE_TIME : DateTimeFormatter.ISO_DATE;
        return parseWithFormatter(jsonValue, formatter.withLocale(locale));
    }

    @Override
    protected XMLGregorianCalendar parseWithFormatter(String jsonValue, DateTimeFormatter formatter) {
        final TemporalAccessor parsed = formatter.parse(jsonValue);
        LocalTime time = parsed.query(TemporalQueries.localTime());
        ZoneId zone = parsed.query(TemporalQueries.zone());
        if (zone == null) {
            zone = UTC;
        }
        if (time == null) {
            time = ZERO_LOCAL_TIME;
        }
        ZonedDateTime result = LocalDate.from(parsed).atTime(time).atZone(zone);
        return datatypeFactory.newXMLGregorianCalendar(GregorianCalendar.from(result));
    }
}
