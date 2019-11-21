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

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.serializer.JsonbSerializer;

import org.eclipse.yasson.internal.AnnotationIntrospector;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.customization.PropertyCustomization;
import org.eclipse.yasson.internal.model.customization.PropertyCustomizationBuilder;
import org.eclipse.yasson.internal.serializer.AdaptedObjectSerializer;
import org.eclipse.yasson.internal.serializer.DefaultSerializers;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;
import org.eclipse.yasson.internal.serializer.SerializerProviderWrapper;
import org.eclipse.yasson.internal.serializer.UserSerializerSerializer;

/**
 * A model for class property.
 * Property is JavaBean alike meta information field / getter / setter of a property in class.
 */
public class PropertyModel implements Comparable<PropertyModel> {

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

    private final PropertyValuePropagation propagation;

    private final JsonbSerializer<?> propertySerializer;

    private final Type getterMethodType;

    private final Type setterMethodType;
    
    /**
     * Create a new PropertyModel that merges two existing PropertyModel that have identical read/write names.
     * The input PropertyModel objects MUST be equal (a.equals(b) == true)
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
        this.propagation = new ReflectionPropagation(property,
                classModel.getClassCustomization().getPropertyVisibilityStrategy());
        this.propertySerializer = resolveCachedSerializer();
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
        this.propagation = new ReflectionPropagation(property,
                                                     classModel.getClassCustomization().getPropertyVisibilityStrategy());
        this.getterMethodType = propagation.isGetterVisible() ? property.getGetterType() : null;
        this.setterMethodType = propagation.isSetterVisible() ? property.getSetterType() : null;
        this.customization = introspectCustomization(property, jsonbContext);
        this.readName = calculateReadWriteName(customization.getJsonReadName(),
                                               jsonbContext.getConfigProperties().getPropertyNamingStrategy());
        this.writeName = calculateReadWriteName(customization.getJsonWriteName(),
                                                jsonbContext.getConfigProperties().getPropertyNamingStrategy());
        this.propertySerializer = resolveCachedSerializer();
    }

    /**
     * Try to cache serializer for this bean property. Only if type cannot be changed during runtime.
     *
     * @return serializer instance to be cached
     */
    @SuppressWarnings("unchecked")
    private JsonbSerializer<?> resolveCachedSerializer() {
        Type serializationType = getPropertySerializationType();
        if (!ReflectionUtils.isResolvedType(serializationType)) {
            return null;
        }
        if (customization.getSerializeAdapterBinding() != null) {
            return new AdaptedObjectSerializer<>(classModel, customization.getSerializeAdapterBinding());
        }
        if (customization.getSerializerBinding() != null) {
            return new UserSerializerSerializer<>(classModel, customization.getSerializerBinding().getJsonbSerializer());
        }

        final Class<?> propertyRawType = ReflectionUtils.getRawType(serializationType);
        final Optional<SerializerProviderWrapper> valueSerializerProvider = DefaultSerializers.getInstance()
                .findValueSerializerProvider(propertyRawType);
        if (valueSerializerProvider.isPresent()) {
            return valueSerializerProvider.get().getSerializerProvider().provideSerializer(customization);
        }

        return null;
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
        final SerializerBinding serializerBinding = jsonbContext.getAnnotationIntrospector().getSerializerBinding(property);
        if (serializerBinding != null) {
            return serializerBinding;
        }
        return jsonbContext.getComponentMatcher().getSerializerBinding(getPropertySerializationType(), null).orElse(null);
    }

    private PropertyCustomization introspectCustomization(Property property, JsonbContext jsonbContext) {
        final AnnotationIntrospector introspector = jsonbContext.getAnnotationIntrospector();
        final PropertyCustomizationBuilder builder = new PropertyCustomizationBuilder();
        //drop all other annotations for transient properties
        EnumSet<AnnotationTarget> transientInfo = introspector.getJsonbTransientCategorized(property);
        if (transientInfo.size() != 0) {
            builder.setReadTransient(transientInfo.contains(AnnotationTarget.GETTER));
            builder.setWriteTransient(transientInfo.contains(AnnotationTarget.SETTER));

            if (transientInfo.contains(AnnotationTarget.PROPERTY)) {
                if (!transientInfo.contains(AnnotationTarget.GETTER)) {
                    builder.setReadTransient(true);
                }
                if (!transientInfo.contains(AnnotationTarget.SETTER)) {
                    builder.setWriteTransient(true);
                }
            }

            if (builder.isReadTransient()) {
                introspector.checkTransientIncompatible(property.getFieldElement());
                introspector.checkTransientIncompatible(property.getGetterElement());
            }
            if (builder.isWriteTransient()) {
                introspector.checkTransientIncompatible(property.getFieldElement());
                introspector.checkTransientIncompatible(property.getSetterElement());
            }
        }

        if (!builder.isReadTransient()) {
            builder.setJsonWriteName(introspector.getJsonbPropertyJsonWriteName(property));
            builder.setNillable(introspector.isPropertyNillable(property)
                                        .orElse(classModel.getClassCustomization().isNillable()));
            builder.setSerializerBinding(getUserSerializerBinding(property, jsonbContext));
        }

        if (!builder.isWriteTransient()) {
            builder.setJsonReadName(introspector.getJsonbPropertyJsonReadName(property));
            builder.setDeserializerBinding(introspector.getDeserializerBinding(property));
        }

        final AdapterBinding adapterBinding = jsonbContext.getAnnotationIntrospector().getAdapterBinding(property);
        if (adapterBinding != null) {
            builder.setSerializeAdapter(adapterBinding);
            builder.setDeserializeAdapter(adapterBinding);
        } else {
            builder.setSerializeAdapter(jsonbContext.getComponentMatcher()
                                                .getSerializeAdapterBinding(getPropertySerializationType(), null).orElse(null));
            builder.setDeserializeAdapter(jsonbContext.getComponentMatcher()
                                                  .getDeserializeAdapterBinding(getPropertyDeserializationType(), null)
                                                  .orElse(null));
        }

        introspectDateFormatter(property, introspector, builder, jsonbContext);
        introspectNumberFormatter(property, introspector, builder);
        builder.setImplementationClass(introspector.getImplementationClass(property));

        return builder.buildPropertyCustomization();
    }

