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
package org.eclipse.yasson.model;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.unmarshaller.CurrentItem;

import java.lang.reflect.Type;

/**
 * Wrapper for metadata of serialized property.
 *
 * @author Roman Grigoriadi
 */
public class JsonbPropertyInfo {

    private JsonbContext context;

    private Type runtimeType;

    private ClassModel classModel;

    private JsonBindingModel jsonBindingModel;

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
     * Gets binding model.
     *
     * @return Binding model.
     */
    public JsonBindingModel getJsonBindingModel() {
        return jsonBindingModel;
    }

    /**
     * Sets binding model.
     *
     * @param jsonBindingModel Binding model to set.
     * @return Updated object.
     */
    public JsonbPropertyInfo withJsonBindingModel(JsonBindingModel jsonBindingModel) {
        this.jsonBindingModel = jsonBindingModel;
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
