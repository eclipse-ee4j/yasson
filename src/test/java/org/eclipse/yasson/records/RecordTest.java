/*
 * Copyright (c) 2021, 2025 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.records;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.Jsonbs;
import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordTest {

    @Test
    public void testRecordProcessing() {
        Car car = new Car("skoda", "green");
        String expected = "{\"colorChanged\":\"green\",\"typeChanged\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        Car deserialized = Jsonbs.defaultJsonb.fromJson(expected, Car.class);
        assertEquals(car, deserialized);
    }

    @Test
    public void testRecordProcessingWithoutJsonbProperties() {
        CarWithoutAnnotations car = new CarWithoutAnnotations("skoda", "green");
        String expected = "{\"color\":\"green\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertEquals(expected, json);
        CarWithoutAnnotations deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithoutAnnotations.class);
        assertEquals(car, deserialized);
    }

    @Test
    public void testRecordProcessingWithExtraMethod() {
        CarWithExtraMethod car = new CarWithExtraMethod("skoda", "green");
        String expected = "{\"color\":\"green\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        CarWithExtraMethod deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithExtraMethod.class);
        assertThat(deserialized, is(car));
    }

    @Test
    public void testRecordMultipleConstructors() {
        CarWithMultipleConstructors car = new CarWithMultipleConstructors("skoda");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        JsonbException jsonbException = assertThrows(JsonbException.class,
                                                     () -> Jsonbs.defaultJsonb.fromJson(expected,
                                                                                        CarWithMultipleConstructors.class));
        String expectedMessage = Messages.getMessage(MessageKeys.RECORD_MULTIPLE_CONSTRUCTORS, CarWithMultipleConstructors.class);
        assertThat(jsonbException.getMessage(), is(expectedMessage));
    }

    @Test
    public void testRecordMultipleConstructorsWithJsonbCreator() {
        CarWithMultipleConstructorsAndCreator car = new CarWithMultipleConstructorsAndCreator("skoda");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        CarWithMultipleConstructorsAndCreator deserialized =  Jsonbs.defaultJsonb
                .fromJson(expected, CarWithMultipleConstructorsAndCreator.class);
        assertThat(car, is(deserialized));
    }

    @Test
    public void testRecordJsonbCreator() {
        CarWithCreator car = new CarWithCreator("skoda", "red");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        CarWithCreator deserialized = Jsonbs.defaultJsonb.fromJson(expected, CarWithCreator.class);
        assertThat(deserialized, is(car));
    }

    @Test
    public void testRecordWithDefaultConstructor() {
        CarWithDefaultConstructor car = new CarWithDefaultConstructor("skoda", "red");
        String expected = "{\"color\":\"red\",\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        assertThrows(JsonbException.class, () -> Jsonbs.defaultJsonb.fromJson(expected, CarWithDefaultConstructor.class));
    }

    @Test
    public void testRecordWithGenerics() {
        CarWithGenerics<Color> car = new CarWithGenerics<>("skoda", new Color("green", "#00FF00"));
        String expected = "{\"color\":{\"code\":\"#00FF00\",\"name\":\"green\"},\"type\":\"skoda\"}";

        String json = Jsonbs.defaultJsonb.toJson(car);
        assertThat(json, is(expected));
        
        CarWithGenerics<Color> deserialized = Jsonbs.defaultJsonb
                .fromJson(expected, new TestTypeToken<CarWithGenerics<Color>>() {}.getType());
        assertThat(deserialized, is(car));  
    }
    // -----------------------------------------------------------------
    // isVirtualAccessorMethod — virtual (computed) record attributes
    // -----------------------------------------------------------------

    /**
     * A record's zero-parameter, non-void method whose name does not match any
     * component must be serialized as a JSON property (virtual attribute).
     */
    @Test
    public void testRecordVirtualAttributeIsIncludedInJson() {
        RecordWithVirtualAttributes record = new RecordWithVirtualAttributes("skoda", "green");
        String json = Jsonbs.defaultJsonb.toJson(record);

        assertThat(json, containsString("\"displayName\":\"skoda (green)\""));
        assertThat(json, containsString("\"componentCount\":2"));
    }

    /**
     * The standard record components must still appear alongside the virtual attributes.
     */
    @Test
    public void testRecordVirtualAttributeDoesNotSuppressComponents() {
        RecordWithVirtualAttributes record = new RecordWithVirtualAttributes("skoda", "green");
        String json = Jsonbs.defaultJsonb.toJson(record);

        assertThat(json, containsString("\"type\":\"skoda\""));
        assertThat(json, containsString("\"color\":\"green\""));
    }

    /**
     * {@code hashCode()} is explicitly excluded by {@code isVirtualAccessorMethod}
     * and must not appear in the serialized JSON.
     */
    @Test
    public void testRecordHashCodeMethodIsNotIncludedInJson() {
        RecordWithVirtualAttributes record = new RecordWithVirtualAttributes("skoda", "green");
        String json = Jsonbs.defaultJsonb.toJson(record);

        assertThat(json, not(containsString("\"hashCode\"")));
    }

    /**
     * {@code toString()} is explicitly excluded by {@code isVirtualAccessorMethod}
     * and must not appear in the serialized JSON.
     */
    @Test
    public void testRecordToStringMethodIsNotIncludedInJson() {
        RecordWithVirtualAttributes record = new RecordWithVirtualAttributes("skoda", "green");
        String json = Jsonbs.defaultJsonb.toJson(record);

        assertThat(json, not(containsString("\"toString\"")));
    }

    /**
     * Virtual attributes are read-only; deserialization of the underlying components
     * must still work correctly even when virtual attribute keys are present in the JSON.
     * Unknown virtual attribute keys are simply ignored on the way in.
     */
    @Test
    public void testRecordVirtualAttributeIsIgnoredDuringDeserialization() {
        String json = "{\"type\":\"skoda\",\"color\":\"green\","
                + "\"displayName\":\"skoda (green)\",\"componentCount\":2}";
        RecordWithVirtualAttributes deserialized =
                Jsonbs.defaultJsonb.fromJson(json, RecordWithVirtualAttributes.class);

        assertThat(deserialized.type(), is("skoda"));
        assertThat(deserialized.color(), is("green"));
    }

    /**
     * A virtual accessor method whose name starts with {@code get} must NOT have its
     * prefix stripped when serialized from a record.  JavaBean name-mangling only applies
     * to regular classes; for records the raw method name is used as the JSON key.
     * <p>
     * So {@code getDisplayName()} on a record must serialize as {@code "getDisplayName"},
     * not as {@code "displayName"}.
     */
    @Test
    public void testRecordGetterStyleVirtualAttributeIsNotStripped() {
        RecordWithGetterStyleVirtualAttribute record =
                new RecordWithGetterStyleVirtualAttribute("skoda", "green");
        String json = Jsonbs.defaultJsonb.toJson(record);

        assertThat(json, containsString("\"getDisplayName\":\"skoda (green)\""));
        assertThat(json, not(containsString("\"displayName\"")));
    }
}
