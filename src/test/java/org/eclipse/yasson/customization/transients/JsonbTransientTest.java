/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.customization.transients;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.transients.models.*;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbTransient;

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

    // -------------------------------------------------------------------------
    // Record variants
    // -------------------------------------------------------------------------

    /**
     * Serialization: verifies that {@link JsonbTransient} on a record component,
     * on an accessor method, on both together, and on a virtual (non-component)
     * accessor method each suppress the property from the JSON output, while an
     * unannotated component remains visible.
     */
    @Test
    public void testJsonbTransientRecordSerialize() {
        JsonbTransientRecord record = new JsonbTransientRecord(
                "non transient",
                "component transient value",
                "accessor transient value",
                "component and accessor transient value"
        );

        assertEquals("{\"plainProperty\":\"non transient\"}", defaultJsonb.toJson(record));
    }

    /**
     * Deserialization: verifies the correct transient semantics for records:
     * <ul>
     *   <li>{@link JsonbTransient} on a record <b>component</b> (backing field) suppresses
     *       both serialization and deserialization — the JSON value is not bound.</li>
     *   <li>{@link JsonbTransient} on the <b>accessor method</b> only suppresses serialization;
     *       the component is still populated during deserialization because the accessor
     *       annotation is the equivalent of a getter annotation on a regular class.</li>
     *   <li>When present on both component and accessor, the component annotation governs
     *       and the value is not bound.</li>
     * </ul>
     */
    @Test
    public void testJsonbTransientRecordDeserialize() {
        JsonbTransientRecord result = defaultJsonb.fromJson(
                "{\"plainProperty\":\"plainProperty value\"," +
                "\"componentTransient\":\"component transient value\"," +
                "\"accessorTransient\":\"accessor transient value\"," +
                "\"componentAndAccessorTransient\":\"component and accessor transient value\"," +
                "\"virtualAttributeTransient\":\"virtual transient value\"" +
                "}",
                JsonbTransientRecord.class);

        assertEquals("plainProperty value", result.plainProperty());
        assertNull(result.componentTransient());
        assertEquals("accessor transient value", result.accessorTransient());
        assertNull(result.componentAndAccessorTransient());
    }

    /**
     * Verifies that all meaningful combinations of {@link JsonbTransient} placement on fields,
     * getters, and setters are correctly honoured when deserialization goes through an explicit
     * {@link jakarta.json.bind.annotation.JsonbCreator} constructor.
     *
     * <p>Combinations verified:
     * <ul>
     *   <li>field only          → write-transient: JSON value ignored, constructor gets {@code null}</li>
     *   <li>getter only         → read-transient only: JSON value still bound (write allowed)</li>
     *   <li>setter only         → write-transient: JSON value ignored, constructor gets {@code null}</li>
     *   <li>field + getter      → write-transient: JSON value ignored</li>
     *   <li>field + setter      → write-transient: JSON value ignored</li>
     *   <li>getter + setter     → write-transient: JSON value ignored</li>
     *   <li>field + getter + setter → write-transient: JSON value ignored</li>
     * </ul>
     */
    @Test
    public void testJsonbTransientWithCreatorDeserialize() {
        JsonbTransientWithCreator result = defaultJsonb.fromJson(
                "{" +
                "\"plainProperty\":\"plain value\"," +
                "\"fieldTransient\":\"field transient value\"," +
                "\"getterTransient\":\"getter transient value\"," +
                "\"setterTransient\":\"setter transient value\"," +
                "\"fieldAndGetterTransient\":\"field+getter transient value\"," +
                "\"fieldAndSetterTransient\":\"field+setter transient value\"," +
                "\"getterAndSetterTransient\":\"getter+setter transient value\"," +
                "\"allTransient\":\"all transient value\"" +
                "}",
                JsonbTransientWithCreator.class);

        // Not transient — always populated
        assertEquals("plain value", result.plainProperty);

        // Write-transient cases: JSON value must be discarded; constructor slot receives null
        assertNull(result.fieldTransient,          "@JsonbTransient on field: should suppress deserialization via @JsonbCreator");
        assertNull(result.setterTransient,         "@JsonbTransient on setter: should suppress deserialization via @JsonbCreator");
        assertNull(result.fieldAndGetterTransient, "@JsonbTransient on field+getter: should suppress deserialization via @JsonbCreator");
        assertNull(result.fieldAndSetterTransient, "@JsonbTransient on field+setter: should suppress deserialization via @JsonbCreator");
        assertNull(result.getterAndSetterTransient,"@JsonbTransient on getter+setter: should suppress deserialization via @JsonbCreator");
        assertNull(result.allTransient,            "@JsonbTransient on field+getter+setter: should suppress deserialization via @JsonbCreator");

        // Getter-only transient: read-transient only, write is still allowed
        assertEquals("getter transient value", result.getterTransient,
                "@JsonbTransient on getter only: deserialization should still be allowed via @JsonbCreator");
    }

    /**
     * Verifies that the serialization side of {@link JsonbTransient} on a class with a
     * {@link jakarta.json.bind.annotation.JsonbCreator} constructor is also respected.
     */
    @Test
    public void testJsonbTransientWithCreatorSerialize() {
        JsonbTransientWithCreator instance = new JsonbTransientWithCreator(
                "plain value",
                "field transient value",
                "getter transient value",
                "setter transient value",
                "field+getter transient value",
                "field+setter transient value",
                "getter+setter transient value",
                "all transient value"
        );

        // Only plainProperty and setterTransient should appear:
        //   - setterTransient has @JsonbTransient only on setter → read-transient=false → serialized
        //   - getterTransient has @JsonbTransient on getter → read-transient=true → suppressed
        //   - everything else is fully transient
        assertEquals("{\"plainProperty\":\"plain value\",\"setterTransient\":\"setter transient value\"}",
                defaultJsonb.toJson(instance));
    }

    @Test
    public void testTransientGetterNoField() {
        TransientGetterNoField pojo = new TransientGetterNoField();
        assertEquals("{}", defaultJsonb.toJson(pojo));
    }

    /**
     * Tests that {@link JsonbTransient} declared on an abstract method in an interface or abstract
     * class is honoured when serializing a concrete subtype, including anonymous subclasses.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>An interface getter annotated with {@code @JsonbTransient} is excluded from the output</li>
     *   <li>An abstract-class getter annotated with {@code @JsonbTransient} is excluded from the output</li>
     *   <li>The same exclusion applies to concrete subclasses and anonymous subclasses</li>
     * </ul>
     *
     * @see <a href="https://github.com/eclipse-ee4j/yasson/issues/454">Issue #454</a>
     */
    @Test
    public void testJsonbTransientInheritedFromAbstractMethodInInterfaceAndClass() {
        final String EXPECTED = "{\"field2\":\"bbb\"}";
        assertEquals(EXPECTED, defaultJsonb.toJson(new TransientAbstractInterface() {
            @Override
            public String getField1() { return "aaa"; }
            @Override
            public String getField2() { return "bbb"; }
        }));
        assertEquals(EXPECTED, defaultJsonb.toJson(new TransientAbstractClass() {
            @Override
            public String getField1() { return "aaa"; }
            @Override
            public String getField2() { return "bbb"; }
        }));
        assertEquals(EXPECTED, defaultJsonb.toJson(new TransientAbstractClassImpl()));
        assertEquals(EXPECTED, defaultJsonb.toJson(new TransientAbstractClassImpl() {}));
    }

    /** Interface whose {@code getField1} getter is annotated {@link JsonbTransient}. */
    public static abstract class TransientAbstractClass {
        @JsonbTransient
        public abstract String getField1();

        public abstract String getField2();
    }

    /** Concrete implementation of {@link TransientAbstractClass}. */
    public static class TransientAbstractClassImpl extends TransientAbstractClass {
        @Override
        public String getField1() { return "aaa"; }

        @Override
        public String getField2() { return "bbb"; }
    }

    /** Abstract class whose {@code getField1} getter is annotated {@link JsonbTransient}. */
    public interface TransientAbstractInterface {
        @JsonbTransient
        String getField1();

        String getField2();
    }
}
