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

package org.eclipse.yasson.model;

import org.eclipse.yasson.internal.adapter.AdapterBinding;
import org.eclipse.yasson.internal.adapter.DeserializerBinding;
import org.eclipse.yasson.internal.adapter.SerializerBinding;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Builder for ensuring immutable state of {@link Customization} objects.
 *
 * @author Roman Grigoriadi
 */
public class CustomizationBuilder {

    private boolean nillable;

    private boolean jsonbTransient;

    private String jsonReadName;

    private String jsonWriteName;

    private AdapterBinding adapterInfo;

    private SerializerBinding serializerBinding;

    private DeserializerBinding deserializerBinding;

    private JsonbDateFormatter dateFormatter;

    private JsonbNumberFormatter numberFormat;

    private JsonbCreator creator;

    private String[] propertyOrder;

    /**
     * Creates a customization for class properties.
     *
     * @return A new instance of {@link PropertyCustomization}
     */
    public PropertyCustomization buildPropertyCustomization() {
        return new PropertyCustomization(this);
    }

    /**
     * Creates customization for class.
     *
     * @return A new instance of {@link ClassCustomization}
     */
    public ClassCustomization buildClassCustomization() {
        return new ClassCustomization(this);
    }

    /**
     * Returns true if <i>nillable</i> customization is present.
     *
     * @return True if <i>nillable</i> customization is present.
     */
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Sets a presence of <i>nillable</i> customization.
     *
     * @param nillable Presence of <i>nillable</i> customization.
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    /**
     * Returns true if <i>transient</i> customization is present.
     *
     * @return True if <i>transient</i> customization is present.
     */
    public boolean isJsonbTransient() {
        return jsonbTransient;
    }

    /**
     * Sets a presence of <i>transient</i> customization.
     *
     * @param jsonbTransient Presence of <i>transient</i> customization.
     */
    public void setJsonbTransient(boolean jsonbTransient) {
        this.jsonbTransient = jsonbTransient;
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
     * Gets an adapter.
     *
     * @return Adapter.
     */
    public AdapterBinding getAdapterInfo() {
        return adapterInfo;
    }

    /**
     * Sets an adapter.
     *
     * @param adapterInfo Adapter.
     */
    public void setAdapterInfo(AdapterBinding adapterInfo) {
        this.adapterInfo = adapterInfo;
    }

    /**
     * Gets meta info for user serializers.
     *
     * @return Serializer info
     */
    public SerializerBinding getSerializerBinding() {
        return serializerBinding;
    }

    /**
     * Sets serializer info.
     *
     * @param serializerBinding Serializer info to set.
     */
    public void setSerializerBinding(SerializerBinding serializerBinding) {
        this.serializerBinding = serializerBinding;
    }

    /**
     * Gets a deserializer.
     *
     * @return Deserializer.
     */
    public DeserializerBinding getDeserializerBinding() {
        return deserializerBinding;
    }

    /**
     * Sets a deserializer info.
     *
     * @param deserializerBinding Deserializer.
     */
    public void setDeserializerBinding(DeserializerBinding deserializerBinding) {
        this.deserializerBinding = deserializerBinding;
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
     * Gets number formatter for formatting numbers.
     *
     * @return Number format.
     */
    public JsonbNumberFormatter getNumberFormat() {
        return numberFormat;
    }

    /**
     * Sets number formatter for formatting numbers.
     *
     * @param numberFormat Number format.
     */
    public void setNumberFormat(JsonbNumberFormatter numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * Gets custom constructor or method for user instantiation.
     *
     * @return Custom creator.
     */
    public JsonbCreator getCreator() {
        return creator;
    }

    /**
     * Sets custom constructor or method for user instantiation.
     *
     * @param creator Creator to set.
     */
    public void setCreator(JsonbCreator creator) {
        this.creator = creator;
    }

    /**
     * Gets ordered list of property names.
     *
     * @return Sorted names of properties.
     */
    public String[] getPropertyOrder() {
        return propertyOrder;
    }

    /**
     * Sets a sorted list of property names.
     *
     * @param propertyOrder Array containing property names
     */
    public void setPropertyOrder(String[] propertyOrder) {
        this.propertyOrder = propertyOrder;
    }
}
