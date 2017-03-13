/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.customization.transients;

import org.eclipse.yasson.customization.transients.models.*;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbProperty;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTransientTest {

    private Jsonb jsonb;

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testJsonbTransientPropertySerialize() {
        JsonbTransientValue pojo = new JsonbTransientValue();
        pojo.setPlainProperty("non transient");
        pojo.setPropertyTransient("TRANSIENT");
        pojo.setGetterTransient("Getter transient value");
        pojo.setSetterTransient("Setter transient value");
        pojo.setGetterAndPropertyTransient("Getter and property transient value");
        pojo.setSetterAndPropertyTransient("Setter and property transient value");
        pojo.setSetterAndGetterTransient("Setter and getter transient value");
        pojo.setSetterAndGetterAndPropertyTransient("Setter and getter and property transient value");

        assertEquals("{\"plainProperty\":\"non transient\",\"setterTransient\":\"Setter transient value\"}", jsonb.toJson(pojo));
    }

    @Test
    public void testJsonbTransientPropertyDeserialize() {
        JsonbTransientValue result = jsonb.fromJson("{\"plainProperty\":\"plainProperty value\"," +
                        "\"propertyTransient\":\"TRANSIENT\"," +
                        "\"getterTransient\":\"Getter transient value\"," +
                        "\"setterTransient\":\"Setter transient value\"," +
                        "\"getterAndPropertyTransient\":\"Getter and property transient value\"," +
                        "\"setterAndPropertyTransient\":\"Setter and property transient value\"," +
                        "\"setterAndGetterTransient\":\"Setter and getter transient value\"," +
                        "\"setterAndGetterAndPropertyTransient\":\"Setter and getter and property transient value\"" +
                        "}"
                , JsonbTransientValue.class);

        assertEquals("plainProperty value", result.getPlainProperty());
        assertNull(result.getPropertyTransient());
        assertEquals("Getter transient value", result.getGetterTransient());
        assertNull(result.getSetterTransient());
        assertNull(result.getGetterAndPropertyTransient());
        assertNull(result.getSetterAndPropertyTransient());
        assertNull(result.getSetterAndGetterTransient());
        assertNull(result.getSetterAndGetterAndPropertyTransient());
    }

    @Test
    public void testTransientAcceptableAnnotationCombinationsSerialization() throws Exception {
        JsonbTransientAcceptableAnnotationCollision pojo = new JsonbTransientAcceptableAnnotationCollision();
        pojo.setAnnotatedPropertyTransientGetter("transient value");
        pojo.setAnnotatedPropertyTransientSetter("Annotated property transient setter");
        pojo.setAnnotatedPropertyTransientGetterAndSetter("transient value");

        assertEquals("{\"annotated_property_transient_setter\":\"Annotated property transient setter\"}", jsonb.toJson(pojo));
    }

    @Test
    public void testTransientAcceptableAnnotationCombinationsDeserialization() throws Exception {
        JsonbTransientAcceptableAnnotationCollision result = jsonb.fromJson(
                "{\"annotated_property_transient_setter\":\"Annotated property transient setter\"," +
                        "\"annotated_property_transient_getter\":\"Annotated property transient getter\"," +
                        "\"annotated_property_transient_getter_and_setter\":\"Annotated property transient getter and setter\"},",
                JsonbTransientAcceptableAnnotationCollision.class);

        assertEquals("Annotated property transient getter", result.getAnnotatedPropertyTransientGetter());
        assertNull(result.getAnnotatedPropertyTransientSetter());
        assertNull(result.getAnnotatedPropertyTransientGetterAndSetter());
    }

    @Test
    public void testTransientCollidesOnProperty() throws Exception {
        JsonbTransientCollisionOnProperty pojo = new JsonbTransientCollisionOnProperty();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }

    @Test
    public void testTransientCollidesOnGetter() throws Exception {
        JsonbTransientCollisionOnGetter pojo = new JsonbTransientCollisionOnGetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndGetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndGetter pojo = new JsonbTransientCollisionOnPropertyAndGetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }

    @Test
    public void testTransientCollidesOnSetter() throws Exception {
        JsonbTransientCollisionOnSetter pojo = new JsonbTransientCollisionOnSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndSetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndSetter pojo = new JsonbTransientCollisionOnPropertyAndSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndGetterAndSetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndGetterAndSetter pojo = new JsonbTransientCollisionOnPropertyAndGetterAndSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith(String.format("JsonbTransient annotation collides with %s", JsonbProperty.class)));
        }
    }
}
