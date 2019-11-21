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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.json.bind.config.PropertyNamingStrategy;

import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.StrategiesProvider;

/**
 * A model for Java class.
 */
public class ClassModel {

    private final Class<?> clazz;

    private final ClassCustomization classCustomization;

    private final ClassModel parentClassModel;

    private final Constructor<?> defaultConstructor;

    /**
     * A map of all class properties, including properties from superclasses. Used to access by name.
     */
    private Map<String, PropertyModel> properties;

    /**
     * Sorted properties according to sorting strategy. Used for serialization property ordering.
     */
    private PropertyModel[] sortedProperties;

    private final PropertyNamingStrategy propertyNamingStrategy;

    /**
     * Gets a property model by default (non customized) name.
     *
     * @param name A name as parsed from field / getter / setter without annotation customizing.
     * @return Property model.
     */
    public PropertyModel getPropertyModel(String name) {
        return properties.get(name);
    }

    /**
     * Create instance of class model.
     *
     * @param clazz                  Class to model.
     * @param customization          Customization of the class parsed from annotations.
     * @param parentClassModel       Class model of parent class.
     * @param propertyNamingStrategy Property naming strategy.
     */
    public ClassModel(Class<?> clazz,
                      ClassCustomization customization,
                      ClassModel parentClassModel,
                      PropertyNamingStrategy propertyNamingStrategy) {
        this.clazz = clazz;
        this.classCustomization = customization;
        this.parentClassModel = parentClassModel;
        this.propertyNamingStrategy = propertyNamingStrategy;
        this.defaultConstructor = ReflectionUtils.getDefaultConstructor(clazz, false);
        setProperties(new ArrayList<>());
    }

    /**
     * Search for field in this class model and superclasses of its class.
     *
     * @param jsonReadName name as it appears in JSON during reading.
     * @return PropertyModel if found.
     */
    public PropertyModel findPropertyModelByJsonReadName(String jsonReadName) {
        Objects.requireNonNull(jsonReadName);
        return searchProperty(this, jsonReadName);
    }

    private PropertyModel searchProperty(ClassModel classModel, String jsonReadName) {
        //Standard javabean properties without overridden name (most of the cases)
        final PropertyModel result = classModel.getPropertyModel(jsonReadName);
        if (result != null && result.getPropertyName().equals(result.getReadName())) {
            return result;
        }
        //Search for overridden name on setter with @JsonbProperty annotation
        for (PropertyModel propertyModel : properties.values()) {
            if (equalsReadName(jsonReadName, propertyModel)) {
                return propertyModel;
            }
        }
        //property not found
        return null;
    }

    /**
     * Check if name is equal according to property strategy. In case of {@link CaseInsensitiveStrategy} ignore case.
     * User can provide own strategy implementation, cast to custom interface is not an option.
     *
     * @return True if names are equal.
     */
    private boolean equalsReadName(String jsonName, PropertyModel propertyModel) {
        final String propertyReadName = propertyModel.getReadName();
        if (propertyNamingStrategy == StrategiesProvider.CASE_INSENSITIVE_STRATEGY) {
            return jsonName.equalsIgnoreCase(propertyReadName);
        }
        return jsonName.equals(propertyReadName);
    }

    /**
     * Gets type.
     *
     * @return Type.
     */
    public Class<?> getType() {
        return clazz;
    }

    /**
     * Introspected customization for a class.
     *
     * @return Immutable class customization.
     */
    public ClassCustomization getClassCustomization() {
        return classCustomization;
    }

    /**
     * Class model of parent class if present.
     *
     * @return class model of a parent
     */
    public ClassModel getParentClassModel() {
        return parentClassModel;
    }

    /**
     * Get sorted class properties copy, combination of field and its getter / setter, javabeans alike.
     *
     * @return sorted class properties.
     */
    public PropertyModel[] getSortedProperties() {
        return sortedProperties;
    }

    /**
     * Sets parsed properties of the class.
     *
     * @param parsedProperties class properties
     */
    public void setProperties(List<PropertyModel> parsedProperties) {
        sortedProperties = parsedProperties.toArray(new PropertyModel[] {});
        this.properties = parsedProperties.stream().collect(Collectors.toMap(PropertyModel::getPropertyName, (mod) -> mod));
    }

    /**
     * Get class properties copy, combination of field and its getter / setter, javabeans alike.
     *
     * @return class properties.
     */
    public Map<String, PropertyModel> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Default no argument constructor of the class used for deserialization.
     *
     * @return default constructor
     */
    public Constructor<?> getDefaultConstructor() {
        return defaultConstructor;
    }
}
