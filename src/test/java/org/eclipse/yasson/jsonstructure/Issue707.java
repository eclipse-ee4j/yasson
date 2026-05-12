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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Test resources for Issue #707: YassonParser.isIntegralNumber throws JsonbException 
 * instead of IllegalStateException.
 * 
 * This replicates the user's scenario where they want to handle both numeric and 
 * string IDs by catching IllegalStateException when the value is not a number.
 */
public class Issue707 {
    
    /**
     * Simple ID wrapper that can be created from either a long or a string.
     */
    public static class RequestId {
        private final String value;
        
        private RequestId(String value) {
            this.value = value;
        }
        
        public static RequestId of(long id) {
            return new RequestId(String.valueOf(id));
        }
        
        public static RequestId of(String id) {
            return new RequestId(id);
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RequestId requestId = (RequestId) o;
            return Objects.equals(value, requestId.value);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
        
        @Override
        public String toString() {
            return "RequestId{" + value + "}";
        }
    }
    
    /**
     * Container class that uses a custom deserializer for the ID field.
     */
    public static class Request {
        private RequestId id;
        
        @JsonbTypeDeserializer(RequestIdDeserializer.class)
        public RequestId getId() {
            return id;
        }
        
        public void setId(RequestId id) {
            this.id = id;
        }
    }
    
    /**
     * Custom deserializer that handles both numeric and string IDs.
     * This is the exact pattern from the issue report.
     */
    public static class RequestIdDeserializer implements JsonbDeserializer<RequestId> {
        @Override
        public RequestId deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            RequestId id = null;
            try {
                // Try to read as a number first
                if (parser.isIntegralNumber()) {
                    id = RequestId.of(parser.getLong());
                } else {
                    // Not an integral number, but is still a number
                    id = RequestId.of(parser.getBigDecimal().toString());
                }
            } catch (IllegalStateException e) {
                id = RequestId.of(parser.getString());
            }
            return id;
        }
    }
}

