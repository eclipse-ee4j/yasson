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

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.OptionalInt;

/**
 * @author David Kral
 */
public class OptionalIntTypeDeserializer extends AbstractValueTypeDeserializer<OptionalInt> {

    public OptionalIntTypeDeserializer(JsonBindingModel model) {
        super(OptionalInt.class, model);
    }

    @Override
    public OptionalInt deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event next = ((JsonbParser) parser).moveToValue();
        if (next == JsonParser.Event.VALUE_NULL) {
            return OptionalInt.empty();
        }
        final String value = parser.getString();
        return deserialize(value, (Unmarshaller) ctx, rtType);
    }

    @Override
    protected OptionalInt deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        try {
            return OptionalInt.of(Integer.parseInt(jsonValue));
        } catch (NumberFormatException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, OptionalInt.class));
        }
    }

}
