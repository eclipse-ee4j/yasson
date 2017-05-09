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
package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonBindingModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomizationBuilder;
import org.eclipse.yasson.internal.model.customization.ContainerCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;

import java.lang.reflect.Type;

/**
 * Metadata wrapper for currently processed object.
 * References mapping models of an unmarshalled item,
 * creates instances of it, sets finished unmarshalled objects into object tree.
 *
 * @param <T> Instantiated object type
 * @author Roman Grigoriadi
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
     * Cached reference of a model of this item in wrapper class (if any).
     */
    private final JsonBindingModel wrapperModel;

    /**
     * Creates and populates an instance from given builder.
     *
     * @param builder Builder to initialize from.
     */
    protected AbstractItem(AbstractSerializerBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.wrapperModel = builder.getModel();
        this.classModel = builder.getClassModel();
        this.runtimeType = builder.getRuntimeType();
    }

    /**
     * Creates an instance.
     *
     * @param wrapper Item wrapper.
     * @param runtimeType Runtime type.
     * @param classModel Class model.
     * @param wrapperModel Binding model.
     */
    public AbstractItem(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel, JsonBindingModel wrapperModel) {
        this.wrapper = wrapper;
        this.runtimeType = runtimeType;
        this.classModel = classModel;
        this.wrapperModel = wrapperModel;
    }

    @Override
    public ClassModel getClassModel() {
        return classModel;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    /**
     * A wrapper model for this item. May represent a JavaBean property or a container like collection.
     *
     * @return wrapper model.
     */
    @Override
    public JsonBindingModel getWrapperModel() {
        return wrapperModel;
    }

    @Override
    public Type getRuntimeType() {
        return runtimeType;
    }

    protected Customization resolveContainerModelCustomization(Type componentType, JsonbContext jsonbContext) {
        Class<?> valueRawType = ReflectionUtils.resolveRawType(this, componentType);
        ClassModel classModel = jsonbContext.getMappingContext().getClassModel(valueRawType);
        if (classModel != null) {
            return new ContainerCustomization(classModel.getCustomization());
        }
        return new ContainerCustomization(new ClassCustomizationBuilder());
    }
}
