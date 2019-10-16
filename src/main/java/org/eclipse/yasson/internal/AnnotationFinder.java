/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Finds an annotation including inherited annotations (e.g. meta-annotations).
 */
class AnnotationFinder {

    private static final String CONSTRUCTOR_PROPERTIES_ANNOTATION = "java.beans.ConstructorProperties";
    private static final Logger LOGGER = Logger.getLogger(AnnotationFinder.class.getName());

    private final String annotationClassName;
    private final Class<? extends Annotation> annotationClass; // may be null

    /**
     * Gets the {@link AnnotationFinder} for the given Annotation-Type.
     *
     * @param annotation {@link Class}, that is a sub-type of {@link Annotation}
     * @return {@link AnnotationFinder}
     */
    public static AnnotationFinder findAnnotation(Class<?> annotation) {
        return findAnnotationByName(annotation.getName());
    }

    /**
     * Gets the {@link AnnotationFinder} for the given Annotation-Type Name.
     *
     * @param annotationClassName {@link String}, that is a sub-type of {@link Annotation}
     * @return {@link AnnotationFinder}
     */
    public static AnnotationFinder findAnnotationByName(String annotationClassName) {
        return new AnnotationFinder(annotationClassName, getOptionalAnnotationClass(annotationClassName));
    }

    /**
     * Gets the {@link AnnotationFinder} for @ConstructorProperties-Annotation.
     *
     * @return {@link AnnotationFinder}
     */
    public static AnnotationFinder findConstructorProperties() {
        return findAnnotationByName(CONSTRUCTOR_PROPERTIES_ANNOTATION);
    }

    private AnnotationFinder(String annotationClassName, Class<? extends Annotation> annotationClass) {
        this.annotationClassName = annotationClassName;
        this.annotationClass = annotationClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T in(Annotation[] annotations) {
        if (annotationClass == null) {
            return null;
        }
        return (T) findAnnotation(annotations, annotationClass, new HashSet<>());
    }

    /**
     * Looks for the annotation {@link #in(Annotation[])} <br>
     * and executes the "value" Method of it dynamically.
     *
     * @param annotations - Array of {@link Annotation}n.
     * @return {@link Object}
     */
    public Object valueIn(Annotation[] annotations) {
        return invocateValueMethod(in(annotations));
    }

    private Object invocateValueMethod(Annotation annotation) {
        if (annotation == null) {
            return null;
        }
        try {
            return annotation.annotationType().getMethod("value").invoke(annotation);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            String message = Messages
                    .getMessage(MessageKeys.MISSING_VALUE_PROPERTY_IN_ANNOTATION, annotation.annotationType().getName());
            LOGGER.finest(message);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> Class<T> getOptionalAnnotationClass(String classname) {
        try {
            return (Class<T>) Class.forName(classname);
        } catch (ClassNotFoundException e) {
            String message = Messages.getMessage(MessageKeys.ANNOTATION_NOT_AVAILABLE, classname);
            LOGGER.finest(message);
            return null;
        }
    }

    /**
     * Searches for annotation, collects processed, to avoid StackOverflow.
     */
    // "static" to use it in a hybrid procedural and object oriented manner.
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T findAnnotation(Annotation[] declaredAnnotations,
                                                          Class<T> annotationClass,
                                                          Set<Annotation> processed) {
        for (Annotation candidate : declaredAnnotations) {
            final Class<? extends Annotation> annType = candidate.annotationType();
            if (annType.equals(annotationClass)) {
                return (T) candidate;
            }
            processed.add(candidate);
            final List<Annotation> inheritedAnnotations = new ArrayList<>(Arrays.asList(annType.getDeclaredAnnotations()));
            inheritedAnnotations.removeAll(processed);
            if (inheritedAnnotations.size() > 0) {
                final T inherited = findAnnotation(inheritedAnnotations.toArray(new Annotation[inheritedAnnotations.size()]),
                                                   annotationClass,
                                                   processed);
                if (inherited != null) {
                    return inherited;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "AnnotationFinder [annotationClassName=" + annotationClassName + ", annotationClass=" + annotationClass + "]";
    }
}
