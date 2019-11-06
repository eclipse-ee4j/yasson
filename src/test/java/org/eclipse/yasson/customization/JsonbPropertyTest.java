/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.JsonbPropertyName;
import org.eclipse.yasson.customization.model.JsonbPropertyNameCollision;
import org.eclipse.yasson.customization.model.JsonbPropertyNillable;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.config.PropertyNamingStrategy;

/**
 * Tests parsing of {@link javax.json.bind.annotation.JsonbProperty} test.
 * @author Roman Grigoriadi
 */
public class JsonbPropertyTest {

    @Test
    public void testPropertyName() throws Exception {

        JsonbPropertyName pojo = new JsonbPropertyName();
        pojo.setFieldAnnotatedName("FIELD_ANNOTATED");
        pojo.setMethodAnnotName("METHOD_ANNOTATED");
        pojo.setFieldOverriddenWithMethodAnnot("OVERRIDDEN_GETTER");

        assertEquals("{\"fieldAnnotatedNameCustomized\":\"FIELD_ANNOTATED\",\"getterAnnotatedName\":\"METHOD_ANNOTATED\",\"getterOverriddenName\":\"OVERRIDDEN_GETTER\"}",
                defaultJsonb.toJson(pojo));

        String toUnmarshall = "{\"fieldAnnotatedNameCustomized\":\"FIELD_ANNOTATED\",\"setterOverriddenName\":\"OVERRIDDEN_GETTER\",\"setterAnnotatedName\":\"METHOD_ANNOTATED\"}";
        JsonbPropertyName result = defaultJsonb.fromJson(toUnmarshall, JsonbPropertyName.class);
        assertEquals("FIELD_ANNOTATED", result.getFieldAnnotatedName());
        assertEquals("METHOD_ANNOTATED", result.getMethodAnnotName());
        assertEquals("OVERRIDDEN_GETTER", result.getFieldOverriddenWithMethodAnnot());
    }

    @Test
    public void testNameCollision() {
        JsonbPropertyNameCollision nameCollisionPojo = new JsonbPropertyNameCollision();
        tryClash(() -> defaultJsonb.toJson(nameCollisionPojo));
        tryClash(() -> defaultJsonb.fromJson("{}", JsonbPropertyNameCollision.class));
    }



    private static void tryClash(Runnable clashCommand) {
        try {
            clashCommand.run();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Property pojoName clashes with property pojoNameCollision"));
        }
    }


    @Test
    public void testPropertyNillable() {
        JsonbPropertyNillable pojo = new JsonbPropertyNillable();
        assertEquals("{\"nullField\":null}", defaultJsonb.toJson(pojo));
    }
    
    @Test
    public void testRenamedGetterAndSetter() {
        // Reported in issue: https://github.com/eclipse-ee4j/yasson/issues/355
        final RenamedGetterAndSetter b = new RenamedGetterAndSetter();
        b.setTest("hi");
        final String h = defaultJsonb.toJson(b);
        final String expectedJson = "{\"apple\":\"hi\"}";
        assertEquals(expectedJson, h); //this passes
        final RenamedGetterAndSetter b1 = defaultJsonb.fromJson(h, RenamedGetterAndSetter.class);
        
        assertEquals("hi", b1.getTest()); //this fails but passes in 1.0.4
    }
    
    public static class RenamedGetterAndSetter {
        private String apple;
        
        @JsonbProperty("apple")
        public String getTest() {
            return apple;
        }

        @JsonbProperty("apple")
        public void setTest(String test) {
            this.apple = test;
        }
    }

    /**
     * In this test getter / setter doesn't match to field "doi", because declared by javabean convention.
     * When model is parsed there are to properties:
     * Property for private field "doi", without customization. This property is not readable because field
     * is private without getter / setter.
     * And property "DOI" - getter / setter without a field with customization on getter renaming it to doi
     * in serialized document.
     *
     * Because first of those properties is not readable this should not raise naming clash error.
     */
    @Test
    public void testNonConflictingProperties() {
        NonConflictingProperties nonConflictingProperties = new NonConflictingProperties();
        nonConflictingProperties.setDOI("DOI value");

        String json = defaultJsonb.toJson(nonConflictingProperties);
        assertEquals("{\"doi\":\"DOI value\"}", json);

        NonConflictingProperties result = defaultJsonb.fromJson("{\"DOI\":\"DOI value\"}", NonConflictingProperties.class);
        assertEquals("DOI value", result.getDOI());
    }

    /**
     * Same problem as above but now field is public, so clash takes place.
     */
    @Test
    public void testConflictingProperties() {
        ConflictingProperties conflictingProperties = new ConflictingProperties();
        conflictingProperties.setDOI("DOI value");
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());

        try {
            jsonb.toJson(conflictingProperties);
            fail();
        } catch (JsonbException e) {
            if (!e.getMessage().equals("Property DOI clashes with property doi by read or write name in class org.eclipse.yasson.customization.JsonbPropertyTest$ConflictingProperties.")) {
                throw e;
            }
        }
    }

    /**
     * Tests clash with property altered by naming strategy.
     */
    @Test
    public void testConflictingWithUpperCamelStrategy() {
        ConflictingWithUpperCamelStrategy pojo = new ConflictingWithUpperCamelStrategy();
        pojo.setDOI("DOI value");

        String json = defaultJsonb.toJson(pojo);
        assertEquals("{\"Doi\":\"DOI value\",\"doi\":\"DOI value\"}", json);

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
                .withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE));

        try {
            jsonb.toJson(pojo);
            fail();
        } catch (JsonbException e) {
            if (!e.getMessage().equals("Property DOI clashes with property doi by read or write name in class org.eclipse.yasson.customization.JsonbPropertyTest$ConflictingWithUpperCamelStrategy.")) {
                throw e;
            }
        }

    }
    
    @Test
    public void testConflictingWithLowercaseStrategy() {
    	// scenario raised by user here: https://github.com/eclipse-ee4j/yasson/issues/296
    	Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES));
    	assertEquals("{\"url\":\"http://foo.com\"}", 
    			jsonb.toJson(new ConflictingIfLowercase()));
    }
    
    public static class ConflictingIfLowercase {
    	private String url = "foo.com";
    	
    	public String getURL() {
    		return "http://" + url;
    	}
    }

    public static class NonConflictingProperties {
        private String doi;

        @JsonbProperty("doi")
        public String getDOI() {
            return doi;
        }
        public void setDOI(String doi) {
            this.doi = doi;
        }
    }

    public static class ConflictingProperties {
        public String doi;

        @JsonbProperty("doi")
        public String getDOI() {
            return doi;
        }
        public void setDOI(String doi) {
            this.doi = doi;
        }
    }

    public static class ConflictingWithUpperCamelStrategy {
        public String doi;

        @JsonbProperty("Doi")
        public String getDOI() {
            return doi;
        }
        public void setDOI(String doi) {
            this.doi = doi;
        }
    }
}
