/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Function;

import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * Abstract class for getting / setting value into the property.
 */
public abstract class PropertyValuePropagation {

    private final Field field;

    private final Method getter;

    private final Method setter;

    private final PropertyVisibilityStrategy propertyVisibilityStrategy;

    /**
     * Mode of property propagation get or set.
     */
    public enum OperationMode {
        /**
         * Get property operation.
         */
        GET,
        /**
         * Set property operation.
         */
        SET
    }

    /**
     * Property can be written (unmarshalled from json).
     */
    private boolean writable;

    /**
     * Property can be read (marshalled to json).
     */
    private boolean readable;

    private final boolean getterVisible;

    private final boolean setterVisible;

    /**
     * Construct a property propagation.
     *
     * @param property Provided property.
     * @param strategy Visibility strategy
     */
    protected PropertyValuePropagation(Property property, PropertyVisibilityStrategy strategy) {
        this.field = property.getField();
        this.getter = property.getGetter();
        this.setter = property.getSetter();
        this.propertyVisibilityStrategy = strategy;
        this.getterVisible = isMethodVisible(field, getter);
        this.setterVisible = isMethodVisible(field, setter);

        initReadable(field, getter);
        initWritable(field, setter);
    }

    private void initReadable(Field field, Method getter) {

        final boolean fieldReadable = field == null || (field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) == 0;
        if (!fieldReadable) {
            readable = false;
            return;
        }
        if (getter != null && getterVisible) {
            acceptMethod(getter, OperationMode.GET);
            readable = true;
        } else if (isFieldVisible(field, getter)) {
            acceptField(field, OperationMode.GET);
            readable = true;
        }
    }

    private void initWritable(Field field, Method setter) {

        final boolean fieldWritable =
                field == null || (field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL)) == 0;
        if (!fieldWritable) {
            writable = false;
            return;
        }
        if (setter != null && setterVisible && !setter.getDeclaringClass().isAnonymousClass()) {
            acceptMethod(setter, OperationMode.SET);
            writable = true;
        } else if (isFieldVisible(field, setter) && !field.getDeclaringClass().isAnonymousClass()) {
            acceptField(field, OperationMode.SET);
            writable = true;
        }
    }

    private boolean isFieldVisible(Field field, Method method) {
        if (field == null) {
            return false;
        }
        Boolean accessible = isVisible(strategy -> strategy.isVisible(field), field, method);
        //overridden by strategy, or anonymous class (readable by spec)
        if (accessible && (
                !Modifier.isPublic(field.getModifiers())
                        || field.getDeclaringClass().isAnonymousClass()
                        || isNotPublicAndNonNested(field.getDeclaringClass()))) {
            overrideAccessible(field);
        }
        return accessible;
    }

    private boolean isNotPublicAndNonNested(Class<?> declaringClass) {
        return !declaringClass.isMemberClass() && !Modifier.isPublic(declaringClass.getModifiers());
    }

    private boolean isMethodVisible(Field field, Method method) {
        if (method == null || Modifier.isStatic(method.getModifiers())) {
            return false;
        }

        Boolean accessible = isVisible(strategy -> strategy.isVisible(method), field, method);
        //overridden by strategy, anonymous class, or lambda
        if (accessible && (
                !Modifier.isPublic(method.getModifiers()) || method.getDeclaringClass().isAnonymousClass() || method
                        .getDeclaringClass().isSynthetic())) {
            overrideAccessible(method);
        }
        return accessible;
    }

    private void overrideAccessible(AccessibleObject accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            accessibleObject.setAccessible(true);
            return null;
        });
    }

    /**
     * Look up class and package level @JsonbVisibility, or global config PropertyVisibilityStrategy.
     * If any is found it is used for resolving visibility by calling provided visibilityCheckFunction.
     *
     * @param visibilityCheckFunction function declaring visibility check
     * @return Optional with result of visibility check, or empty optional if no strategy is found
     */
    private Boolean isVisible(Function<PropertyVisibilityStrategy, Boolean> visibilityCheckFunction, Field field, Method method) {
        return propertyVisibilityStrategy != null
                ? visibilityCheckFunction.apply(propertyVisibilityStrategy)
                : visibilityCheckFunction.apply(new DefaultVisibilityStrategy(field, method));
    }

    /**
     * Accept a {@link Method} to use value propagation.
     *
     * @param method method
     * @param mode   read or write
     */
    protected abstract void acceptMethod(Method method, OperationMode mode);

    /**
     * Accept a {@link Field} to use for value propagation.
     *
     * @param field field
     * @param mode  mod
     */
    protected abstract void acceptField(Field field, OperationMode mode);

    /**
     * Set a value to a field. Based on policy invokes a setter or sets directly to a field.
     *
     * @param object object to set value in
     * @param value  value to set, null is valid
     */
    abstract void setValue(Object object, Object value);

    /**
     * Gets a value of a field. Based on policy invokes a getter or gets directly from a field.
     *
     * @param object object to get from
     */
    abstract Object getValue(Object object);

    /**
     * Property is writable. Based on access policy and java field modifiers.
     *
     * @return true if can be deserialized from JSON
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Property is readable. Based on access policy and java field modifiers.
     *
     * @return true if can be serialized to JSON
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * Field of a javabean property.
     *
     * @return {@link Field field}
     */
    public Field getField() {
        return field;
    }

    /**
     * Setter of a javabean property.
     *
     * @return {@link Method getter}
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * Getter of a javabean property.
     *
     * @return {@link Method setter}
     */
    public Method getSetter() {
        return setter;
    }

    public boolean isGetterVisible() {
        return getterVisible;
    }

    public boolean isSetterVisible() {
        return setterVisible;
    }

    private static final class DefaultVisibilityStrategy implements PropertyVisibilityStrategy {

        private final Field field;

        private final Method method;

        DefaultVisibilityStrategy(Field field, Method method) {
            this.field = field;
            this.method = method;
        }

        @Override
        public boolean isVisible(Field field) {
            //don't check field if getter is not visible (forced by spec)
            if (method != null && !isVisible(method)) {
                return false;
            }
            return Modifier.isPublic(field.getModifiers());
        }

        @Override
        public boolean isVisible(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    }
}
