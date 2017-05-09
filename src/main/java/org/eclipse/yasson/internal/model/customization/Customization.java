/*******************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization configuration for class or field.
 * Configuration parsed from annotation is put here.
 * Immutable.
 *
 * @author Roman Grigoriadi
 */
public abstract class Customization {

    private final AdapterBinding adapterBinding;

    private final SerializerBinding serializerBinding;

    private final DeserializerBinding deserializerBinding;

    private final boolean nillable;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    public Customization(CustomizationBuilder builder) {
        this.nillable = builder.isNillable();
        this.adapterBinding = builder.getAdapterInfo();
        this.serializerBinding = builder.getSerializerBinding();
        this.deserializerBinding = builder.getDeserializerBinding();
    }

    /**
     * Copy constructor.
     *
     * @param other other customization instance
     */
    public Customization(Customization other) {
        this.nillable = other.isNillable();
        this.adapterBinding = other.getAdapterBinding();
        this.serializerBinding = other.getSerializerBinding();
        this.deserializerBinding = other.getDeserializerBinding();
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
     * Adapter wrapper class with resolved generic information.
     *
     * @return components wrapper
     */
    public AdapterBinding getAdapterBinding() {
        return adapterBinding;
    }

    /**
     * Serializer wrapper with resolved generic info.
     *
     * @return serializer wrapper
     */
    public SerializerBinding getSerializerBinding() {
        return serializerBinding;
    }

    /**
     * Deserializer wrapper with resolved generic info.
     *
     * @return deserializer wrapper
     */
    public DeserializerBinding getDeserializerBinding() {
        return deserializerBinding;
    }

    /**
     * Number formatter for formatting numbers during serialization process. It could be the same formatter instance used for deserialization
     * (returned by {@link #getDeserializeNumberFormatter()}
     *
     * @return number formatter
     */
    public abstract JsonbNumberFormatter getSerializeNumberFormatter();

    /**
     * Number formatter for formatting numbers during deserialization process. It could be the same formatter instance used for serialization
     * (returned by {@link #getSerializeNumberFormatter()}
     *
     * @return number formatter
     */
    public abstract JsonbNumberFormatter getDeserializeNumberFormatter();

    /**
     * Date formatter for formatting date values during serialization process. It could be the same formatter instance used for deserialization
     * (returned by {@link #getDeserializeDateFormatter()}. If not set, defaulted to <code>javax.json.bind.annotation.JsonbDateFormat.DEFAULT_FORMAT.
     * </code>
     *
     * @return date formatter
     */
    public abstract JsonbDateFormatter getSerializeDateFormatter();

    /**
     * Date formatter for formatting date values during deserialization process. It could be the same formatter instance used for serialization
     * (returned by {@link #getSerializeDateFormatter()}. If not set, defaulted to <code>javax.json.bind.annotation.JsonbDateFormat.DEFAULT_FORMAT.
     * </code>
     *
     * @return date formatter
     */
    public abstract JsonbDateFormatter getDeserializeDateFormatter();

    /**
     * The flag indicating whether the value of the underlying type/property should be processed during serialization process or not.
     *
     * @return  true indicates that the underlying type/property should be included in serialization process and false indicates it should not
     */
    public abstract boolean isReadTransient();

    /**
     * The flag indicating whether the value of the underlying type/property should be processed during deserialization process or not.
     *
     * @return  true indicates that the underlying type/property should be included in deserialization process and false indicates it should not
     */
    public abstract boolean isWriteTransient();
}
