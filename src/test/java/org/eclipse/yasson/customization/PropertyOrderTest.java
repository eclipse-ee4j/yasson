/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.customization;

import org.eclipse.yasson.customization.model.*;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

import static org.junit.Assert.assertEquals;

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
        Jsonb jsonb = JsonbBuilder.create();
        FieldSpecificOrder fieldSpecificOrder = new FieldSpecificOrder();
        String expectedSpecific = "{\"aField\":\"aValue\",\"dField\":\"dValue\",\"bField\":\"bValue\",\"cField\":\"cValue\"}";
        assertEquals(expectedSpecific, jsonb.toJson(fieldSpecificOrder));
        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE));
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
        Assert.assertTrue(jsonString.matches("\\{\\s*\"first\"\\s*\\:\\s*0\\s*,\\s*\"second\"\\s*\\:\\s*\"Test String\"\\s*,\\s*\"third\"\\s*\\:\\s*1\\s*\\}"));

        RenamedPropertiesContainer unmarshalledObject = jsonb.fromJson("{ \"first\" : 1, \"second\" : \"Test String\", \"third\" : 1 }", RenamedPropertiesContainer.class);
        Assert.assertEquals(3, unmarshalledObject.getIntInstance());
    }
}
