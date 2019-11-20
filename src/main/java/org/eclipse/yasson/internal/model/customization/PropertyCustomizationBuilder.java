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

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * The property customization builder that would be used to build an instance of {@link PropertyCustomization} to ensure its
 * immutability.
 */
public class PropertyCustomizationBuilder extends CustomizationBuilder {

    private String jsonReadName;
    private String jsonWriteName;

    private JsonbNumberFormatter serializeNumberFormatter;
    private JsonbNumberFormatter deserializeNumberFormatter;

    private JsonbDateFormatter serializeDateFormatter;
    private JsonbDateFormatter deserializeDateFormatter;

    private boolean readTransient;
    private boolean writeTransient;

    private AdapterBinding serializeAdapter;
    private AdapterBinding deserializeAdapter;

    private Class implementationClass;

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
     * Gets date formatter for formatting dates during serialization process.
     *
     * @return date formatter for formatting dates during serialization process.
     */
    public JsonbDateFormatter getSerializeDateFormatter() {
        return serializeDateFormatter;
    }

    /**
     * Sets date formatter for formatting dates during serialization process.
     *
     * @param serializeDateFormatter Date formatter for formatting dates during serialization process.
     */
    public void setSerializeDateFormatter(JsonbDateFormatter serializeDateFormatter) {
        this.serializeDateFormatter = serializeDateFormatter;
    }

    /**
     * Gets date formatter for formatting dates during deserialization process.
     *
     * @return Date formatter for formatting dates during deserialization process.
     */
    public JsonbDateFormatter getDeserializeDateFormatter() {
        return deserializeDateFormatter;
    }

    /**
     * Sets date formatter for formatting dates during deserialization process.
     *
     * @param deserializeDateFormatter Date formatter for formatting dates during deserialization process.
     */
    public void setDeserializeDateFormatter(JsonbDateFormatter deserializeDateFormatter) {
        this.deserializeDateFormatter = deserializeDateFormatter;
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

    /**
     * Returns true if <i>read transient</i> customization is present.
     *
     * @return True if <i>read transient</i> customization is present.
     */
    public boolean isReadTransient() {
        return readTransient;
    }

    /**
     * Sets a presence of <i>read transient</i> customization.
     *
     * @param readTransient Presence of <i>read transient</i> customization.
     */
    public void setReadTransient(boolean readTransient) {
        this.readTransient = readTransient;
    }

    /**
     * Returns true if <i>write transient</i> customization is present.
     *
     * @return True if <i>write transient</i> customization is present.
     */
    public boolean isWriteTransient() {
        return writeTransient;
    }

    /**
     * Sets a presence of <i>write transient</i> customization.
     *
     * @param writeTransient Presence of <i>write transient</i> customization.
     */
    public void setWriteTransient(boolean writeTransient) {
        this.writeTransient = writeTransient;
    }

    /**
     * Implementation class if property is interface type.
     *
     * @return class implementing property interface
     */
    public Class getImplementationClass() {
        return implementationClass;
    }

    /**
     * Implementation class if property is interface type.
     *
     * @param implementationClass implementing property interface
     */
    public void setImplementationClass(Class implementationClass) {
        this.implementationClass = implementationClass;
    }

    @Override
    public void setAdapterInfo(AdapterBinding adapterInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdapterBinding getAdapterInfo() {
        return null;
    }

    public AdapterBinding getSerializeAdapter() {
        return serializeAdapter;
    }

    public void setSerializeAdapter(AdapterBinding adapter) {
        this.serializeAdapter = adapter;
    }

    public AdapterBinding getDeserializeAdapter() {
        return deserializeAdapter;
    }

    public void setDeserializeAdapter(AdapterBinding adapter) {
        this.deserializeAdapter = adapter;
    }
}