    private void introspectDateFormatter(Property property,
                                         AnnotationIntrospector introspector,
                                         PropertyCustomizationBuilder builder,
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

        if (!builder.isReadTransient()) {
            final JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(jsonDateFormatCategorized,
                                                                                  AnnotationTarget.GETTER,
                                                                                  AnnotationTarget.PROPERTY,
                                                                                  AnnotationTarget.CLASS);

            builder.setSerializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
        }

        if (!builder.isWriteTransient()) {
            final JsonbDateFormatter dateFormatter = getTargetForMostPreciseScope(jsonDateFormatCategorized,
                                                                                  AnnotationTarget.SETTER,
                                                                                  AnnotationTarget.PROPERTY,
                                                                                  AnnotationTarget.CLASS);

            builder.setDeserializeDateFormatter(dateFormatter != null ? dateFormatter : configDateFormatter);
        }
    }

    private void introspectNumberFormatter(Property property,
                                           AnnotationIntrospector introspector,
                                           PropertyCustomizationBuilder builder) {
        /*
         * If @JsonbNumberFormat is placed on getter implementation must use this format on serialization.
         * If @JsonbNumberFormat is placed on setter implementation must use this format on deserialization.
         * If @JsonbNumberFormat is placed on field implementation must use this format on serialization and deserialization.
         *
         * Priority from high to low is getter / setter > field > class > package > global configuration
         */
        Map<AnnotationTarget, JsonbNumberFormatter> jsonNumberFormatCategorized = introspector.getJsonNumberFormatter(property);

        if (!builder.isReadTransient()) {
            builder.setSerializeNumberFormatter(getTargetForMostPreciseScope(jsonNumberFormatCategorized,
                                                                             AnnotationTarget.GETTER,
                                                                             AnnotationTarget.PROPERTY,
                                                                             AnnotationTarget.CLASS));
        }

        if (!builder.isWriteTransient()) {
            builder.setDeserializeNumberFormatter(getTargetForMostPreciseScope(jsonNumberFormatCategorized,
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
    private <T> T getTargetForMostPreciseScope(Map<AnnotationTarget, T> collectedAnnotations, AnnotationTarget... targets) {
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
        return propagation.getValue(object);
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
        propagation.setValue(object, value);
    }

    /**
     * Property is readable. Based on access policy and java field modifiers.
     *
     * @return true if can be serialized to JSON
     */
    public boolean isReadable() {
        return !customization.isReadTransient() && propagation.isReadable();
    }

    /**
     * Property is writable. Based on access policy and java field modifiers.
     *
     * @return true if can be deserialized from JSON
     */
    public boolean isWritable() {
        return !customization.isWriteTransient() && propagation.isWritable();
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
        if (compare == 0) {
            compare = writeName.compareTo(o.writeName);
        }
        return compare;
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
     * Gets serializer.
     *
     * @return Serializer.
     */
    public JsonbSerializer<?> getPropertySerializer() {
        return propertySerializer;
    }

    /**
     * If customized by JsonbPropertyAnnotation, than is used, otherwise use strategy to translate.
     * Since this is cached for performance reasons strategy has to be consistent
     * with calculated values for same input.
     */
    private String calculateReadWriteName(String readWriteName, PropertyNamingStrategy strategy) {
        return readWriteName != null ? readWriteName : strategy.translateName(propertyName);
    }

    /**
     * Wrapper object of {@code java.lang.reflect} representations of this javabean property.
     *
     * @return Property model
     */
    public PropertyValuePropagation getPropagation() {
        return propagation;
    }
}
