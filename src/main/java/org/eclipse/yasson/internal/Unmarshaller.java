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

package org.eclipse.yasson.internal;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.DefaultSerializers;
import org.eclipse.yasson.internal.serializer.DeserializerBuilder;

/**
 * JSONB unmarshaller.
 * Uses {@link JsonParser} to navigate through json string.
 */
public class Unmarshaller extends ProcessingContext implements DeserializationContext {

    private static final Logger LOGGER = Logger.getLogger(Unmarshaller.class.getName());

    /**
     * Creates instance of unmarshaller.
     *
     * @param jsonbContext context to use
     */
    public Unmarshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, JsonParser parser) {
        return deserializeItem(clazz, parser);
    }

    @Override
    public <T> T deserialize(Type type, JsonParser parser) {
        return deserializeItem(type, parser);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeItem(Type type, JsonParser parser) {
        try {
            DeserializerBuilder deserializerBuilder = new DeserializerBuilder(getJsonbContext())
                    .withType(type).withJsonValueType(getRootEvent(parser));
            Class<?> rawType = ReflectionUtils.getRawType(type);
            if (!DefaultSerializers.getInstance().isKnownType(rawType)) {
                ClassModel classModel = getMappingContext().getOrCreateClassModel(rawType);
                deserializerBuilder.withCustomization(classModel.getClassCustomization());
            }

            return (T) deserializerBuilder.build().deserialize(parser, this, type);
        } catch (JsonbException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, e.getMessage()), e);
        }
    }

    /**
     * Get root value event, either for new deserialization process, or deserialization sub-process invoked from
     * custom user deserializer.
     */
    private JsonParser.Event getRootEvent(JsonParser parser) {
        JsonbRiParser.LevelContext currentLevel = ((JsonbParser) parser).getCurrentLevel();
        //Wrapper parser is at start
        if (currentLevel.getParent() == null) {
            return parser.next();
        }
        final JsonParser.Event lastEvent = currentLevel.getLastEvent();
        return lastEvent == JsonParser.Event.KEY_NAME ? parser.next() : lastEvent;
    }

}
