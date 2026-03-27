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

public class Issue673 {
    
    public static interface Referenceable {

    }

    public static class Reference implements Referenceable {

        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

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

    @JsonbTypeInfo(key = "type", value = {
        @JsonbSubtype(alias = Location.TYPE, 
                      type = Location.class)
    })
    public static interface LocationInterface {

    }

    public static class Location implements LocationInterface {

        public final static String TYPE = "Location";

        private String tags;
        private Referenceable referenceable;

        @JsonbTypeDeserializer(TagsDeserializer.class)
        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
        
        @JsonbTypeDeserializer(ReferenceableDeserializer.class)
        public Referenceable getReference() {
            return referenceable;
        }

        public void setReference(Referenceable referenceable) {
            this.referenceable = referenceable;
        }
    }

    public static class TagsDeserializer implements JsonbDeserializer<String> {
        @Override
        public String deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            final JsonValue v = jp.getArray();
            if (v instanceof JsonArray arr) {
                return arr.stream()
                        .filter(JsonString.class::isInstance)
                        .map(JsonString.class::cast)
                        .map(JsonString::getString)
                        .collect(Collectors.joining(", "));
            }
            return null;
        }

    }
    
    public static class ReferenceableDeserializer implements JsonbDeserializer<Referenceable> {

        @Override
        public Referenceable deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            final JsonValue v = jp.getValue();
            if (v instanceof JsonString str) {
                return new IRIReference(str.getString());
            }
            if (v instanceof JsonObject obj) {
                return dc.deserialize(Reference.class,
                        Json.createParserFactory(Collections.EMPTY_MAP)
                                .createParser(obj));
            }
            return null;
        }
    }

}
