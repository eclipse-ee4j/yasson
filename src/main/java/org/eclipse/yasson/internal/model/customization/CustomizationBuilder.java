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

package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;
import org.eclipse.yasson.internal.model.JsonbCreator;

/**
 * Abstract base builder for ensuring immutable state of {@link Customization} objects.
 *
 * @author Roman Grigoriadi
 */
public abstract class CustomizationBuilder {

    private boolean nillable;

    private AdapterBinding adapterInfo;

    private SerializerBinding serializerBinding;

    private DeserializerBinding deserializerBinding;

    private String[] propertyOrder;

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
     * Gets an components.
     *
     * @return Adapter.
     */
    public AdapterBinding getAdapterInfo() {
        return adapterInfo;
    }

    /**
     * Sets an components.
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
