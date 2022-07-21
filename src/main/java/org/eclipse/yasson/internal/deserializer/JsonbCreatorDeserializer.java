/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import static org.eclipse.yasson.internal.deserializer.ObjectDeserializer.NOOP;
import static org.eclipse.yasson.internal.deserializer.ObjectDeserializer.VALUE_SKIPPERS;

/**
 * Creator of the Object instance with the usage of the {@link JsonbCreator}.
 */
class JsonbCreatorDeserializer implements ModelDeserializer<JsonParser> {

    private final Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains;
    private final Map<String, ModelDeserializer<Object>> defaultCreatorValues;
    private final List<String> creatorParams;
    private final Set<String> ignoredProperties;
    private final JsonbCreator creator;
    private final Class<?> clazz;
    private final Function<String, String> renamer;
    private final boolean failOnUnknownProperties;

    JsonbCreatorDeserializer(Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains,
                             Map<String, ModelDeserializer<Object>> defaultCreatorValues,
                             JsonbCreator creator,
                             Class<?> clazz,
                             Function<String, String> renamer,
                             boolean failOnUnknownProperties,
                             Set<String> ignoredProperties) {
        this.propertyDeserializerChains = propertyDeserializerChains;
        this.defaultCreatorValues = defaultCreatorValues;
        this.creatorParams = Arrays.stream(creator.getParams()).map(CreatorModel::getName).collect(Collectors.toList());
        this.ignoredProperties = Set.copyOf(ignoredProperties);
        this.creator = creator;
        this.clazz = clazz;
        this.renamer = renamer;
        this.failOnUnknownProperties = failOnUnknownProperties;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
        String key = null;
        Map<String, Object> paramValues = new HashMap<>();
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
                        Object o = propertyDeserializerChains.get(key).deserialize(parser, context);
                        if (creatorParams.contains(key)) {
                            paramValues.put(key, o);
                        }
                    } catch (JsonbException e) {
                        throw new JsonbException("Unable to deserialize property '" + key + "' because of: " + e.getMessage(), e);
                    }
                } else if (failOnUnknownProperties && !ignoredProperties.contains(key)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_JSON_PROPERTY, key, clazz));
                } else {
                    //We need to skip the corresponding structure if property key was not found
                    VALUE_SKIPPERS.getOrDefault(next, NOOP).accept(parser);
                }
                break;
            case END_OBJECT:
                Object[] params = new Object[creatorParams.size()];
                for (int i = 0; i < creatorParams.size(); i++) {
                    String param = creatorParams.get(i);
                    if (paramValues.containsKey(param)) {
                        params[i] = paramValues.get(param);
                    } else {
                        params[i] = defaultCreatorValues.get(param).deserialize(null, context);
                    }
                }
                context.setInstance(creator.call(params, clazz));
                context.getDeferredDeserializers().forEach(Runnable::run);
                context.getDeferredDeserializers().clear();
                return context.getInstance();
            default:
                throw new JsonbException("Unexpected state: " + next);
            }
        }
        return context.getInstance();
    }

    @Override
    public String toString() {
        return "ObjectInstanceCreator{"
                + "parameters=" + creatorParams
                + ", clazz=" + clazz
                + '}';
    }
}
