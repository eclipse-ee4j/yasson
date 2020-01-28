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

package org.eclipse.yasson.customization.transients;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.transients.models.*;
import jakarta.json.bind.JsonbException;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTransientTest {

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

        assertEquals("{\"plainProperty\":\"non transient\",\"setterTransient\":\"Setter transient value\"}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testJsonbTransientPropertyDeserialize() {
        JsonbTransientValue result = defaultJsonb.fromJson("{\"plainProperty\":\"plainProperty value\"," +
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
    public void testTransientCollidesOnProperty() throws Exception {
        JsonbTransientCollisionOnProperty pojo = new JsonbTransientCollisionOnProperty();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientCollidesOnGetter() throws Exception {
        JsonbTransientCollisionOnGetter pojo = new JsonbTransientCollisionOnGetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndGetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndGetter pojo = new JsonbTransientCollisionOnPropertyAndGetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientCollidesOnSetter() throws Exception {
        JsonbTransientCollisionOnSetter pojo = new JsonbTransientCollisionOnSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndSetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndSetter pojo = new JsonbTransientCollisionOnPropertyAndSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientCollidesOnPropertyAndGetterAndSetter() throws Exception {
        JsonbTransientCollisionOnPropertyAndGetterAndSetter pojo = new JsonbTransientCollisionOnPropertyAndGetterAndSetter();
        pojo.setTransientProperty("TRANSIENT");

        try {
        	defaultJsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("JsonbTransient annotation cannot be used with other jsonb annotations on the same property."));
        }
    }

    @Test
    public void testTransientGetterPlusJsonbPropertyField() {
    	assertThrows(JsonbException.class, () -> {
	        TransientGetterPlusCustomizationAnnotatedFieldContainer pojo = new TransientGetterPlusCustomizationAnnotatedFieldContainer();
	        defaultJsonb.toJson(pojo);
    	});
    }

    @Test
    public void testTransientSetterPlusJsonbPropertyField() {
    	assertThrows(JsonbException.class, () -> {
	        TransientSetterPlusCustomizationAnnotatedFieldContainer pojo = new TransientSetterPlusCustomizationAnnotatedFieldContainer();
	        defaultJsonb.toJson(pojo);
    	});
    }

    @Test
    public void testTransientSetterplusJsonbPropertyGetter() {
        TransientSetterPlusCustomizationAnnotatedGetterContainer pojo = new TransientSetterPlusCustomizationAnnotatedGetterContainer();
        assertEquals("{\"instance\":\"INSTANCE\"}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testTransientGetterNoField() {
        TransientGetterNoField pojo = new TransientGetterNoField();
        assertEquals("{}", defaultJsonb.toJson(pojo));
    }
}
