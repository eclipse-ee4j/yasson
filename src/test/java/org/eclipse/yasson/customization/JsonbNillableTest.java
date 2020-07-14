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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbProperty;

import static org.junit.jupiter.api.Assertions.*;

import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.JsonbNillableClassSecondLevel;
import org.eclipse.yasson.customization.model.JsonbNillableOverriddenWithJsonbProperty;
import org.eclipse.yasson.customization.model.JsonbNillableOverridesClass;
import org.eclipse.yasson.customization.model.JsonbNillableOverridesInterface;
import org.eclipse.yasson.customization.model.JsonbNillableValue;
import org.eclipse.yasson.customization.model.packagelevelannotations.JsonbNillablePackageLevel;
import org.eclipse.yasson.customization.model.packagelevelannotations.PackageLevelOverriddenWithClassLevel;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;

/**
 * Tests a {@link jakarta.json.bind.annotation.JsonbNillable} annotation.
 * @author Roman Grigoriadi
 */
public class JsonbNillableTest {

    @Test
    public void testJsonbNillable() {
        JsonbNillableValue pojo = new JsonbNillableValue();
        assertEquals("{\"nillableField\":null}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testJsonbNillableOverriddenWithJsonbProperty() {
        JsonbNillableOverriddenWithJsonbProperty pojo = new JsonbNillableOverriddenWithJsonbProperty();
        assertEquals("{}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testPackageLevelNillable() {
        JsonbNillablePackageLevel pojo = new JsonbNillablePackageLevel();
        assertEquals("{\"packageLevelNillableField\":null}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testPackageLevelOverriddenWithClassLevel() {
        PackageLevelOverriddenWithClassLevel pojo = new PackageLevelOverriddenWithClassLevel();
        assertEquals("{}", defaultJsonb.toJson(pojo));
    }

    /**
     * Tests inheritance of annotations from interfaces.
     */
    @Test
    public void testNillableInheritFromInterface() throws Exception {
        JsonbNillableClassSecondLevel pojo = new JsonbNillableClassSecondLevel();
        assertEquals("{\"classNillable\":null}", defaultJsonb.toJson(pojo));
    }

    @Test
    public void testInheritanceOverride() throws Exception {
        JsonbNillableOverridesInterface overridesInterface = new JsonbNillableOverridesInterface();
        assertEquals("{}", defaultJsonb.toJson(overridesInterface));

        JsonbNillableOverridesClass overridesClass = new JsonbNillableOverridesClass();
        assertEquals("{}", defaultJsonb.toJson(overridesClass));
    }

    @Test
    public void testNillableInConfig() {
        String jsonString = nullableJsonb.toJson(new ScalarValueWrapper<String>(){});
        assertEquals("{\"value\":null}", jsonString);
    }
    
    public static class PrimitiveNullBoolean {

        @JsonbProperty(nillable = true)
        private Boolean someBoolean;

        void setSomeBoolean(boolean value) { // note that value is a primitive boolean
            // leaving this empty, exception will be thrown before Yasson gets here.
        }
    }
    
    /**
     * Test for issue https://github.com/eclipse-ee4j/yasson/issues/399
     */
    @Test
    public void testNillableSomeBoolean() {
    	Jsonb jsonb = JsonbBuilder.create();
        String input = "{\"someBoolean\": null}";
        PrimitiveNullBoolean deserialized = jsonb.fromJson(input, PrimitiveNullBoolean.class);
        assertNull(deserialized.someBoolean);
    }
}
