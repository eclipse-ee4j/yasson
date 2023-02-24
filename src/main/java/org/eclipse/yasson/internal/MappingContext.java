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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonbAnnotatedElement;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;

/**
 * JSONB mappingContext. Created once per {@link jakarta.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * Thread safe.
 */
public class MappingContext {
    private final JsonbContext jsonbContext;

    private final ConcurrentHashMap<Class<?>, ClassModel> classes = new ConcurrentHashMap<>();

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

        Deque<Class<?>> newClassModels = new ArrayDeque<>();
        for (Class<?> classToParse = clazz; classToParse != Object.class; classToParse = classToParse.getSuperclass()) {
            if (classToParse == null) {
                break;
            }
            newClassModels.push(classToParse);
        }
        if (clazz == Object.class) {
            return classes.computeIfAbsent(clazz, (c) -> new ClassModel(c, ClassCustomization.empty(), null, null));
        }

        ClassModel parentClassModel = null;
        while (!newClassModels.isEmpty()) {
            Class<?> toParse = newClassModels.pop();
            parentClassModel = classes
                    .computeIfAbsent(toParse, createParseClassModelFunction(parentClassModel, classParser, jsonbContext));
        }
        return classes.get(clazz);
    }

    private static Function<Class<?>, ClassModel> createParseClassModelFunction(ClassModel parentClassModel,
                                                                                ClassParser classParser,
                                                                                JsonbContext jsonbContext) {
        return aClass -> {
            JsonbAnnotatedElement<Class<?>> clsElement = jsonbContext.getAnnotationIntrospector().collectAnnotations(aClass);
            ClassCustomization customization = jsonbContext.getAnnotationIntrospector()
                    .introspectCustomization(clsElement,
                                             parentClassModel == null
                                                     ? ClassCustomization.empty()
                                                     : parentClassModel.getClassCustomization(),
                                             jsonbContext.getConfigProperties().getPropertyNamingStrategy());
            //            PolymorphismSupport configPolymorphism = jsonbContext.getConfigProperties().getPolymorphismSupport();
//            if (configPolymorphism != null) {
//                customization = mergeConfigAndAnnotationPolymorphism(configPolymorphism,
//                                                                     configPolymorphism.getClassPolymorphism(aClass),
//                                                                     customization,
//                                                                     aClass);
//            }
            ClassModel newClassModel = new ClassModel(aClass,
                                                      customization,
                                                      parentClassModel,
                                                      jsonbContext.getConfigProperties().getPropertyNamingStrategy());
            if (!BuiltInTypes.isKnownType(aClass)) {
                classParser.parseProperties(newClassModel, clsElement);
            }
            return newClassModel;
        };
    }

//    private static ClassCustomization mergeConfigAndAnnotationPolymorphism(PolymorphismSupport generalPolymorphism,
//                                                                           Optional<Polymorphism> maybeClassPolymorphism,
//                                                                           ClassCustomization customization,
//                                                                           Class<?> aClass) {
//        PolymorphismConfig polymorphismConfig = customization.getPolymorphismConfig();
//        PolymorphismConfig.Builder polyConfigBuilder;
//        if (polymorphismConfig != null) {
//            polyConfigBuilder = PolymorphismConfig.builder().of(polymorphismConfig);
//        } else {
//            polyConfigBuilder = PolymorphismConfig.builder();
//            maybeClassPolymorphism.ifPresent(classPolymorphism -> polyConfigBuilder
//                    .inherited(!classPolymorphism.getBoundClass().equals(aClass)));
//        }
//        generalPolymorphism.getKeyName().filter(s -> !s.isEmpty()).ifPresent(polyConfigBuilder::fieldName);
//        generalPolymorphism.useClassNames().ifPresent(polyConfigBuilder::useClassNames);
//        polyConfigBuilder.whitelistedPackages(generalPolymorphism.getWhitelistedPackages());
//
//        maybeClassPolymorphism.ifPresent(classPolymorphism -> {
//            classPolymorphism.getKeyName().filter(s -> !s.isEmpty()).ifPresent(polyConfigBuilder::fieldName);
//            classPolymorphism.useClassNames().ifPresent(polyConfigBuilder::useClassNames);
//            classPolymorphism.getFormat().ifPresent(polyConfigBuilder::format);
//            classPolymorphism.getAliases().forEach(polyConfigBuilder::alias);
//            polyConfigBuilder.whitelistedPackages(classPolymorphism.getWhitelistedPackages());
//        });
//        PolymorphismConfig polyConfigMerged = polyConfigBuilder.build();
//        if (polyConfigMerged.getFieldName() == null || polyConfigMerged.getFieldName().isEmpty()) {
//            throw new JsonbException("Polymorphism type field name cannot be null or empty: " + aClass);
//        }
//        return ClassCustomization.builder()
//                .of(customization)
//                .polymorphismConfig(polyConfigMerged)
//                .build();
//    }

    /**
     * Search for class model, without parsing if not found.
     *
     * @param clazz Class to search by or parse, not null.
     * @return Model of a class if found.
     */
    public ClassModel getClassModel(Class<?> clazz) {
        return classes.get(clazz);
    }

}
