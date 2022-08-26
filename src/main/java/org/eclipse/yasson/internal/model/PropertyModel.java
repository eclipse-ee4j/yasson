/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;

/**
 * A model for class property.
 * Property is JavaBean alike meta information field / getter / setter of a property in class.
 */
public final class PropertyModel implements Comparable<PropertyModel> {

    private static final MethodHandles.Lookup LOOKUP = ModulesUtil.lookup();

    /**
     * Field propertyName as in class by java bean convention.
     */
    private final String propertyName;

    /**
     * Calculated name to be used when reading json document.
     */
    private final String readName;

    /**
     * Calculated name to be used when writing json document.
     */
    private final String writeName;

    /**
     * Field propertyType.
     */
    private final Type propertyType;

    /**
     * Model of the class this field belongs to.
     */
    private final ClassModel classModel;

    private final Property property;

    /**
     * Customization of this property.
     */
    private final PropertyCustomization customization;

    private final MethodHandle getValueHandle;

    private final MethodHandle setValueHandle;

    private final Field field;

    private final Method getter;

    private final Method setter;

    private final Type getterMethodType;

    private final Type setterMethodType;

    /**
     * Create a new PropertyModel that merges two existing PropertyModel that have identical read/write names.
     * The input PropertyModel objects MUST be equal (a.equals(b) == true)
     *
     * @param a a PropertyModel instance to merge
     * @param b the other PropertyModel instance to merge
     */
    public PropertyModel(PropertyModel a, PropertyModel b) {
        if (!a.equals(b)) {
            throw new IllegalStateException("Property models " + a + " and " + b + " cannot be merged");
        }

        // Initial cloning steps
        this.classModel = a.classModel;
        this.propertyName = a.propertyName;
        this.readName = a.readName;
        this.writeName = a.writeName;
        this.propertyType = a.propertyType;
        this.customization = a.customization;

        // Merging steps
        this.getterMethodType = a.getterMethodType != null ? a.getterMethodType : b.getterMethodType;
        this.setterMethodType = a.setterMethodType != null ? a.setterMethodType : b.setterMethodType;
        this.property = a.property;
        if (b.property.getField() != null) {
            this.property.setField(b.property.getField());
        }
        if (b.property.getGetter() != null) {
            this.property.setGetter(b.property.getGetter());
        }
        if (b.property.getSetter() != null) {
            this.property.setSetter(b.property.getSetter());
        }
        this.field = property.getField();
        this.getter = property.getGetter();
        this.setter = property.getSetter();

        PropertyVisibilityStrategy strategy = classModel.getClassCustomization().getPropertyVisibilityStrategy();
        this.getValueHandle = createReadHandle(field, getter, isMethodVisible(getter, strategy), strategy);
        this.setValueHandle = createWriteHandle(field, setter, isMethodVisible(setter, strategy), strategy);
    }

    /**
     * Creates an instance.
     *
     * @param classModel   Class model of declaring class.
     * @param property     Property.
     * @param jsonbContext Context.
     */
    public PropertyModel(ClassModel classModel, Property property, JsonbContext jsonbContext) {
        this.classModel = classModel;
        this.property = property;
        this.propertyName = property.getName();
        this.propertyType = property.getPropertyType();
        this.field = property.getField();
        this.getter = property.getGetter();
        this.setter = property.getSetter();

        PropertyVisibilityStrategy strategy = classModel.getClassCustomization().getPropertyVisibilityStrategy();
        boolean getterVisible = isMethodVisible(getter, strategy);
        boolean setterVisible = isMethodVisible(setter, strategy);

        this.getValueHandle = createReadHandle(field, getter, getterVisible, strategy);
        this.setValueHandle = createWriteHandle(field, setter, setterVisible, strategy);
        this.getterMethodType = getterVisible ? property.getGetterType() : null;
        this.setterMethodType = setterVisible ? property.getSetterType() : null;
        this.customization = introspectCustomization(property, jsonbContext, classModel);
        this.readName = calculateReadWriteName(customization.getJsonReadName(), propertyName,
                                               jsonbContext.getConfigProperties().getPropertyNamingStrategy());
        this.writeName = calculateReadWriteName(customization.getJsonWriteName(), propertyName,
                                                jsonbContext.getConfigProperties().getPropertyNamingStrategy());
    }

    /**
     * Returns which type should be used to deserialization.
     *
     * @return deserialization type
     */
    public Type getPropertyDeserializationType() {
        return setterMethodType == null ? propertyType : setterMethodType;
    }

    /**
     * Returns which type should be used to serialization.
     *
     * @return serialization type
     */
    public Type getPropertySerializationType() {
        return getterMethodType == null ? propertyType : getterMethodType;
    }

