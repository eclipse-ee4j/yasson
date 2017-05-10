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
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Deserializer for {@link Boolean} type.
 *
 * @author David Kral
 */
public class BooleanTypeDeserializer extends AbstractValueTypeDeserializer<Boolean> {

    /**
     * Creates an instance.
     *
     * @param model Binding model.
     */
    public BooleanTypeDeserializer(JsonBindingModel model) {
        super(Boolean.class, model);
    }

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        final JsonParser.Event event = ((JsonbParser) parser).moveToValue();
        switch (event) {
            case VALUE_TRUE:
                return Boolean.TRUE;
            case VALUE_FALSE:
                return Boolean.FALSE;
            case VALUE_STRING:
                return Boolean.parseBoolean(parser.getString());
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Unknown JSON value: " + event));
        }
    }

}
