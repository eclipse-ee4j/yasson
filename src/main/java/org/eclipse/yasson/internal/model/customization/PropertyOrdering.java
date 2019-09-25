/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Gyúróczki Gergő
 ******************************************************************************/
package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;
import java.util.*;
import java.util.function.*;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

/**
 * Order properties in bean object. {@link javax.json.bind.annotation.JsonbPropertyOrder} have always precedence.
 * If configured with {@link JsonbConfig} provided property order strategy will be used.
 *
 * @author Roman Grigoriadi
 */
public class PropertyOrdering {

    private final Consumer<List<PropertyModel>> propertyOrderStrategy;

    /**
     * Creates a new instance.
     *
     * @param propertyOrderStrategy Property order strategy. Must be not null.
     */
    public PropertyOrdering(Consumer<List<PropertyModel>> propertyOrderStrategy) {
        this.propertyOrderStrategy = Objects.requireNonNull(propertyOrderStrategy);
    }

    /**
     * Sorts class properties either, by class {@link javax.json.bind.annotation.JsonbPropertyOrder} annotation,
     * or by {@link PropertyOrderStrategy} if set in {@link JsonbConfig}.
     *
     * @param properties Properties to sort.
     * @param classModel Class model.
     * @return Sorted list of properties.
     */
    public List<PropertyModel> orderProperties(List<PropertyModel> properties, ClassModel classModel) {
        Map<String, PropertyModel> byReadName = new HashMap<>();
        properties.forEach(propertyModel -> byReadName.put(propertyModel.getPropertyName(), propertyModel));

        String[] order = classModel.getClassCustomization().getPropertyOrder();
        List<PropertyModel> sortedProperties = new ArrayList<>();
        if (order != null) {
            //if @JsonbPropertyOrder annotation is defined on a class
            for (String propName : order) {
                final PropertyModel remove = byReadName.remove(propName);
                if (remove != null) {
                    sortedProperties.add(remove);
                }
            }
        }

        List<PropertyModel> readNamesToSort = new ArrayList<>(byReadName.values());
        propertyOrderStrategy.accept(readNamesToSort);
        sortedProperties.addAll(readNamesToSort);
        return sortedProperties;
    }
}