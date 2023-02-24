/*
 * Copyright (c) 2015, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.defaultmapping.modifiers.model.FieldModifiersClass;
import org.eclipse.yasson.defaultmapping.modifiers.model.MethodModifiersClass;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.spi.JsonProvider;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Test for ClassParser component.
 *
 * @author Roman Grigoriadi
 */
public class ClassParserTest {
    private static final JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());
    private static final ClassParser classParser = new ClassParser(jsonbContext);
    private static final AnnotationIntrospector introspector = new AnnotationIntrospector(jsonbContext);

    @Test
    public void testDefaultMappingFieldModifiers() {
        final JsonbAnnotatedElement<Class<?>> clsElement = introspector.collectAnnotations(FieldModifiersClass.class);
        ClassModel model = new ClassModel(FieldModifiersClass.class, introspector.introspectCustomization(clsElement,
                                                                                                          ClassCustomization.empty(), jsonbContext.getConfigProperties().getPropertyNamingStrategy()), null, null);
        classParser.parseProperties(model, clsElement);
        assertTrue(model.getPropertyModel("finalString").isReadable());
        assertFalse(model.getPropertyModel("finalString").isWritable());
        assertFalse(model.getPropertyModel("staticString").isReadable());
        assertFalse(model.getPropertyModel("staticString").isWritable());
        assertFalse(model.getPropertyModel("transientString").isReadable());
        assertFalse(model.getPropertyModel("transientString").isWritable());
    }

    @Test
    public void testDefaultMappingMethodModifiers() {
        final JsonbAnnotatedElement<Class<?>> clsElement = introspector.collectAnnotations(MethodModifiersClass.class);
        ClassModel model = new ClassModel(FieldModifiersClass.class, introspector.introspectCustomization(clsElement,
                                                                                                          ClassCustomization.empty(), jsonbContext.getConfigProperties().getPropertyNamingStrategy()), null, null);
        classParser.parseProperties(model, clsElement);
        assertFalse(model.getPropertyModel("publicFieldWithPrivateMethods").isReadable());
        assertFalse(model.getPropertyModel("publicFieldWithPrivateMethods").isWritable());
        assertTrue(model.getPropertyModel("publicFieldWithoutMethods").isReadable());
        assertTrue(model.getPropertyModel("publicFieldWithoutMethods").isWritable());
        assertTrue(model.getPropertyModel("getterWithoutFieldValue").isReadable());
        assertTrue(model.getPropertyModel("getterWithoutFieldValue").isWritable());


        MethodModifiersClass object = new MethodModifiersClass();
        final AtomicReference<String> accepted = new AtomicReference<>();
        Consumer<String> withoutFieldConsumer = accepted::set;
        object.setSetterWithoutFieldConsumer(withoutFieldConsumer);
        model.getPropertyModel("getterWithoutFieldValue").setValue(object, "ACCEPTED_VALUE");
        assertEquals("ACCEPTED_VALUE", accepted.get());
    }
}