    private SerializerBinding<?> getUserSerializerBinding(Property property, JsonbContext jsonbContext) {
        final SerializerBinding<?> serializerBinding = jsonbContext.getAnnotationIntrospector().getSerializerBinding(property);
        if (serializerBinding != null) {
            return serializerBinding;
        }
        return jsonbContext.getComponentMatcher().getSerializerBinding(getPropertySerializationType(), null).orElse(null);
    }

    private PropertyCustomization introspectCustomization(Property property, JsonbContext jsonbContext, ClassModel classModel) {
        final AnnotationIntrospector introspector = jsonbContext.getAnnotationIntrospector();
        final PropertyCustomization.Builder builder = PropertyCustomization.builder();
        //drop all other annotations for transient properties
        EnumSet<AnnotationTarget> transientInfo = introspector.getJsonbTransientCategorized(property);
        ClassModel parent = classModel;
        // Check parent classes for transient annotations
        while ((parent = parent.getParentClassModel()) != null) {
            PropertyModel parentProperty = parent.getPropertyModel(property.getName());
            if (parentProperty != null) {
                if (parentProperty.customization.isReadTransient()) {
                    transientInfo.add(AnnotationTarget.GETTER);
                }
                if (parentProperty.customization.isWriteTransient()) {
                    transientInfo.add(AnnotationTarget.SETTER);
                }
            }
        }
        if (transientInfo.size() != 0) {
            builder.readTransient(transientInfo.contains(AnnotationTarget.GETTER));
            builder.writeTransient(transientInfo.contains(AnnotationTarget.SETTER));

            if (transientInfo.contains(AnnotationTarget.PROPERTY)) {
                if (!transientInfo.contains(AnnotationTarget.GETTER)) {
                    builder.readTransient(true);
                }
                if (!transientInfo.contains(AnnotationTarget.SETTER)) {
                    builder.writeTransient(true);
                }
            }

            if (builder.readTransient()) {
                introspector.checkTransientIncompatible(property.getFieldElement());
                introspector.checkTransientIncompatible(property.getGetterElement());
            }
            if (builder.writeTransient()) {
                introspector.checkTransientIncompatible(property.getFieldElement());
                introspector.checkTransientIncompatible(property.getSetterElement());
            }
        }

        if (!builder.readTransient()) {
            builder.jsonWriteName(introspector.getJsonbPropertyJsonWriteName(property));
            builder.nillable(introspector.isPropertyNillable(property).orElse(classModel.getClassCustomization().isNillable()));
            builder.serializerBinding(getUserSerializerBinding(property, jsonbContext));
        }

        if (!builder.writeTransient()) {
            builder.jsonReadName(introspector.getJsonbPropertyJsonReadName(property));
            builder.deserializerBinding(introspector.getDeserializerBinding(property));
        }

        final AdapterBinding adapterBinding = jsonbContext.getAnnotationIntrospector().getAdapterBinding(property);
        if (adapterBinding != null) {
            builder.serializeAdapter(adapterBinding);
            builder.deserializeAdapter(adapterBinding);
        } else {
            builder.serializeAdapter(jsonbContext.getComponentMatcher()
                                             .getSerializeAdapterBinding(getPropertySerializationType(), null).orElse(null));
            builder.deserializeAdapter(jsonbContext.getComponentMatcher()
                                               .getDeserializeAdapterBinding(getPropertyDeserializationType(), null)
                                               .orElse(null));
        }

        introspectDateFormatter(property, introspector, builder, jsonbContext);
        introspectNumberFormatter(property, introspector, builder);
        builder.implementationClass(introspector.getImplementationClass(property));

        return builder.build();
    }

    private static void introspectDateFormatter(Property property,
                                                AnnotationIntrospector introspector,
                                                PropertyCustomization.Builder builder,
                                                JsonbContext jsonbContext) {
        /*
         * If @JsonbDateFormat is placed on getter implementation must use this format on serialization.
         * If @JsonbDateFormat is placed on setter implementation must use this format on deserialization.
         * If @JsonbDateFormat is placed on field implementation must use this format on serialization and deserialization.
         *
         * Priority from high to low is getter / setter > field > class > package > global configuration
         */
        Map<AnnotationTarget, JsonbDateFormatter> jsonDateFormatCategorized = introspector
                .getJsonbDateFormatCategorized(property);
        final JsonbDateFormatter configDateFormatter = jsonbContext.getConfigProperties().getConfigDateFormatter();

        if (!builder.readTransient()) {
            final JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(jsonDateFormatCategorized,
                                                                                  AnnotationTarget.GETTER,
                                                                                  AnnotationTarget.PROPERTY,
                                                                                  AnnotationTarget.CLASS);

            builder.serializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
        }

        if (!builder.writeTransient()) {
            final JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(jsonDateFormatCategorized,
                                                                                  AnnotationTarget.SETTER,
                                                                                  AnnotationTarget.PROPERTY,
                                                                                  AnnotationTarget.CLASS);

            builder.deserializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
        }
    }

