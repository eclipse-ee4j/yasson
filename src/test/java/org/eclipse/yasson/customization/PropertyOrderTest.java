/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.FieldCustomOrder;
import org.eclipse.yasson.customization.model.FieldCustomOrderWrapper;
import org.eclipse.yasson.customization.model.FieldOrder;
import org.eclipse.yasson.customization.model.FieldOrderNameAnnotation;
import org.eclipse.yasson.customization.model.FieldSpecificOrder;
import org.eclipse.yasson.customization.model.RenamedPropertiesContainer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.json.bind.config.PropertyOrderStrategy;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderTest {

    @Test
    public void testPropertySorting() {
        FieldOrder fieldOrder = new FieldOrder();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedLexicographical = "{\"aField\":\"aValue\",\"bField\":\"bValue\",\"cField\":\"cValue\",\"dField\":\"dValue\"}";
        assertEquals(expectedLexicographical, jsonb.toJson(fieldOrder));

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        String expectedReverse = "{\"dField\":\"dValue\",\"cField\":\"cValue\",\"bField\":\"bValue\",\"aField\":\"aValue\"}";
        assertEquals(expectedReverse, jsonb.toJson(fieldOrder));
    }

    @Test
    public void testPropertyCustomOrder() {
        FieldCustomOrder fieldCustomOrder = new FieldCustomOrder();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedCustomOrder = "{\"aField\":\"aValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"bField\":\"bValue\"}";
        assertEquals(expectedCustomOrder, jsonb.toJson(fieldCustomOrder));

        FieldCustomOrderWrapper fieldCustomOrderWrapper = new FieldCustomOrderWrapper();
        String expectedOrder = "{\"fieldCustomOrder\":{\"aField\":\"aValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"bField\":\"bValue\"},\"intField\":1,\"stringField\":\"stringValue\"}";
        assertEquals(expectedOrder, jsonb.toJson(fieldCustomOrderWrapper));
    }

    @Test
    public void testPropertySetCustomOrder() {
        FieldSpecificOrder fieldSpecificOrder = new FieldSpecificOrder();
        String expectedSpecific = "{\"aField\":\"aValue\",\"dField\":\"dValue\",\"bField\":\"bValue\",\"cField\":\"cValue\"}";
        assertEquals(expectedSpecific, defaultJsonb.toJson(fieldSpecificOrder));
        
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        expectedSpecific = "{\"aField\":\"aValue\",\"dField\":\"dValue\",\"cField\":\"cValue\",\"bField\":\"bValue\"}";
        assertEquals(expectedSpecific, jsonb.toJson(fieldSpecificOrder));
    }

    @Test
    public void testPropertySortingWithNamingAnnotation() {
        FieldOrderNameAnnotation fieldOrderNameAnnotation = new FieldOrderNameAnnotation();
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL));
        String expectedLexicographical = "{\"bField\":\"bValue\",\"cField\":\"cValue\",\"dField\":\"dValue\",\"zField\":\"aValue\"}";
        assertEquals(expectedLexicographical, jsonb.toJson(fieldOrderNameAnnotation));

        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
        String expectedReverse = "{\"zField\":\"aValue\",\"dField\":\"dValue\",\"cField\":\"cValue\",\"bField\":\"bValue\"}";
        assertEquals(expectedReverse, jsonb.toJson(fieldOrderNameAnnotation));
    }

    @Test
    public void testLexicographicalPropertyOrderRenamedProperties() {
        JsonbConfig config = new JsonbConfig();
        config.setProperty(JsonbConfig.PROPERTY_ORDER_STRATEGY, PropertyOrderStrategy.LEXICOGRAPHICAL);
        Jsonb jsonb = JsonbBuilder.create(config);

        String jsonString = jsonb.toJson(new RenamedPropertiesContainer() {{ setStringInstance("Test String"); setLongInstance(1); }});
        assertTrue(jsonString.matches("\\{\\s*\"first\"\\s*\\:\\s*0\\s*,\\s*\"second\"\\s*\\:\\s*\"Test String\"\\s*,\\s*\"third\"\\s*\\:\\s*1\\s*\\}"));

        RenamedPropertiesContainer unmarshalledObject = jsonb.fromJson("{ \"first\" : 1, \"second\" : \"Test String\", \"third\" : 1 }", RenamedPropertiesContainer.class);
        assertEquals(3, unmarshalledObject.getIntInstance());
    }

    @Test
    public void testJsonbPropertyOrderOnRenamedProperties() {
        assertEquals("{\"c\":11,\"d\":10,\"aExtra\":\"extra\"}", defaultJsonb.toJson(new Range(10, 11)));
    }
    
    // By default, this object would use A-X ordering with property order:
    // anExtraProp, propA, propB
    // But with JsonbPropertyOrder we will put props 'propB' and 'propA' first, and leftovers will go at the end, resulting in:
    // propB, propA, anExtraProp
    @JsonbPropertyOrder({"propB","propA"})
    public class Range {

        @JsonbProperty("d")
        public final int propA;

        @JsonbProperty("c")
        public final int propB;
        
        @JsonbProperty("aExtra")
        public final String anExtraProp = "extra";

        @JsonbCreator
        public Range(
                @JsonbProperty("d") int propA,
                @JsonbProperty("c") int propB
        ) {
            this.propA = propA;
            this. propB = propB;
        }
    }
}
