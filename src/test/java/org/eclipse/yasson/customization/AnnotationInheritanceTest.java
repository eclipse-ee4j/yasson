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

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.InheritedAnnotationsPojo;

/**
 * @author Roman Grigoriadi
 */
public class AnnotationInheritanceTest {

    @Test
    public void testAnnotationInheritance() {
        InheritedAnnotationsPojo pojo = new InheritedAnnotationsPojo();
        String expectedJson = "{}";
        assertEquals(expectedJson, defaultJsonb.toJson(pojo));

        InheritedAnnotationsPojo result = defaultJsonb.fromJson("{\"renamedProperty\":\"abc\"}", InheritedAnnotationsPojo.class);
        assertEquals("abc", result.property);
    }
}
