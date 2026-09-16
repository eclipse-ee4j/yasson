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
 * Test fixtures for custom deserializers that handle both numeric and string ID values,
 * relying on {@link JsonParser#isIntegralNumber()} throwing {@link IllegalStateException}
 * when called on a non-numeric token.
 *
 * <p>These classes support tests that verify {@code YassonParser.isIntegralNumber()} propagates
 * the correct exception type ({@code IllegalStateException}) rather than wrapping it as a
 * {@code JsonbException}, so that caller code can distinguish a type-mismatch from a
 * serialisation failure.
 *
 * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/707">Issue #707</a>
 * @see JsonStructureToParserAdapterTest#isIntegralNumberThrowsIllegalStateException()
 * @see JsonStructureToParserAdapterTest#isIntegralNumberWithNumericValue()
 * @see JsonStructureToParserAdapterTest#isIntegralNumberWithFloatingPoint()
 */
public class IntegralNumberDeserializerFixtures {

    /**
     * Simple ID wrapper that can be created from either a {@code long} or a {@code String}.
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
     *
     * <p>Attempts to read the token as an integral number first. If the token is not numeric,
     * {@link JsonParser#isIntegralNumber()} throws {@link IllegalStateException}, which is
     * caught so that the value can be read as a plain string instead.
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
