/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Common type for all supported value type serializers.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractValueTypeDeserializer<T> implements JsonbDeserializer<T> {

    private final Class<T> clazz;

    private final Customization customization;

    /**
     * Creates a new instance.
     *
     * @param clazz Class to work with.
     * @param customization Model customization.
     */
    public AbstractValueTypeDeserializer(Class<T> clazz, Customization customization) {
        this.clazz = clazz;
        this.customization = customization;
    }

    /**
     * Extracts single string value for conversion.
     *
     * @param parser Parser to get value from.
     * @param ctx Unmarshaller.
     * @param rtType return type.
     * @return Deserialized object.
     */
    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Unmarshaller unmarshaller = (Unmarshaller) ctx;
        final JsonParser.Event event = ((JsonbParser) parser).getCurrentLevel().getLastEvent();
        if (event == JsonParser.Event.VALUE_NULL) {
            return null;
        }

        final String value = parser.getString();
        try {
            return deserialize(value, unmarshaller, rtType);
        } catch (JsonbException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.DESERIALIZE_VALUE_ERROR, value, rtType), e);
        }
    }

    /**
     * Convert string value to object.
     *
     * @param jsonValue Json value.
     * @param unmarshaller Unmarshaller instance.
     * @param rtType Runtime type.
     * @return Deserialized object.
     */
    protected T deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        throw new UnsupportedOperationException("Operation not supported in " + getClass());
    }

    /**
     * Returns customization of object
     *
     * @return object customization
     */
    public Customization getCustomization() {
        return customization;
    }

    /**
     * Type of a property or creator parameter which is deserialized.
     *
     * @return property type.
     */
    protected Class<T> getPropertyType() {
        return clazz;
    }
}
