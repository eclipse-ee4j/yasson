/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
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

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertCreatedInstanceContainsAllParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestAsserts.assertParameters;
import static org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.constructorsOf;

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithPublicNoArgAndAnnotatedPrivateConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithPublicNoArgAndAnnotatedProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithTwoConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithoutAnnotatedConstructor;
import org.eclipse.yasson.internal.model.JsonbCreator;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    private final JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());
    private final AnnotationFinder constructorPropertiesFinder = AnnotationFinder.findConstructorProperties();

    /**
     * class under test.
     */
    private ConstructorPropertiesAnnotationIntrospector instrospector = new ConstructorPropertiesAnnotationIntrospector(jsonbContext, constructorPropertiesFinder);

    @Test
    public void testObjectShouldBeCreateableFromConstructorPropertiesAnnotatedConstructor() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithConstructorPropertiesAnnotation.class));
        assertParameters(ObjectWithConstructorPropertiesAnnotation.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithConstructorPropertiesAnnotation.example(), creator);
    }

    @Test
    public void testShouldAlsoWorkWithStaticFactoryMethodAndPredefinedAnnotationFinder() {
        instrospector = ConstructorPropertiesAnnotationIntrospector.forContext(jsonbContext);
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithConstructorPropertiesAnnotation.class));
        assertNotNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoCreatorAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithoutAnnotatedConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereIsNoConstructorPropertiesAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithJsonbCreatorAnnotatedConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testNullShouldBeReturnedWhenThereAreMoreThanOneConstructorPropertiesAnnotation() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithTwoConstructorPropertiesAnnotation.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessiblePrivateConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithPublicNoArgAndAnnotatedPrivateConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessiblePackageProtectedConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithPublicNoArgAndAnnotatedPackageProtectedConstructor.class));
        assertNull(creator);
    }

    @Test
    public void testAnnotatedInaccessibleProtectedConstructorShouldBeIgnored() {
        JsonbCreator creator = instrospector.getCreator(constructorsOf(ObjectWithPublicNoArgAndAnnotatedProtectedConstructor.class));
        assertNull(creator);
    }
}
