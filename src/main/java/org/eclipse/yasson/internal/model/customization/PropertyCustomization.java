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

package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.serializer.JsonbDateFormatter;
import org.eclipse.yasson.internal.serializer.JsonbNumberFormatter;

/**
 * Customization for a property of a class.
 */
public class PropertyCustomization extends CustomizationBase {

    private final String jsonReadName;
    private final String jsonWriteName;

    private final JsonbNumberFormatter serializeNumberFormatter;
    private final JsonbNumberFormatter deserializeNumberFormatter;

    private final JsonbDateFormatter serializeDateFormatter;
    private final JsonbDateFormatter deserializeDateFormatter;

    private final AdapterBinding serializeAdapter;
    private final AdapterBinding deserializeAdapter;

    private boolean readTransient;
    private boolean writeTransient;

    private final Class<?> implementationClass;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    public PropertyCustomization(PropertyCustomizationBuilder builder) {
        super(builder);
        this.serializeAdapter = builder.getSerializeAdapter();
        this.deserializeAdapter = builder.getDeserializeAdapter();
        this.jsonReadName = builder.getJsonReadName();
        this.jsonWriteName = builder.getJsonWriteName();
        this.serializeNumberFormatter = builder.getSerializeNumberFormatter();
        this.deserializeNumberFormatter = builder.getDeserializeNumberFormatter();
        this.serializeDateFormatter = builder.getSerializeDateFormatter();
        this.deserializeDateFormatter = builder.getDeserializeDateFormatter();
        this.readTransient = builder.isReadTransient();
        this.writeTransient = builder.isWriteTransient();
        this.implementationClass = builder.getImplementationClass();
    }

    /**
     * Name if specified for property setter with {@link javax.json.bind.annotation.JsonbProperty}.
     *
     * @return read name
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Name if specified for property getter with {@link javax.json.bind.annotation.JsonbProperty}.
     *
     * @return write name
     */
    public String getJsonWriteName() {
        return jsonWriteName;
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        return serializeNumberFormatter;
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        return deserializeNumberFormatter;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        return serializeDateFormatter;
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        return deserializeDateFormatter;
    }

    /**
     * The flag indicating whether the value of the underlying type/property should be processed during serialization process
     * or not.
     *
     * @return true indicates that the underlying type/property should be included in serialization process and false indicates
     * it should not
     */
    public boolean isReadTransient() {
        return readTransient;
    }

    /**
     * The flag indicating whether the value of the underlying type/property should be processed during deserialization process
     * or not.
     *
     * @return true indicates that the underlying type/property should be included in deserialization process and false
     * indicates it should not
     */
    public boolean isWriteTransient() {
        return writeTransient;
    }

    /**
     * Implementation class if property is interface type.
     *
     * @return class implementing property interface
     */
    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    @Override
    public AdapterBinding getDeserializeAdapterBinding() {
        return deserializeAdapter;
    }

    @Override
    public AdapterBinding getSerializeAdapterBinding() {
        return serializeAdapter;
    }

}
