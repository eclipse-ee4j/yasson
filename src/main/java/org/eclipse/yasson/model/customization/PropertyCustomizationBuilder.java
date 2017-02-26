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

package org.eclipse.yasson.model.customization;

import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * The property customization builder that would be used to build an instance of {@link PropertyCustomization} to ensure its immutability.
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class PropertyCustomizationBuilder extends CustomizationBuilder {

    private String jsonReadName;

    private String jsonWriteName;

    private JsonbNumberFormatter serializeNumberFormatter;

    private JsonbNumberFormatter deserializeNumberFormatter;

    /**
     * Creates a customization for class properties.
     *
     * @return A new instance of {@link PropertyCustomization}
     */
    public PropertyCustomization buildPropertyCustomization() {
        return new PropertyCustomization(this);
    }

    /**
     * Gets number formatter for formatting numbers during serialization process.
     *
     * @return Number formatter for formatting numbers during serialization process.
     */
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        return serializeNumberFormatter;
    }

    /**
     * Sets number formatter for formatting numbers during serialization process.
     *
     * @param serializeNumberFormatter Number formatter for formatting numbers during serialization process.
     */
    public void setSerializeNumberFormatter(JsonbNumberFormatter serializeNumberFormatter) {
        this.serializeNumberFormatter = serializeNumberFormatter;
    }

    /**
     * Gets number formatter for formatting numbers during deserialization process.
     *
     * @return Number formatter for formatting numbers during deserialization process.
     */
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        return deserializeNumberFormatter;
    }

    /**
     * Sets number formatter for formatting numbers during deserialization process.
     *
     * @param deserializeNumberFormatter Number formatter for formatting numbers during deserialization process.
     */
    public void setDeserializeNumberFormatter(JsonbNumberFormatter deserializeNumberFormatter) {
        this.deserializeNumberFormatter = deserializeNumberFormatter;
    }

    /**
     * Sets a JSON property name used to read a property value from on deserialization.
     *
     * @return JSON property name
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Sets a JSON property name used to read a property value from on deserialization.
     *
     * @param jsonReadName JSON property name
     */
    public void setJsonReadName(String jsonReadName) {
        this.jsonReadName = jsonReadName;
    }

    /**
     * Gets a property name which is written to JSON document on serialization.
     *
     * @return Property name.
     */
    public String getJsonWriteName() {
        return jsonWriteName;
    }

    /**
     * Sets a property name which is written to JSON document on serialization.
     *
     * @param jsonWriteName Property name.
     */
    public void setJsonWriteName(String jsonWriteName) {
        this.jsonWriteName = jsonWriteName;
    }

}
