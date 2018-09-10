/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.serializer.ContainerSerializerProvider;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;

import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * JSONB mappingContext. Created once per {@link javax.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * Thread safe.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class MappingContext {

    private static class ParseClassModelFunction implements Function<Class, ClassModel> {

        private ClassModel parentClassModel;

        private ClassParser classParser;

        private JsonbContext jsonbContext;

        public ParseClassModelFunction(ClassModel parentClassModel, ClassParser classParser, JsonbContext jsonbContext) {
            this.parentClassModel = parentClassModel;
            this.classParser = classParser;
            this.jsonbContext = jsonbContext;
        }

        @Override
        public ClassModel apply(Class aClass) {
            final JsonbAnnotatedElement<Class<?>> clsElement = jsonbContext.getAnnotationIntrospector().collectAnnotations(aClass);
            final ClassCustomization customization = jsonbContext.getAnnotationIntrospector().introspectCustomization(clsElement);
            final ClassModel newClassModel = new ClassModel(aClass, customization, parentClassModel, jsonbContext.getConfigProperties().getPropertyNamingStrategy());
            classParser.parseProperties(newClassModel, clsElement);
            return newClassModel;
        }
    }

    private final JsonbContext jsonbContext;

    private final ConcurrentHashMap<Class<?>, ClassModel> classes = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, ContainerSerializerProvider> serializers = new ConcurrentHashMap<>();

    private final ClassParser classParser;

    /**
     * Create mapping context which is scoped to jsonb runtime.
     *
     * @param jsonbContext Context. Required.
     */
    public MappingContext(JsonbContext jsonbContext) {
        Objects.requireNonNull(jsonbContext);
        this.jsonbContext = jsonbContext;
        this.classParser = new ClassParser(jsonbContext);
    }

    /**
     * Searches for class model for given class. Returns the existing instance. Creates a new instance if
     * it doesn't exist.
     *
     * @param clazz Class to search by or parse, not null.
     * @return {@link ClassModel} for given class.
     */
    public ClassModel getOrCreateClassModel(Class<?> clazz) {
        ClassModel classModel = classes.get(clazz);
        if (classModel != null) {
            return classModel;
        }
        final Stack<Class> newClassModels = new Stack<>();
        for (Class classToParse = clazz; classToParse != Object.class; classToParse = classToParse.getSuperclass()) {
            if (classToParse == null){
                break;
            }
            newClassModels.push(classToParse);
        }
        if (clazz == Object.class) {
            classes.computeIfAbsent(clazz, (c) -> new ClassModel(c, null, null, null));
            return classes.get(clazz);
        }

        ClassModel parentClassModel = null;
        while (!newClassModels.empty()) {
            Class toParse = newClassModels.pop();
            parentClassModel = classes.computeIfAbsent(toParse, new ParseClassModelFunction(parentClassModel, classParser, jsonbContext));
        }
        return classes.get(clazz);
    }

    /**
     * Provided class class model is returned first by iterator.
     * Following class models are sorted by hierarchy from provided class up to the Object.class.
     *
     * @param clazz class to start iteration of class models from
     * @return iterator of class models
     */
    public Iterator<ClassModel> classModelIterator(final Class<?> clazz) {
        return new Iterator<ClassModel>() {
            private Class<?> next = clazz;

            @Override
            public boolean hasNext() {
                return next != Object.class;
            }

            @Override
            public ClassModel next() {
                final ClassModel result = classes.get(next);
                next = next.getSuperclass();
                return result;
            }
        };
    }

    /**
     * Search for class model, without parsing if not found.
     *
     * @param clazz Class to search by or parse, not null.
     * @return Model of a class if found.
     */
    public ClassModel getClassModel(Class<?> clazz) {
        return classes.get(clazz);
    }

    /**
     * Gets serializer provider for given class.
     *
     * @param clazz Class to get serializer provider for.
     * @return Serializer provider.
     */
    public ContainerSerializerProvider getSerializerProvider(Class<?> clazz) {
        return serializers.get(clazz);
    }

    /**
     * Adds given serializer provider for given class.
     *
     * @param clazz Class to add serializer provider for.
     * @param serializerProvider Serializer provider to add.
     */
    public void addSerializerProvider(Class<?> clazz, ContainerSerializerProvider serializerProvider) {
        serializers.putIfAbsent(clazz, serializerProvider);
    }
}