    private static void introspectNumberFormatter(Property property,
                                                  AnnotationIntrospector introspector,
                                                  PropertyCustomization.Builder builder) {
        /*
         * If @JsonbNumberFormat is placed on getter implementation must use this format on serialization.
         * If @JsonbNumberFormat is placed on setter implementation must use this format on deserialization.
         * If @JsonbNumberFormat is placed on field implementation must use this format on serialization and deserialization.
         *
         * Priority from high to low is getter / setter > field > class > package > global configuration
         */
        Map<AnnotationTarget, JsonbNumberFormatter> jsonNumberFormatCategorized = introspector.getJsonNumberFormatter(property);

        if (!builder.readTransient()) {
            builder.serializeNumberFormatter(getTargetForMostPreciseScope(jsonNumberFormatCategorized,
                                                                          AnnotationTarget.GETTER,
                                                                          AnnotationTarget.PROPERTY,
                                                                          AnnotationTarget.CLASS));
        }

        if (!builder.writeTransient()) {
            builder.deserializeNumberFormatter(getTargetForMostPreciseScope(jsonNumberFormatCategorized,
                                                                            AnnotationTarget.SETTER,
                                                                            AnnotationTarget.PROPERTY,
                                                                            AnnotationTarget.CLASS));
        }
    }

