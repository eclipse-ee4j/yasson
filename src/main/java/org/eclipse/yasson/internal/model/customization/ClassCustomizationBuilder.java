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
 *      Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/
package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * The customization builder that would be used to build an instance of {@link ClassCustomization} to ensure its immutability.
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class ClassCustomizationBuilder extends CustomizationBuilder {

    private JsonbCreator jsonbCreator;

    /**
     * The class level number formatter that would be used by default for all number properties that don't have a dedicated number formatter
     * annotation.
     */
    private JsonbNumberFormatter numberFormatter;

    /**
     * The class level date formatter that would be used by default for all date properties that don't have a dedicated date formatter annotation.
     */
    private JsonbDateFormatter dateFormatter;

    /**
     * Creates a customization for class properties.
     *
     * @return A new instance of {@link PropertyCustomization}
     */
    public ClassCustomization buildClassCustomization() {
        return new ClassCustomization(this);
    }

    /**
     * Returns the default number formatter instance that would be used for all number properties that don't have a dedicated number formatter.
     *
     * @return the default number formatter instance that would be used for all number properties that don't have a dedicated number formatter
     */
    public JsonbNumberFormatter getNumberFormatter() {
        return numberFormatter;
    }

    /**
     * Sets the default number formatter instance that would be used for all number properties that don't have a dedicated number formatter.
     *
     * @param numberFormatter the default number formatter instance that would be used for all number properties that don't have a dedicated number
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
}
