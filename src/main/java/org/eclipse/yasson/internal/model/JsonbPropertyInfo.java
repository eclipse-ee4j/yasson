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

package org.eclipse.yasson.internal.model;

import java.lang.reflect.Type;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.serializer.CurrentItem;

/**
 * Wrapper for metadata of serialized property.
 */
public class JsonbPropertyInfo {

    private JsonbContext context;

    private Type runtimeType;

    private ClassModel classModel;

    private CurrentItem<?> wrapper;

    /**
     * Gets context.
     *
     * @return Context.
     */
    public JsonbContext getContext() {
        return context;
    }

    /**
     * Sets context.
     *
     * @param context Context to set.
     * @return Updated object.
     */
    public JsonbPropertyInfo setContext(JsonbContext context) {
        this.context = context;
        return this;
    }

    /**
     * Gets runtime type.
     *
     * @return Runtime type.
     */
    public Type getRuntimeType() {
        return runtimeType;
    }

    /**
     * Sets runtime type.
     *
     * @param runtimeType Runtime type to set.
     * @return Updated object.
     */
    public JsonbPropertyInfo withRuntimeType(Type runtimeType) {
        this.runtimeType = runtimeType;
        return this;
    }

    /**
     * Gets class model.
     *
     * @return Class model.
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Sets class model.
     *
     * @param classModel Class model to set.
     * @return Updated object.
     */
    public JsonbPropertyInfo withClassModel(ClassModel classModel) {
        this.classModel = classModel;
        return this;
    }

    /**
     * Gets wrapper.
     *
     * @return Wrapper.
     */
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * Sets wrapper.
     *
     * @param wrapper Wrapper to set.
     * @return Updated object.
     */
    public JsonbPropertyInfo withWrapper(CurrentItem<?> wrapper) {
        this.wrapper = wrapper;
        return this;
    }
}
