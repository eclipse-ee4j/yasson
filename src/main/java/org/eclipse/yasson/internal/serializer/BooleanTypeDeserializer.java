/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer for {@link Boolean} type.
 */
public class BooleanTypeDeserializer extends AbstractValueTypeDeserializer<Boolean> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public BooleanTypeDeserializer(Customization customization) {
        super(Boolean.class, customization);
    }

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonParser.Event event = ((JsonbParser) parser).moveToValue();
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
