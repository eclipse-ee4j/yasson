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

package org.eclipse.yasson.jsonstructure;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Test case for Issue #673: Custom deserializers with polymorphic types and JSON structure API.
 *
 * <p>This test validates the interaction between:
 * <ul>
 *   <li>Custom {@link JsonbDeserializer} implementations</li>
 *   <li>Polymorphic type handling via {@link JsonbTypeInfo} and {@link JsonbSubtype}</li>
 *   <li>JSON-P structure API ({@link JsonArray}, {@link JsonObject}, {@link JsonValue})</li>
 * </ul>
 *
 * <p>The test ensures that custom deserializers can properly access and process JSON structure
 * objects when deserializing complex types with polymorphic behavior.
 *
 * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/673">Issue #673</a>
 */
public class Issue673 {
    
    /**
     * Marker interface for objects that can be referenced.
     * Implemented by both {@link Reference} and {@link IRIReference}.
     */
    public static interface Referenceable {

    }

    /**
     * A reference object with a description field.
     * Deserialized from JSON objects containing a "description" property.
     */
    public static class Reference implements Referenceable {

        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * An IRI (Internationalized Resource Identifier) reference.
     * Deserialized from JSON string values representing URIs.
     */
    public static class IRIReference implements Referenceable {

        private String value;

        public IRIReference() {}

        public IRIReference(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Interface for location types with polymorphic deserialization support.
     * Uses {@link JsonbTypeInfo} to determine concrete type based on "type" field in JSON.
     */
    @JsonbTypeInfo(key = "type", value = {
        @JsonbSubtype(alias = Location.TYPE,
                      type = Location.class)
    })
    public static interface LocationInterface {

    }

    /**
     * Concrete location implementation with custom deserializers for complex fields.
     *
     * <p>Demonstrates:
     * <ul>
     *   <li>Array-to-string conversion via {@link TagsDeserializer}</li>
     *   <li>Polymorphic reference deserialization via {@link ReferenceableDeserializer}</li>
     * </ul>
     */
    public static class Location implements LocationInterface {

        public final static String TYPE = "Location";

        private String tags;
        private Referenceable referenceable;

        /**
         * Gets the tags as a comma-separated string.
         * Uses custom deserializer to convert JSON array to string.
         *
         * @return comma-separated tag string
         */
        @JsonbTypeDeserializer(TagsDeserializer.class)
        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
        
        /**
         * Gets the reference object.
         * Uses custom deserializer to handle polymorphic deserialization
         * from either string (IRI) or object (Reference) JSON values.
         *
         * @return the referenceable object
         */
        @JsonbTypeDeserializer(ReferenceableDeserializer.class)
        public Referenceable getReference() {
            return referenceable;
        }

        public void setReference(Referenceable referenceable) {
            this.referenceable = referenceable;
        }
    }

    /**
     * Custom deserializer that converts a JSON array of strings into a comma-separated string.
     *
     * <p>Example JSON: {@code ["tag1", "tag2", "tag3"]} → {@code "tag1, tag2, tag3"}
     *
     * <p>This tests the ability to use {@link JsonParser#getArray()} to access
     * JSON structure objects during deserialization.
     */
    public static class TagsDeserializer implements JsonbDeserializer<String> {
        @Override
        public String deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            final JsonValue v = jp.getArray();
            if (v instanceof JsonArray) {
                JsonArray arr = (JsonArray) v;
                return arr.stream()
                        .filter(JsonString.class::isInstance)
                        .map(JsonString.class::cast)
                        .map(JsonString::getString)
                        .collect(Collectors.joining(", "));
            }
            return null;
        }

    }
    
    /**
     * Custom deserializer that handles polymorphic deserialization of {@link Referenceable} objects.
     *
     * <p>Supports two JSON representations:
     * <ul>
     *   <li>String value → {@link IRIReference} (e.g., {@code "http://example.com"})</li>
     *   <li>Object value → {@link Reference} (e.g., {@code {"description": "..."}})</li>
     * </ul>
     *
     * <p>This tests the ability to:
     * <ul>
     *   <li>Use {@link JsonParser#getValue()} to access JSON structure objects</li>
     *   <li>Recursively deserialize nested objects using {@link DeserializationContext#deserialize}</li>
     *   <li>Create new parsers from JSON-P structure objects</li>
     * </ul>
     */
    public static class ReferenceableDeserializer implements JsonbDeserializer<Referenceable> {

        @Override
        public Referenceable deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            final JsonValue v = jp.getValue();
            if (v instanceof JsonString) {
                JsonString str = (JsonString) v;
                return new IRIReference(str.getString());
            }
            if (v instanceof JsonObject) {
                JsonObject obj = (JsonObject) v;
                return dc.deserialize(Reference.class,
                        Json.createParserFactory(Collections.emptyMap())
                                .createParser(obj));
            }
            return null;
        }
    }

}
