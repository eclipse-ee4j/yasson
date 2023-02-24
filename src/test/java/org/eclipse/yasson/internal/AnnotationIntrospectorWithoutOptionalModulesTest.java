/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.spi.JsonProvider;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedFactoryMethod;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithoutAnnotatedConstructor;
import org.eclipse.yasson.internal.model.JsonbCreator;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link AnnotationIntrospector} with missing optional module "java.deskop", <br>
 * that contains the ConstructorProperties-Annotation.
 * <p>
 * Requires --limit-modules java.base,java.logging,java.sql (to exclude java.desktop) to work. <br>
 * See pom.xml surefire plugin configuration.
 * 
 * @see AnnotationIntrospectorTestFixtures
 */
public class AnnotationIntrospectorWithoutOptionalModulesTest {

    /**
     * class under test.
     */
    private static final AnnotationIntrospector instrospector = new AnnotationIntrospector(new JsonbContext(new JsonbConfig(), JsonProvider.provider()));
    private final PropertyNamingStrategy propertyNamingStrategy = propertyName -> propertyName;

    @Test
    public void testNoConstructorPropertiesAnnotationWithoutOptionalModules() {
        String className = "java.beans.ConstructorProperties";
        try {
            Class.forName(className);
            fail("Class [" + className + "] should not be available for this test.");
        } catch (ClassNotFoundException e) {
            // OK, as expected
        }
    }

    @Test
    public void testCreatorShouldBeNullOnMissingConstructorAnnotation() {
        assertNull(instrospector.getCreator(ObjectWithoutAnnotatedConstructor.class, propertyNamingStrategy));
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedConstructorWithoutOptionalModules() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedConstructor.class, propertyNamingStrategy);
        assertParameters(ObjectWithJsonbCreatorAnnotatedConstructor.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedConstructor.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethodWithoutOptionalModules() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedFactoryMethod.class, propertyNamingStrategy);
        assertParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.example(), creator);
    }
}
