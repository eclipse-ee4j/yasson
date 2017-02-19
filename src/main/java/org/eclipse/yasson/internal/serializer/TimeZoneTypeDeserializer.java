/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.util.TimeZone;

/**
 * Deserializer for {@link TimeZone} type.
 * 
 * @author David Kral
 */
public class TimeZoneTypeDeserializer extends AbstractValueTypeDeserializer<TimeZone> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public TimeZoneTypeDeserializer(JsonBindingModel model) {
        super(TimeZone.class, model);
    }

    @Override
    protected TimeZone deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        //Use of three-letter time zone ID has been already deprecated and is not supported.
        if (jsonValue.length() == 3) {
            throw new JsonbException("Unsupported TimeZone: " + jsonValue);
        }
        return TimeZone.getTimeZone(jsonValue);
    }
}
