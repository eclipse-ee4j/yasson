/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.serializers;

import java.lang.reflect.Type;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A serializer or deserializer instance registered in the {@link JsonbConfig} is used for the
 * properties annotated with its class, instead of a new instance
 * (<a href="https://github.com/eclipse-ee4j/yasson/issues/627">#627</a>).
 */
public class RegisteredComponentReuseTest {

    public static class PrefixSerializer implements JsonbSerializer<String> {

        private final String prefix;

        public PrefixSerializer() {
            this("new:");
        }

        public PrefixSerializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void serialize(String obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(prefix + obj);
        }
    }

    public static class PrefixDeserializer implements JsonbDeserializer<String> {

        private final String prefix;

        public PrefixDeserializer() {
            this("new:");
        }

        public PrefixDeserializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return prefix + parser.getString();
        }
    }

    public static class Annotated {

        @JsonbTypeSerializer(PrefixSerializer.class)
        @JsonbTypeDeserializer(PrefixDeserializer.class)
        public String value;
    }

    @Test
    public void registeredSerializerIsUsedForAnnotatedProperty() throws Exception {
        Annotated annotated = new Annotated();
        annotated.value = "x";
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withSerializers(new PrefixSerializer("registered:")))) {
            assertEquals("{\"value\":\"registered:x\"}", jsonb.toJson(annotated));
        }
    }

    @Test
    public void registeredDeserializerIsUsedForAnnotatedProperty() throws Exception {
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withDeserializers(new PrefixDeserializer("registered:")))) {
            assertEquals("registered:x", jsonb.fromJson("{\"value\":\"x\"}", Annotated.class).value);
        }
    }

    @Test
    public void annotatedPropertyWithoutRegisteredInstanceUsesNewInstance() throws Exception {
        Annotated annotated = new Annotated();
        annotated.value = "x";
        try (Jsonb jsonb = JsonbBuilder.create()) {
            assertEquals("{\"value\":\"new:x\"}", jsonb.toJson(annotated));
            assertEquals("new:x", jsonb.fromJson("{\"value\":\"x\"}", Annotated.class).value);
        }
    }
}
