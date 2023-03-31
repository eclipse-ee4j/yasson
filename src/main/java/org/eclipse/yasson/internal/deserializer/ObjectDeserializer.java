/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Object container deserializer.
 */
class ObjectDeserializer implements ModelDeserializer<JsonParser> {

    static final Consumer<JsonParser> NOOP = jsonParser -> {};
    static final EnumMap<JsonParser.Event, Consumer<JsonParser>> VALUE_SKIPPERS = new EnumMap<>(JsonParser.Event.class);

    static {
        VALUE_SKIPPERS.put(JsonParser.Event.START_OBJECT, JsonParser::skipObject);
        VALUE_SKIPPERS.put(JsonParser.Event.START_ARRAY, JsonParser::skipArray);
    }

    private final Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains;
    private final Function<String, String> renamer;
    private final Class<?> rawClass;
    private final boolean failOnUnknownProperty;
    private final Set<String> ignoredProperties;

    ObjectDeserializer(Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains,
                       Function<String, String> renamer,
                       Class<?> rawClass,
                       boolean failOnUnknownProperty,
                       Set<String> ignoredProperties) {
        this.propertyDeserializerChains = Map.copyOf(propertyDeserializerChains);
        this.renamer = renamer;
        this.rawClass = rawClass;
        this.failOnUnknownProperty = failOnUnknownProperty;
        this.ignoredProperties = Set.copyOf(ignoredProperties);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
        String key = null;
        while (parser.hasNext()) {
            final JsonParser.Event next = parser.next();
            context.setLastValueEvent(next);
            switch (next) {
            case KEY_NAME:
                key = renamer.apply(parser.getString());
                break;
            case VALUE_NULL:
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_NUMBER:
            case VALUE_FALSE:
            case VALUE_TRUE:
                if (propertyDeserializerChains.containsKey(key)) {
                    try {
                        propertyDeserializerChains.get(key).deserialize(parser, context);
                    } catch (JsonbException e) {
                        throw new JsonbException("Unable to deserialize property '" + key + "' because of: " + e.getMessage(), e);
                    }
                } else if (failOnUnknownProperty && !ignoredProperties.contains(key)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_JSON_PROPERTY, key, rawClass));
                } else {
                    //We need to skip the corresponding structure if property key was not found
                    VALUE_SKIPPERS.getOrDefault(next, NOOP).accept(parser);
                }
                break;
            case END_ARRAY:
                break;
            case END_OBJECT:
                return context.getInstance();
            default:
                throw new JsonbException("Unexpected state: " + next);
            }
        }
        return context.getInstance();
    }
}
