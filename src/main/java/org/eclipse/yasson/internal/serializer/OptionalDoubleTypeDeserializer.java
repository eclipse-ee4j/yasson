/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;
import java.util.OptionalDouble;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link OptionalDouble} type.
 */
public class OptionalDoubleTypeDeserializer extends AbstractValueTypeDeserializer<OptionalDouble> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public OptionalDoubleTypeDeserializer(Customization customization) {
        super(OptionalDouble.class, customization);
    }

    @Override
    public OptionalDouble deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event next = ((JsonbParser) parser).moveToValue();
        if (next == JsonParser.Event.VALUE_NULL) {
            return OptionalDouble.empty();
        }
        String value = parser.getString();
        return deserialize(value, (Unmarshaller) ctx, rtType);
    }

    @Override
    protected OptionalDouble deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        try {
            return OptionalDouble.of(Double.parseDouble(jsonValue));
        } catch (NumberFormatException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, OptionalDouble.class));
        }
    }
}
