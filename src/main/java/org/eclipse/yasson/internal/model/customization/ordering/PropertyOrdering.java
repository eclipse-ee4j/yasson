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
package org.eclipse.yasson.internal.model.customization.ordering;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.Property;
import org.eclipse.yasson.internal.model.PropertyModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Order properties in bean object. {@link javax.json.bind.annotation.JsonbPropertyOrder} have always precedence.
 * If configured with {@link JsonbConfig} provided property order strategy will be used.
 *
 * @author Roman Grigoriadi
 */
public class PropertyOrdering {

    private PropOrderStrategy propertyOrderStrategy;

    /**
     * Creates a new instance.
     *
     * @param propertyOrderStrategy Property order strategy. Must be not null.
     */
    public PropertyOrdering(PropOrderStrategy propertyOrderStrategy) {
        Objects.requireNonNull(propertyOrderStrategy);
        this.propertyOrderStrategy = propertyOrderStrategy;
    }

    /**
     * Sorts class properties either, by class {@link javax.json.bind.annotation.JsonbPropertyOrder} annotation,
     * or by {@link PropertyOrderStrategy} if set in {@link JsonbConfig}.
     *
     * @param properties Properties to sort.
     * @param classModel Class model.
     * @param jsonbContext jsonb context.
     * @return Sorted list of properties.
     */
    public List<PropertyModel> orderProperties(Map<String, Property> properties, ClassModel classModel, JsonbContext jsonbContext) {
        String[] order = classModel.getClassCustomization().getPropertyOrder();
        if (order != null) {
            //if @JsonbPropertyOrder annotation is defined on a class
            List<PropertyModel> sortedProperties = new ArrayList<>();
            for (String propName : order) {
                final Property remove = properties.remove(propName);
                if (remove != null) {
                    sortedProperties.add(new PropertyModel(classModel, remove, jsonbContext));
                }
            }
            /* TODO currently disabled, should remaining fields (unspecified in JsonbPropertyOrder) appear in json?
            for (Map.Entry<String, PropertyModel> entry : properties.entrySet()) {
                sortedProperties.add(entry.getValue());
            }*/
            return sortedProperties;
        }

        //No annotation, check JsonbConfig for ordering strategy use LEXICOGRAPHICAL as default
        return propertyOrderStrategy.sortProperties(properties.values().stream()
                .map((prop)->new PropertyModel(classModel, prop, jsonbContext))
                .collect(Collectors.toList()));
    }

    /**
     * Returns a property order strategy from {@link JsonbConfig}.
     *
     * @return {@link PropOrderStrategy} or null if not present.
     */
    public PropOrderStrategy getPropertyOrderStrategy() {
        return propertyOrderStrategy;
    }
}
