/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.YassonJsonb;
import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonStructureToParserAdapterTest {
    private final JsonProvider jsonProvider = JsonProvider.provider();

    @Test
    public void testBasicJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
    }

    @Test
    public void testNullValues() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.addNull("stringProperty");
        objectBuilder.addNull("bigDecimalProperty");
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
    }

    @Test
    public void testInnerJsonObjectWrappedWithProperties() {
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("innerFirst", "Inner value 1");
        innerBuilder.add("innerSecond", "Inner value 2");

        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("inner", innerBuilder.build());
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
        assertEquals("Inner value 1", result.getInner().getInnerFirst());
        assertEquals("Inner value 2", result.getInner().getInnerSecond());
    }

    @Test
    public void testInnerJsonObjectAtEndProperty() {
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("innerFirst", "Inner value 1");
        innerBuilder.add("innerSecond", "Inner value 2");

        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);
        objectBuilder.add("inner", innerBuilder.build());

        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals("value 1", result.getStringProperty());
        assertEquals(new BigDecimal("1.1"), result.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), result.getLongProperty());
        assertEquals("Inner value 1", result.getInner().getInnerFirst());
        assertEquals("Inner value 2", result.getInner().getInnerSecond());

    }

    @Test
    public void testEmptyJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        JsonObject jsonObject = objectBuilder.build();
        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertNull(result.getLongProperty());
    }

    @Test
    public void testEmptyInnerJsonObject() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();

        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        JsonObject innerObject = innerBuilder.build();

        objectBuilder.add("inner", innerObject);

        JsonObject jsonObject = objectBuilder.build();

        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);
        assertNull(result.getStringProperty());
        assertNull(result.getBigDecimalProperty());
        assertNull(result.getLongProperty());

        assertNotNull(result.getInner());
        assertNull(result.getInner().getInnerFirst());
        assertNull(result.getInner().getInnerSecond());
    }

    @Test
    public void testSimpleArray() {
        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(BigDecimal.TEN).add("String value").addNull();
        JsonArray jsonArray = arrayBuilder.build();
        List result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
        assertEquals(3, result.size());
        assertEquals(BigDecimal.TEN, result.get(0));
        assertEquals("String value", result.get(1));
        assertNull(result.get(2));
    }

    @Test
    public void testArraysInsideObject() {
        JsonArrayBuilder bigDecBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder strBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder blnBuilder = jsonProvider.createArrayBuilder();

        bigDecBuilder.add(BigDecimal.TEN);
        strBuilder.add("String value 1");
        blnBuilder.add(Boolean.TRUE);

        JsonObjectBuilder pojoBuilder = jsonProvider.createObjectBuilder();
        pojoBuilder.add("strings", strBuilder.build());
        pojoBuilder.add("bigDecimals", bigDecBuilder.build());
        pojoBuilder.add("booleans", blnBuilder.build());

        JsonObject jsonObject = pojoBuilder.build();
        Pojo pojo = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertEquals(1, pojo.getBigDecimals().size());
        assertEquals(1, pojo.getStrings().size());
        assertEquals(1, pojo.getBooleans().size());
    }

    @Test
    public void testNestedArrays() {
        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        JsonArrayBuilder innerArrBuilder = jsonProvider.createArrayBuilder();
        innerArrBuilder.add("first").add("second");
        arrayBuilder.add(BigDecimal.TEN);
        arrayBuilder.add(innerArrBuilder.build());

        JsonArray jsonArray = arrayBuilder.build();

        ArrayList result = yassonJsonb.fromJsonStructure(jsonArray, ArrayList.class);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.TEN, result.get(0));
        assertTrue(result.get(1) instanceof List);
        List inner = (List) result.get(1);
        assertEquals(2, inner.size());
        assertEquals("first", inner.get(0));
        assertEquals("second", inner.get(1));
    }

    @Test
    public void testObjectsNestedInArrays() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);

        JsonArrayBuilder innerArrayBuilder = jsonProvider.createArrayBuilder();
        innerArrayBuilder.add("String value 1");
        objectBuilder.add("strings", innerArrayBuilder.build());

        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(objectBuilder.build());

        JsonArray rootArray = arrayBuilder.build();

        List<Object> result = yassonJsonb.fromJsonStructure(rootArray, new TestTypeToken<List<Pojo>>(){}.getType());
        assertTrue(result.get(0) instanceof Pojo);
        Pojo pojo = (Pojo) result.get(0);
        assertNotNull(pojo);
        assertEquals("value 1", pojo.getStringProperty());
        assertEquals(new BigDecimal("1.1"), pojo.getBigDecimalProperty());
        assertEquals(Long.valueOf(10), pojo.getLongProperty());
        assertNotNull(pojo.getStrings());
        assertEquals(1, pojo.getStrings().size());
        assertEquals("String value 1", pojo.getStrings().get(0));
    }

    @Test
    public void testObjectsNestedInArraysRaw() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("stringProperty", "value 1");
        objectBuilder.add("bigDecimalProperty", new BigDecimal("1.1"));
        objectBuilder.add("longProperty", 10L);

        JsonArrayBuilder innerArrayBuilder = jsonProvider.createArrayBuilder();
        innerArrayBuilder.add("String value 1");

        objectBuilder.add("strings", innerArrayBuilder.build());

        JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder();
        arrayBuilder.add(10L);
        arrayBuilder.add(objectBuilder.build());
        arrayBuilder.add("10");

        JsonArray rootArray = arrayBuilder.build();

        List<Object> result = yassonJsonb.fromJsonStructure(rootArray, new TestTypeToken<List<Object>>(){}.getType());
        assertEquals(new BigDecimal("10"), result.get(0));
        assertTrue(result.get(1) instanceof Map);
        Map pojo = (Map) result.get(1);
        assertNotNull(pojo);
        assertEquals("value 1", pojo.get("stringProperty"));
        assertEquals(new BigDecimal("1.1"), pojo.get("bigDecimalProperty"));
        assertEquals(new BigDecimal(10), pojo.get("longProperty"));
        assertTrue(pojo.get("strings") instanceof List);
        List strings = (List) pojo.get("strings");
        assertNotNull(strings);
        assertEquals(1, strings.size());
        assertEquals("String value 1", strings.get(0));
    }


    @Test
    public void testCustomJsonbDeserializer() {
        JsonObjectBuilder outerBuilder = jsonProvider.createObjectBuilder();
        JsonObjectBuilder innerBuilder = jsonProvider.createObjectBuilder();
        innerBuilder.add("first", "String value 1");
        innerBuilder.add("second", "String value 2");
        outerBuilder.add("inner", innerBuilder.build());
        JsonObject object = outerBuilder.build();

        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create(new JsonbConfig().withDeserializers(new InnerPojoDeserializer()));
        Pojo result = jsonb.fromJsonStructure(object, Pojo.class);
        assertNotNull(result.getInner());
        assertEquals("String value 1", result.getInner().getInnerFirst());
        assertEquals("String value 2", result.getInner().getInnerSecond());
    }
    
    /**
     * Tests that a custom deserializer can call {@link jakarta.json.stream.JsonParser#getValue()}
     * to access a JSON-P value object when deserializing a polymorphic type.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/673">Issue #673</a>
     */
    @Test
    public void testGetValue() {
        final String json =
        """
        {
            "type": "Location",
            "reference": "dummy reference"
        }
        """;
        
        Jsonb jsonb = JsonbBuilder.create();
        PolymorphicDeserializerFixtures.LocationInterface result = jsonb.fromJson(json, PolymorphicDeserializerFixtures.LocationInterface.class);
            
        assertNotNull(result);
        assertTrue(result instanceof PolymorphicDeserializerFixtures.Location);
        PolymorphicDeserializerFixtures.Location location = (PolymorphicDeserializerFixtures.Location) result;

        PolymorphicDeserializerFixtures.Referenceable refAble = location.getReference();
        assertNotNull(refAble);
        assertFalse(refAble instanceof PolymorphicDeserializerFixtures.Reference);
        assertTrue(refAble instanceof PolymorphicDeserializerFixtures.IRIReference);
        PolymorphicDeserializerFixtures.IRIReference ref = (PolymorphicDeserializerFixtures.IRIReference) refAble;

        assertEquals("dummy reference", ref.getValue());
    }

    /**
     * Tests that a custom deserializer can call {@link jakarta.json.stream.JsonParser#getArray()}
     * to access a JSON-P array object when deserializing a polymorphic type.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/673">Issue #673</a>
     */
    @Test
    public void testGetArray() {
        final String json =
        """
        {
            "type": "Location",
            "tags": ["test1", "test2"]
        }
        """;
        
        Jsonb jsonb = JsonbBuilder.create();
        PolymorphicDeserializerFixtures.LocationInterface result = jsonb.fromJson(json, PolymorphicDeserializerFixtures.LocationInterface.class);
        
        assertNotNull(result);
        assertTrue(result instanceof PolymorphicDeserializerFixtures.Location);
        PolymorphicDeserializerFixtures.Location location = (PolymorphicDeserializerFixtures.Location) result;
        
        String tags = location.getTags();
        assertNotNull(tags);

        assertEquals("test1, test2", tags);
    }

    /**
     * Tests that {@link jakarta.json.stream.JsonParser#isIntegralNumber()} throws
     * {@link IllegalStateException} (not {@code JsonbException}) when called on a non-numeric
     * token, allowing callers to catch it and fall back to {@link JsonParser#getString()}.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/707">Issue #707</a>
     */
    @Test
    public void isIntegralNumberThrowsIllegalStateException() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("id", "abc123");
        JsonObject jsonObject = objectBuilder.build();
        
        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create();
        IntegralNumberDeserializerFixtures.Request result = jsonb.fromJsonStructure(jsonObject, IntegralNumberDeserializerFixtures.Request.class);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("abc123", result.getId().getValue());
    }

    /**
     * Tests that an integral numeric ID is deserialized correctly when
     * {@link jakarta.json.stream.JsonParser#isIntegralNumber()} returns {@code true}.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/707">Issue #707</a>
     */
    @Test
    public void isIntegralNumberWithNumericValue() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("id", 12345);
        JsonObject jsonObject = objectBuilder.build();
        
        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create();
        IntegralNumberDeserializerFixtures.Request result = jsonb.fromJsonStructure(jsonObject, IntegralNumberDeserializerFixtures.Request.class);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("12345", result.getId().getValue());
    }

    /**
     * Tests that a floating-point numeric ID is deserialized correctly: {@code isIntegralNumber()}
     * returns {@code false} without throwing, and the value is read via
     * {@link jakarta.json.stream.JsonParser#getBigDecimal()}.
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/707">Issue #707</a>
     */
    @Test
    public void isIntegralNumberWithFloatingPoint() {
        JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder();
        objectBuilder.add("id", 123.45);
        JsonObject jsonObject = objectBuilder.build();
        
        YassonJsonb jsonb = (YassonJsonb) JsonbBuilder.create();
        IntegralNumberDeserializerFixtures.Request result = jsonb.fromJsonStructure(jsonObject, IntegralNumberDeserializerFixtures.Request.class);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("123.45", result.getId().getValue());
    }

    /**
     * Regression test for the bug where {@link org.eclipse.yasson.internal.jsonstructure.JsonStructureIterator#getValueEvent}
     * incorrectly mapped {@code TRUE} and {@code FALSE} {@link jakarta.json.JsonValue.ValueType}s to
     * {@link JsonParser.Event#VALUE_STRING} instead of {@link JsonParser.Event#VALUE_TRUE} and
     * {@link JsonParser.Event#VALUE_FALSE}.
     *
     * <p>Drives {@link JsonStructureToParserAdapter} directly so that the wrong event would be visible
     * at the parser level regardless of how Yasson's deserialization chain handles it internally.
     */
    @Test
    public void booleanValuesInArrayProduceCorrectEvents() {
        JsonArray jsonArray = jsonProvider.createArrayBuilder()
                .add(true)
                .add(false)
                .build();

        try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonArray)) {
            // Verify currentEvent() is null before any next() call
            assertNull(parser.currentEvent(), "currentEvent() must be null before the first next() call");

            JsonParser.Event e1 = parser.next();
            assertEquals(JsonParser.Event.START_ARRAY, e1);
            assertEquals(JsonParser.Event.START_ARRAY, parser.currentEvent(),
                    "currentEvent() must equal the last event returned by next()");

            JsonParser.Event e2 = parser.next();
            assertEquals(JsonParser.Event.VALUE_TRUE, e2,
                    "JsonValue.TRUE must produce VALUE_TRUE, not VALUE_STRING");
            assertEquals(JsonParser.Event.VALUE_TRUE, parser.currentEvent(),
                    "currentEvent() must reflect VALUE_TRUE after advancing");

            JsonParser.Event e3 = parser.next();
            assertEquals(JsonParser.Event.VALUE_FALSE, e3,
                    "JsonValue.FALSE must produce VALUE_FALSE, not VALUE_STRING");
            assertEquals(JsonParser.Event.VALUE_FALSE, parser.currentEvent(),
                    "currentEvent() must reflect VALUE_FALSE after advancing");

            JsonParser.Event e4 = parser.next();
            assertEquals(JsonParser.Event.END_ARRAY, e4);
            assertEquals(JsonParser.Event.END_ARRAY, parser.currentEvent());
        }
    }

    /**
     * Regression test verifying that boolean object properties produce the correct parser events
     * ({@link JsonParser.Event#VALUE_TRUE} and {@link JsonParser.Event#VALUE_FALSE})
     * when iterating through a {@link JsonObject} via {@link JsonStructureToParserAdapter}.
     *
     * <p>In the original buggy code the {@code TRUE}/{@code FALSE} switch cases fell through to
     * {@code VALUE_STRING}, which would silently produce wrong events for object-valued booleans.
     */
    @Test
    public void booleanValuesInObjectProduceCorrectEvents() {
        JsonObject jsonObject = jsonProvider.createObjectBuilder()
                .add("flagTrue", true)
                .add("flagFalse", false)
                .build();

        try (JsonStructureToParserAdapter parser = new JsonStructureToParserAdapter(jsonObject)) {
            assertEquals(JsonParser.Event.START_OBJECT, parser.next());

            // flagTrue key
            assertEquals(JsonParser.Event.KEY_NAME, parser.next());
            assertEquals("flagTrue", parser.getString());

            JsonParser.Event trueEvent = parser.next();
            assertEquals(JsonParser.Event.VALUE_TRUE, trueEvent,
                    "JsonValue.TRUE in an object must produce VALUE_TRUE, not VALUE_STRING");
            assertEquals(JsonParser.Event.VALUE_TRUE, parser.currentEvent());

            // flagFalse key
            assertEquals(JsonParser.Event.KEY_NAME, parser.next());
            assertEquals("flagFalse", parser.getString());

            JsonParser.Event falseEvent = parser.next();
            assertEquals(JsonParser.Event.VALUE_FALSE, falseEvent,
                    "JsonValue.FALSE in an object must produce VALUE_FALSE, not VALUE_STRING");
            assertEquals(JsonParser.Event.VALUE_FALSE, parser.currentEvent());

            assertEquals(JsonParser.Event.END_OBJECT, parser.next());
        }
    }

    /**
     * End-to-end regression that boolean fields on a POJO round-trip correctly through
     * {@link org.eclipse.yasson.YassonJsonb#fromJsonStructure} when the source is a {@link JsonObject}.
     *
     * <p>Prior to the fix, {@code VALUE_TRUE}/{@code VALUE_FALSE} were reported as {@code VALUE_STRING},
     * causing Yasson's type-switch to fall through to its default case and throw a {@link jakarta.json.bind.JsonbException}.
     */
    @Test
    public void booleanFieldsDeserializeCorrectlyFromJsonStructure() {
        JsonObject jsonObject = jsonProvider.createObjectBuilder()
                .add("booleans", jsonProvider.createArrayBuilder()
                        .add(true)
                        .add(false)
                        .build())
                .build();

        Pojo result = yassonJsonb.fromJsonStructure(jsonObject, Pojo.class);

        assertNotNull(result.getBooleans(), "booleans list must not be null");
        assertEquals(2, result.getBooleans().size());
        assertEquals(Boolean.TRUE, result.getBooleans().get(0),
                "First boolean element must deserialize to TRUE");
        assertEquals(Boolean.FALSE, result.getBooleans().get(1),
                "Second boolean element must deserialize to FALSE");
    }
}
