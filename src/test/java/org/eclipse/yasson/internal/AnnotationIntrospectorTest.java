/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedFactoryMethod;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithJsonbCreatorAnnotatedProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithTwoJsonbCreatorAnnotatedSpots;
import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ObjectWithoutAnnotatedConstructor;
import org.eclipse.yasson.internal.model.JsonbCreator;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import javax.json.spi.JsonProvider;

/**
 * Tests the {@link AnnotationIntrospector}.
 * 
 * @see AnnotationIntrospectorTestFixtures
 * @see AnnotationIntrospectorTestAsserts
 */
public class AnnotationIntrospectorTest {
    private final JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());

    /**
     * class under test.
     */
    private AnnotationIntrospector instrospector = new AnnotationIntrospector(jsonbContext);

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedConstructor() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedConstructor.class);
        assertParameters(ObjectWithJsonbCreatorAnnotatedConstructor.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedConstructor.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethod() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedFactoryMethod.class);
        assertParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedFactoryMethod.example(), creator);
    }

    @Test
    public void testObjectShouldBeCreateableFromJsonbAnnotatedStaticFactoryMethodIgnoringConstructorPorperties() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation.class);
        assertParameters(ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAndConstructorPropertiesAnnotation.example(), creator);
    }

    @Test
    public void testJsonbAnnotatedProtectedConstructorLeadsToAnException() {
        assertThrows(JsonbException.class, () -> {
	        JsonbCreator creator = instrospector.getCreator(ObjectWithJsonbCreatorAnnotatedProtectedConstructor.class);
	        assertCreatedInstanceContainsAllParameters(ObjectWithJsonbCreatorAnnotatedProtectedConstructor.example(), creator);
        });
    }

    // TODO Under discussion: https://github.com/eclipse-ee4j/yasson/issues/326
    @Disabled
    @Test
    public void testNoArgConstructorShouldBePreferredOverUnusableJsonbAnnotatedProtectedConstructor() {
        JsonbCreator creator = instrospector.getCreator(ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor.class);
        assertParameters(ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor.parameters(), creator);
        assertCreatedInstanceContainsAllParameters(ObjectWithNoArgAndJsonbCreatorAnnotatedProtectedConstructor.example(), creator);
    }

    @Test
    public void testMoreThanOneAnnotatedCreatorMethodShouldLeadToAnException() {
        assertThrows(JsonbException.class, 
        			() -> instrospector.getCreator(ObjectWithTwoJsonbCreatorAnnotatedSpots.class),
        			() -> "More than one @" + JsonbCreator.class.getSimpleName());
    }

    @Test
    public void testCreatorShouldBeNullOnMissingConstructorAnnotation() {
        assertNull(instrospector.getCreator(ObjectWithoutAnnotatedConstructor.class));
    }

}