    /**
     * Pull result for most significant scope defined by order of annotation targets.
     *
     * @param collectedAnnotations all targets
     * @param targets              ordered target types by scope
     */
    private static <T> T getTargetForMostPreciseScope(Map<AnnotationTarget, T> collectedAnnotations,
                                                      AnnotationTarget... targets) {
        for (AnnotationTarget target : targets) {
            final T result = collectedAnnotations.get(target);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Gets property's value.
     *
     * @param object object to read property from
     * @return property's value
     */
    public Object getValue(Object object) {
        try {
            return getValueHandle.invoke(object);
        } catch (Throwable e) {
            throw new JsonbException("Error getting value on: " + object, e);
        }
    }

    /**
     * Sets a property.
     *
     * If not writable (final, transient, static), ignores property.
     *
     * @param object Object to set value in.
     * @param value  Value to set.
     */
    public void setValue(Object object, Object value) {
        if (!isWritable()) {
            return;
        }
        try {
            setValueHandle.invoke(object, value);
        } catch (Throwable e) {
            throw new JsonbException("Error setting value on: " + object, e);
        }
    }

    /**
     * Property is readable. Based on access policy and java field modifiers.
     *
     * @return true if can be serialized to JSON
     */
    public boolean isReadable() {
        return !customization.isReadTransient() && this.getValueHandle != null;
    }

    /**
     * Property is writable. Based on access policy and java field modifiers.
     *
     * @return true if can be deserialized from JSON
     */
    public boolean isWritable() {
        return !customization.isWriteTransient() && this.setValueHandle != null;
    }

    /**
     * Default property name according to Field / Getter / Setter method names.
     * This name is use for identifying properties, for JSON serialization is used customized name
     * which may be derived from default name.
     *
     * @return default name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Model of declaring class of this property.
     *
     * @return class model
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Introspected customization of a property.
     *
     * @return immutable property customization
     */
    public PropertyCustomization getCustomization() {
        return customization;
    }

    @Override
    public int compareTo(PropertyModel o) {
        int compare = readName.compareTo(o.readName);
        return compare == 0 ? writeName.compareTo(o.writeName) : compare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyModel other = (PropertyModel) o;
        return Objects.equals(readName, other.readName)
                && Objects.equals(writeName, other.writeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readName, writeName);
    }

    /**
     * Gets a name of JSON document property to read this property from.
     *
     * @return Name of JSON document property.
     */
    public String getReadName() {
        return readName;
    }

    public String getWriteName() {
        return writeName;
    }

    /**
     * If customized by JsonbPropertyAnnotation, than is used, otherwise use strategy to translate.
     * Since this is cached for performance reasons strategy has to be consistent
     * with calculated values for same input.
     */
    private static String calculateReadWriteName(String readWriteName, String propertyName, PropertyNamingStrategy strategy) {
        return readWriteName != null ? readWriteName : strategy.translateName(propertyName);
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

    // Used in ClassParser
    public static boolean isPropertyReadable(Field field, Method getter, PropertyVisibilityStrategy strategy) {
        return createReadHandle(field, getter, isMethodVisible(getter, strategy), strategy) != null;
    }

    private static MethodHandle createReadHandle(Field field,
                                                 Method getter,
                                                 boolean getterVisible,
                                                 PropertyVisibilityStrategy strategy) {
        boolean fieldReadable = field == null || (field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) == 0;

        if (fieldReadable) {
            if (getter != null && getterVisible) {
                try {
                    return LOOKUP.unreflect(getter);
                } catch (Throwable e) {
                    throw new JsonbException("Error accessing getter '" + getter.getName() + "' declared in '" + getter
                            .getDeclaringClass() + "'", e);
                }
            }
            if (isFieldVisible(field, getter, strategy)) {
                try {
                    return LOOKUP.unreflectGetter(field);
                } catch (IllegalAccessException e) {
                    throw new JsonbException("Error accessing field '" + field.getName() + "' declared in '" + field
                            .getDeclaringClass() + "'", e);
                }
            }
        }

        return null;
    }

    private static MethodHandle createWriteHandle(Field field,
                                                  Method setter,
                                                  boolean setterVisible,
                                                  PropertyVisibilityStrategy strategy) {
        boolean fieldWritable =
                field == null || (field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL)) == 0;

        if (fieldWritable) {
            if (setter != null && setterVisible && !setter.getDeclaringClass().isAnonymousClass()) {
                try {
                    return LOOKUP.unreflect(setter);
                } catch (IllegalAccessException e) {
                    throw new JsonbException("Error accessing setter '" + setter.getName() + "' declared in '" + setter
                            .getDeclaringClass() + "'", e);
                }
            }
            if (isFieldVisible(field, setter, strategy) && !field.getDeclaringClass().isAnonymousClass()) {
                try {
                    return LOOKUP.unreflectSetter(field);
                } catch (IllegalAccessException e) {
                    throw new JsonbException("Error accessing field '" + field.getName() + "' declared in '" + field
                            .getDeclaringClass() + "'", e);
                }
            }
        }

        return null;
    }

    private static boolean isFieldVisible(Field field, Method method, PropertyVisibilityStrategy strategy) {
        if (field == null) {
            return false;
        }
        boolean accessible = isVisible(strat -> strat.isVisible(field), method, strategy);
        //overridden by strategy, or anonymous class (readable by spec)
        if (accessible && (
                !Modifier.isPublic(field.getModifiers())
                        || field.getDeclaringClass().isAnonymousClass()
                        || isNotPublicAndNonNested(field.getDeclaringClass()))) {
            overrideAccessible(field);
        }
        return accessible;
    }

    private static boolean isNotPublicAndNonNested(Class<?> declaringClass) {
        return !declaringClass.isMemberClass() && !Modifier.isPublic(declaringClass.getModifiers());
    }

    private static boolean isMethodVisible(Method method, PropertyVisibilityStrategy strategy) {
        if (method == null || Modifier.isStatic(method.getModifiers())) {
            return false;
        }

        boolean accessible = isVisible(strat -> strat.isVisible(method), method, strategy);
        //overridden by strategy, anonymous class, or lambda
        if (accessible && (
                !Modifier.isPublic(method.getModifiers()) || method.getDeclaringClass().isAnonymousClass() || method
                        .getDeclaringClass().isSynthetic())) {
            overrideAccessible(method);
        }
        return accessible;
    }

    private static void overrideAccessible(AccessibleObject accessibleObject) {
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
    private static boolean isVisible(Predicate<PropertyVisibilityStrategy> visibilityCheckFunction,
                                     Method method,
                                     PropertyVisibilityStrategy strategy) {
        return strategy != null
                ? visibilityCheckFunction.test(strategy)
                : visibilityCheckFunction.test(new DefaultVisibilityStrategy(method));
    }

    private static final class DefaultVisibilityStrategy implements PropertyVisibilityStrategy {

        private final Method method;

        DefaultVisibilityStrategy(Method method) {
            this.method = method;
        }

        @Override
        public boolean isVisible(Field field) {
            //don't check field if getter is not visible (forced by spec)
            return (method == null || isVisible(method)) && Modifier.isPublic(field.getModifiers());
        }

        @Override
        public boolean isVisible(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    }

    public MethodHandle getGetValueHandle() {
        return getValueHandle;
    }

    public MethodHandle getSetValueHandle() {
        return setValueHandle;
    }

}
