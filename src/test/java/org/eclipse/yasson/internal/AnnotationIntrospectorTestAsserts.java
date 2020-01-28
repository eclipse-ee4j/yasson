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

import org.eclipse.yasson.internal.AnnotationIntrospectorTestFixtures.ProvidesParameterRepresentation;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

@Disabled
class AnnotationIntrospectorTestAsserts {

    /**
     * Creates a new Instance of the Object using the given {@link JsonbCreator} and compares the result with the given
     * expected Object. The comparison is done by comparing both {@link ProvidesParameterRepresentation#asParameters()}.
     * 
     * @param expected {@link ProvidesParameterRepresentation}
     * @param creator  {@link JsonbCreator}
     */
    public static <T extends ProvidesParameterRepresentation> void assertCreatedInstanceContainsAllParameters(T expected, JsonbCreator creator) {
        assertNotNull(creator, JsonbCreator.class.getSimpleName() + " expected");
        @SuppressWarnings("unchecked")
        T created = (T) creator.call(expected.asParameters(), expected.getClass());
        assertArrayEquals(expected.asParameters(), created.asParameters());
    }

    /**
     * Compares the expected parameters with the meta-model contained in the {@link JsonbCreator}.
     * 
     * @param expectedParameters {@link Map} with {@link String}-Key and {@link Type}-Value
     * @param creator            CreatorModel
     */
    public static void assertParameters(Map<String, Type> expectedParameters, JsonbCreator creator) {
        assertNotNull(creator, JsonbCreator.class.getSimpleName() + " expected");
        for (Entry<String, Type> parameter : expectedParameters.entrySet()) {
            CreatorModel creatorModel = creator.findByName(parameter.getKey());
            assertEquals(parameter.getKey(), creatorModel.getName());
            assertEquals(parameter.getValue(), creatorModel.getType());
        }
    }
}
