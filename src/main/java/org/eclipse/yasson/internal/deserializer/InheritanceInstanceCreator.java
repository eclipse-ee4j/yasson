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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;

import static jakarta.json.stream.JsonParser.Event;

/**
 * Instance creator following the inheritance structure defined by {@link jakarta.json.bind.annotation.JsonbTypeInfo}.
 */
class InheritanceInstanceCreator implements ModelDeserializer<JsonParser> {

    private final Class<?> processedType;
    private final Map<String, Class<?>> resolvedClasses = new ConcurrentHashMap<>();
    private final DeserializationModelCreator deserializationModelCreator;
    private final TypeInheritanceConfiguration typeInheritanceConfiguration;
    private final ModelDeserializer<JsonParser> defaultProcessor;

    InheritanceInstanceCreator(Class<?> processedType,
                               DeserializationModelCreator deserializationModelCreator,
                               TypeInheritanceConfiguration typeInheritanceConfiguration,
                               ModelDeserializer<JsonParser> defaultProcessor) {
        this.processedType = processedType;
        this.deserializationModelCreator = deserializationModelCreator;
        this.typeInheritanceConfiguration = typeInheritanceConfiguration;
        this.defaultProcessor = defaultProcessor;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
        String alias;
        JsonParser jsonParser;
        String polymorphismKeyName = typeInheritanceConfiguration.getFieldName();
        JsonObject object = parser.getObject();
        alias = object.getString(polymorphismKeyName, null);
        JsonObject newJsonObject = context.getJsonbContext().getJsonProvider().createObjectBuilder(object)
                .remove(polymorphismKeyName)
                .build();
        jsonParser = new JsonStructureToParserAdapter(newJsonObject);
        //To get to the first event
        Event event = jsonParser.next();
        context.setLastValueEvent(event);
        Class<?> polymorphicTypeClass;
        if (alias == null) {
            return defaultProcessor.deserialize(jsonParser, context);
        }
        polymorphicTypeClass = getPolymorphicTypeClass(alias);
        if (polymorphicTypeClass.equals(processedType)) {
            return defaultProcessor.deserialize(jsonParser, context);
        }
        ModelDeserializer<JsonParser> deserializer = deserializationModelCreator.deserializerChain(polymorphicTypeClass);
        return deserializer.deserialize(jsonParser, context);
    }

    @Override
    public String toString() {
        return "Property " + typeInheritanceConfiguration.getFieldName() + " polymorphic information handler";
    }

    private Class<?> getPolymorphicTypeClass(String alias) {
        if (resolvedClasses.containsKey(alias)) {
            return resolvedClasses.get(alias);
        }
        for (Map.Entry<Class<?>, String> entry : typeInheritanceConfiguration.getAliases().entrySet()) {
            if (entry.getValue().equals(alias)) {
                resolvedClasses.put(alias, entry.getKey());
                return entry.getKey();
            }
        }
        throw new JsonbException("Unknown alias \"" + alias + "\" of the type " + processedType.getName() + ". Known aliases: "
                                         + typeInheritanceConfiguration.getAliases().values());
    }

}
