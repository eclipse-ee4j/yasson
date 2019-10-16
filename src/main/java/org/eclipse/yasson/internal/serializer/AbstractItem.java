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

import org.eclipse.yasson.internal.model.ClassModel;

/**
 * Metadata wrapper for currently processed object.
 * References mapping models of an unmarshalled item,
 * creates instances of it, sets finished unmarshalled objects into object tree.
 *
 * @param <T> Instantiated object type
 */
public abstract class AbstractItem<T> implements CurrentItem<T> {

    /**
     * Item containing instance of wrapping object and its metadata.
     * Null in case of a root object.
     */
    private final CurrentItem<?> wrapper;

    private final Type runtimeType;

    /**
     * Cached reference to mapping model of an item.
     */
    private final ClassModel classModel;

    /**
     * Creates and populates an instance from given builder.
     *
     * @param builder Builder to initialize from.
     */
    protected AbstractItem(AbstractSerializerBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.classModel = builder.getClassModel();
        this.runtimeType = builder.getRuntimeType();
    }

    /**
     * Creates an instance.
     *
     * @param wrapper     Item wrapper.
     * @param runtimeType Runtime type.
     * @param classModel  Class model.
     */
    public AbstractItem(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel) {
        this.wrapper = wrapper;
        this.runtimeType = runtimeType;
        this.classModel = classModel;
    }

    @Override
    public ClassModel getClassModel() {
        return classModel;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }

}
