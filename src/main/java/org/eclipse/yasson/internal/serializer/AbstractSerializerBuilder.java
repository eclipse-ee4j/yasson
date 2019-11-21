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

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.util.Objects;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Base class for serializer builders.
 *
 * @param <T> serialization builder type
 */
public class AbstractSerializerBuilder<T extends AbstractSerializerBuilder> {

    /**
     * Not null with an exception of a root item.
     */
    private CurrentItem<?> wrapper;

    /**
     * In case of unknown object genericType.
     * Null for embedded objects such as collections, or known conversion types.
     */
    private ClassModel classModel;

    /**
     * Runtime type resolved after expanding type variables and wildcards.
     */
    private Type runtimeType;

    /**
     * Type is used when field model is not present.
     * In case of root, or embedded objects such as collections.
     */
    private Type genericType;

    /**
     * Class customization.
     */
    private Customization customization;

    /**
     * Jsonb context.
     */
    private final JsonbContext jsonbContext;

    /**
     * Crates a builder.
     *
     * @param jsonbContext Not null.
     */
    public AbstractSerializerBuilder(JsonbContext jsonbContext) {
        Objects.requireNonNull(jsonbContext);
        this.jsonbContext = jsonbContext;
    }

    /**
     * Wrapper item for this item.
     *
     * @param wrapper not null.
     * @return Builder instance for call chaining.
     */
    @SuppressWarnings("unchecked")
    public T withWrapper(CurrentItem<?> wrapper) {
        this.wrapper = wrapper;
        return (T) this;
    }

    /**
     * Customization of the class.
     *
     * @param customization Class customization
     * @return Builder instance for call chaining.
     */
    @SuppressWarnings("unchecked")
    public T withCustomization(Customization customization) {
        this.customization = customization;
        return (T) this;
    }

    /**
     * Class model for this item.
     *
     * @param classModel class model
     * @return Builder instance for call chaining.
     */
    @SuppressWarnings("unchecked")
    public T withClassModel(ClassModel classModel) {
        this.classModel = classModel;
        return (T) this;
    }

    /**
     * Runtime type for this item.
     *
     * @param runtimeType runtime type
     * @return Builder instance for call chaining.
     */
    @SuppressWarnings("unchecked")
    public T withRuntimeType(Type runtimeType) {
        this.runtimeType = runtimeType;
        return (T) this;
    }

    /***
     * Gets or load class model for a class an its superclasses.
     *
     * @param rawType Class to get model for.
     * @return Class model.
     */
    protected ClassModel getClassModel(Class<?> rawType) {
        ClassModel classModel = jsonbContext.getMappingContext().getClassModel(rawType);
        if (classModel == null) {
            classModel = jsonbContext.getMappingContext().getOrCreateClassModel(rawType);
        }
        return classModel;
    }

    /**
     * Wrapper item for this item.
     *
     * @return Wrapper item.
     */
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * Model of a class representing current item and instance (if any).
     * Known collection classes doesn't need such a model.
     *
     * @return model of a class
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Resolved runtime type for instance in case of {@link java.lang.reflect.TypeVariable} or
     * {@link java.lang.reflect.WildcardType}.
     * Otherwise provided type in type field, or type of field model.
     *
     * @return runtime type
     */
    public Type getRuntimeType() {
        return runtimeType;
    }

    /**
     * Type for underlying instance to be created from.
     * In case of type variable or wildcard, will be resolved recursively from parent items.
     *
     * @param type type of instance not null
     * @return builder instance for call chaining
     */
    @SuppressWarnings("unchecked")
    public T withType(Type type) {
        this.genericType = type;
        return (T) this;
    }

    /**
     * Jsonb runtime context.
     *
     * @return jsonb context
     */
    public JsonbContext getJsonbContext() {
        return jsonbContext;
    }

    /**
     * Type customization.
     *
     * @return customization
     */
    public Customization getCustomization() {
        return customization;
    }

    /**
     * Generic type of the item.
     *
     * @return generic type
     */
    public Type getGenericType() {
        return genericType;
    }
}
