/*
 * Copyright (c) 2016, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.model.customization.CreatorCustomization;

/**
 * Parameter for creator constructor / method model.
 */
public class CreatorModel {

    private final String name;

    private final Type type;

    private final CreatorCustomization creatorCustomization;

    /**
     * Creates a new instance.
     *  @param name      Parameter name
     * @param parameter constructor parameter
     * @param index     index of the parameter within the executable
     * @param executable creator executable
     * @param context   jsonb context
     */
    public CreatorModel(String name, Parameter parameter, int index, Executable executable, JsonbContext context) {
        this.name = name;
        this.type = resolveType(parameter, index, executable);

        AnnotationIntrospector annotationIntrospector = context.getAnnotationIntrospector();

        JsonbAnnotatedElement<Parameter> annotated = new JsonbAnnotatedElement<>(parameter);
        boolean required = context.getAnnotationIntrospector().requiredParameters(executable, annotated);
        JsonbNumberFormatter constructorNumberFormatter = context.getAnnotationIntrospector()
                .getConstructorNumberFormatter(annotated);
        JsonbDateFormatter constructorDateFormatter = context.getAnnotationIntrospector().getConstructorDateFormatter(annotated);
        DeserializerBinding<?> deserializerBinding = annotationIntrospector.getDeserializerBinding(parameter);
        AdapterBinding adapterBinding = annotationIntrospector.getAdapterBinding(parameter);
        final JsonbAnnotatedElement<Class<?>> clsElement = annotationIntrospector.collectAnnotations(parameter.getType());
        deserializerBinding = deserializerBinding == null
                ? annotationIntrospector.getDeserializerBinding(clsElement)
                : deserializerBinding;
        adapterBinding = adapterBinding == null
                ? annotationIntrospector.getAdapterBinding(clsElement)
                : adapterBinding;
        this.creatorCustomization = CreatorCustomization.builder()
                .adapterBinding(adapterBinding)
                .deserializerBinding(deserializerBinding)
                .serializerBinding(annotationIntrospector.getSerializerBinding(clsElement))
                .numberFormatter(constructorNumberFormatter)
                .dateFormatter(constructorDateFormatter)
                .required(required)
                .build();
    }

    /**
     * Gets parameter name.
     *
     * @return Parameter name.
     */
    public String getName() {
        return name;
    }

    public CreatorCustomization getCustomization() {
        return creatorCustomization;
    }

    /*
     * Parameter#getParameterizedType() resolves against the executable's full parameter list,
     * which includes implicit and synthetic parameters. Since JDK 22 that lookup yields the raw
     * type for the mandated parameters of a record's canonical constructor whenever the record
     * declares a compact constructor, so a component such as List<Photo> is seen as a raw List
     * and its elements are deserialized into maps.
     *
     * Executable#getGenericParameterTypes() still carries the generic signature, so prefer it.
     * It omits implicit and synthetic parameters, and therefore may be shorter than the
     * parameter array (an inner class constructor, for instance), in which case the indexes do
     * not line up and the original lookup is kept.
     */
    private static Type resolveType(Parameter parameter, int index, Executable executable) {
        Type[] genericParameterTypes = executable.getGenericParameterTypes();
        if (genericParameterTypes.length == executable.getParameterCount() && index < genericParameterTypes.length) {
            return genericParameterTypes[index];
        }
        return parameter.getParameterizedType();
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
