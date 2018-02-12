/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson.internal.model;

import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.model.customization.*;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * Parameter for creator constructor / method model.
 *
 * @author Roman Grigoriadi
 */
public class CreatorModel implements JsonBindingModel {

    private final String name;

    private final Type type;

    private final CreatorCustomization creatorCustomization;

    /**
     * Creates a new instance.
     *
     * @param name Parameter name
     * @param parameter constructor parameter
     * @param context jsonb context
     */
    public CreatorModel(String name, Parameter parameter, JsonbContext context) {
        this.name = name;
        this.type = parameter.getType();

        AnnotationIntrospector annotationIntrospector = context.getAnnotationIntrospector();

        JsonbAnnotatedElement<Parameter> annotated = new JsonbAnnotatedElement<>(parameter);
        JsonbNumberFormatter constructorNumberFormatter = context.getAnnotationIntrospector().getConstructorNumberFormatter(annotated);
        JsonbDateFormatter constructorDateFormatter = context.getAnnotationIntrospector().getConstructorDateFormatter(annotated);
        final JsonbAnnotatedElement<Class<?>> clsElement = annotationIntrospector.collectAnnotations(parameter.getType());
        final CustomizationBuilder builder = new ClassCustomizationBuilder();
        builder.setAdapterInfo(annotationIntrospector.getAdapterBinding(clsElement));
        builder.setDeserializerBinding(annotationIntrospector.getDeserializerBinding(clsElement));
        builder.setSerializerBinding(annotationIntrospector.getSerializerBinding(clsElement));
        this.creatorCustomization = new CreatorCustomization(builder, constructorNumberFormatter, constructorDateFormatter);
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getName() {
        return name;
    }

    @Override
    public Customization getCustomization() {
        return creatorCustomization;
    }

    /**
     * Gets parameter type.
     *
     * @return Parameter type.
     */
    public Type getType() {
        return type;
    }

}
