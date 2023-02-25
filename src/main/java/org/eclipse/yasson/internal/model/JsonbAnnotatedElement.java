/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 * Annotation holder for classes, superclasses, interfaces, fields, getters and setters.
 *
 * @param <T> annotated element
 */
public class JsonbAnnotatedElement<T extends AnnotatedElement> {

    private final Map<Class<? extends Annotation>, LinkedList<AnnotationWrapper<?>>> annotations = new HashMap<>(4);

    private final T element;

    /**
     * Creates a new instance.
     *
     * @param element Element.
     */
    public JsonbAnnotatedElement(T element) {
        for (Annotation ann : element.getAnnotations()) {
            if (element instanceof Class) {
                putAnnotation(ann, false, (Class<?>) element);
            } else {
                putAnnotation(ann, false, null);
            }
        }

        this.element = element;
    }

    /**
     * Gets element.
     *
     * @return Element.
     */
    public T getElement() {
        return element;
    }

    /**
     * Get an annotation by type.
     *
     * @param <AT>            Type of annotation
     * @param annotationClass Type of annotation
     * @return Annotation by passed type
     */
    public <AT extends Annotation> Optional<AT> getAnnotation(Class<AT> annotationClass) {
        return Optional.ofNullable(annotations.get(annotationClass))
                .map(LinkedList::getFirst)
                .map(AnnotationWrapper::getAnnotation)
                .map(annotationClass::cast);
    }

    public <AT extends Annotation> LinkedList<AnnotationWrapper<?>> getAnnotations(Class<AT> annotationClass) {
        return annotations.getOrDefault(annotationClass, new LinkedList<>());
    }

    @SuppressWarnings("unchecked")
    public <AT extends Annotation> AnnotationWrapper<AT> getAnnotationWrapper(Class<AT> annotationClass) {
        return (AnnotationWrapper<AT>) annotations.get(annotationClass).getFirst();
    }

    public Annotation[] getAnnotations() {
        return annotations.values().stream()
                .flatMap(Collection::stream)
                .map(AnnotationWrapper::getAnnotation)
                .toArray(Annotation[]::new);
    }

    /**
     * Adds annotation.
     *
     * @param annotation Annotation to add.
     * @param definedType
     */
    public void putAnnotation(Annotation annotation, boolean inherited, Class<?> definedType) {
//        if (annotations.containsKey(annotation.annotationType())) {
//            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR,
//                                                         "Annotation already present: " + annotation));
//        }
//        annotations.put(annotation.annotationType(), new AnnotationWrapper(annotation, inherited));
        annotations.computeIfAbsent(annotation.annotationType(), aClass -> new LinkedList<>())
                        .add(new AnnotationWrapper<Annotation>(annotation, inherited, definedType));
    }

    public void putAnnotationWrapper(AnnotationWrapper<?> annotationWrapper) {
        annotations.computeIfAbsent(annotationWrapper.getAnnotation().annotationType(), aClass -> new LinkedList<>())
                .add(annotationWrapper);
    }

    public static final class AnnotationWrapper<T extends Annotation> {

        private final T annotation;
        private final boolean inherited;
        private final Class<?> definedType;

        public AnnotationWrapper(T annotation, boolean inherited, Class<?> definedType) {
            this.annotation = annotation;
            this.inherited = inherited;
            this.definedType = definedType;
        }

        public T getAnnotation() {
            return annotation;
        }

        public boolean isInherited() {
            return inherited;
        }

        public Class<?> getDefinedType() {
            return definedType;
        }

        @Override
        public String toString() {
            return definedType.getName();
        }
    }
}
