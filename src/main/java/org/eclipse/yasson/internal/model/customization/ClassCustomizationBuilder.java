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

package org.eclipse.yasson.internal.model.customization;

import javax.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * The customization builder that would be used to build an instance of {@link ClassCustomization} to ensure its immutability.
 */
public class ClassCustomizationBuilder extends CustomizationBuilder {

    private JsonbCreator jsonbCreator;

    /**
     * The class level number formatter that would be used by default for all number properties that don't have a dedicated
     * number formatter
     * annotation.
     */
    private JsonbNumberFormatter numberFormatter;

    /**
     * The class level date formatter that would be used by default for all date properties that don't have a dedicated date
     * formatter annotation.
     */
    private JsonbDateFormatter dateFormatter;

    /**
     * The class or package level property visibility strategy.
     */
    private PropertyVisibilityStrategy propertyVisibilityStrategy;

    /**
     * Creates a customization for class properties.
     *
     * @return A new instance of {@link PropertyCustomization}
     */
    public ClassCustomization buildClassCustomization() {
        return new ClassCustomization(this);
    }

    /**
     * Returns the default number formatter instance that would be used for all number properties that don't have a dedicated
     * number formatter.
     *
     * @return the default number formatter instance that would be used for all number properties that don't have a dedicated
     * number formatter
     */
    public JsonbNumberFormatter getNumberFormatter() {
        return numberFormatter;
    }

    /**
     * Sets the default number formatter instance that would be used for all number properties that don't have a dedicated
     * number formatter.
     *
     * @param numberFormatter the default number formatter instance that would be used for all number properties that don't
     *                        have a dedicated number
     *                        formatter.
     */
    public void setNumberFormatter(JsonbNumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    /**
     * Gets a date format for formatting dates.
     *
     * @return Date format.
     */
    public JsonbDateFormatter getDateFormatter() {
        return dateFormatter;
    }

    /**
     * Sets date format for formatting dates.
     *
     * @param dateFormatter Date format.
     */
    public void setDateFormatter(JsonbDateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    /**
     * Gets custom constructor or method for user instantiation.
     *
     * @return Custom creator.
     */
    public JsonbCreator getCreator() {
        return jsonbCreator;
    }

    /**
     * Sets custom constructor or method for user instantiation.
     *
     * @param jsonbCreator Creator to set.
     */
    public void setCreator(JsonbCreator jsonbCreator) {
        this.jsonbCreator = jsonbCreator;
    }

    /**
     * Property visibility strategy for given class.
     *
     * @return property visibility strategy
     */
    public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
        return propertyVisibilityStrategy;
    }

    /**
     * Sets custom property visibility strategy.
     *
     * @param propertyVisibilityStrategy strategy
     */
    public void setPropertyVisibilityStrategy(PropertyVisibilityStrategy propertyVisibilityStrategy) {
        this.propertyVisibilityStrategy = propertyVisibilityStrategy;
    }
}
