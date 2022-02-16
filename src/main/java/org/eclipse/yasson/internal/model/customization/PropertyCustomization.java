/*
 * Copyright (c) 2015, 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;

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

    private final boolean readTransient;
    private final boolean writeTransient;

    private final Class<?> implementationClass;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    private PropertyCustomization(Builder builder) {
        super(builder);
        this.serializeAdapter = builder.serializeAdapter;
        this.deserializeAdapter = builder.deserializeAdapter;
        this.jsonReadName = builder.jsonReadName;
        this.jsonWriteName = builder.jsonWriteName;
        this.serializeNumberFormatter = builder.serializeNumberFormatter;
        this.deserializeNumberFormatter = builder.deserializeNumberFormatter;
        this.serializeDateFormatter = builder.serializeDateFormatter;
        this.deserializeDateFormatter = builder.deserializeDateFormatter;
        this.readTransient = builder.readTransient;
        this.writeTransient = builder.writeTransient;
        this.implementationClass = builder.implementationClass;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Name if specified for property setter with {@link jakarta.json.bind.annotation.JsonbProperty}.
     *
     * @return read name
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Name if specified for property getter with {@link jakarta.json.bind.annotation.JsonbProperty}.
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

    public static final class Builder extends CustomizationBase.Builder<Builder, PropertyCustomization> {

        private String jsonReadName;
        private String jsonWriteName;
        private JsonbNumberFormatter serializeNumberFormatter;
        private JsonbNumberFormatter deserializeNumberFormatter;
        private JsonbDateFormatter serializeDateFormatter;
        private JsonbDateFormatter deserializeDateFormatter;
        private AdapterBinding serializeAdapter;
        private AdapterBinding deserializeAdapter;
        private boolean readTransient;
        private boolean writeTransient;
        private Class<?> implementationClass;

        private Builder() {
        }

        @Override
        public Builder of(PropertyCustomization customization) {
            jsonReadName = customization.jsonReadName;
            jsonWriteName = customization.jsonWriteName;
            serializeNumberFormatter = customization.serializeNumberFormatter;
            deserializeNumberFormatter = customization.deserializeNumberFormatter;
            serializeDateFormatter = customization.serializeDateFormatter;
            deserializeDateFormatter = customization.deserializeDateFormatter;
            serializeAdapter = customization.serializeAdapter;
            deserializeAdapter = customization.deserializeAdapter;
            readTransient = customization.readTransient;
            writeTransient = customization.writeTransient;
            implementationClass = customization.implementationClass;
            return super.of(customization);
        }

        /**
         * Set a JSON property name used to read a property value from on deserialization.
         *
         * @param jsonReadName JSON property name
         */
        public Builder jsonReadName(String jsonReadName) {
            this.jsonReadName = jsonReadName;
            return this;
        }

        /**
         * Set a property name which is written to JSON document on serialization.
         *
         * @param jsonWriteName Property name.
         */
        public Builder jsonWriteName(String jsonWriteName) {
            this.jsonWriteName = jsonWriteName;
            return this;
        }

        /**
         * Set number formatter for formatting numbers during serialization process.
         *
         * @param serializeNumberFormatter Number formatter for formatting numbers during serialization process.
         */
        public Builder serializeNumberFormatter(JsonbNumberFormatter serializeNumberFormatter) {
            this.serializeNumberFormatter = serializeNumberFormatter;
            return this;
        }

        /**
         * Set number formatter for formatting numbers during deserialization process.
         *
         * @param deserializeNumberFormatter Number formatter for formatting numbers during deserialization process.
         */
        public Builder deserializeNumberFormatter(JsonbNumberFormatter deserializeNumberFormatter) {
            this.deserializeNumberFormatter = deserializeNumberFormatter;
            return this;
        }

        /**
         * Set date formatter for formatting dates during serialization process.
         *
         * @param serializeDateFormatter Date formatter for formatting dates during serialization process.
         */
        public Builder serializeDateFormatter(JsonbDateFormatter serializeDateFormatter) {
            this.serializeDateFormatter = serializeDateFormatter;
            return this;
        }

        /**
         * Set date formatter for formatting dates during deserialization process.
         *
         * @param deserializeDateFormatter Date formatter for formatting dates during deserialization process.
         */
        public Builder deserializeDateFormatter(JsonbDateFormatter deserializeDateFormatter) {
            this.deserializeDateFormatter = deserializeDateFormatter;
            return this;
        }

        public Builder serializeAdapter(AdapterBinding serializeAdapter) {
            this.serializeAdapter = serializeAdapter;
            return this;
        }

        public Builder deserializeAdapter(AdapterBinding deserializeAdapter) {
            this.deserializeAdapter = deserializeAdapter;
            return this;
        }

        /**
         * Sets a presence of <i>read transient</i> customization.
         *
         * @param readTransient Presence of <i>read transient</i> customization.
         */
        public Builder readTransient(boolean readTransient) {
            this.readTransient = readTransient;
            return this;
        }

        public boolean readTransient() {
            return readTransient;
        }

        /**
         * Sets a presence of <i>write transient</i> customization.
         *
         * @param writeTransient Presence of <i>write transient</i> customization.
         */
        public Builder writeTransient(boolean writeTransient) {
            this.writeTransient = writeTransient;
            return this;
        }

        public boolean writeTransient() {
            return writeTransient;
        }

        /**
         * Implementation class if property is interface type.
         *
         * @param implementationClass implementing property interface
         */
        public Builder implementationClass(Class<?> implementationClass) {
            this.implementationClass = implementationClass;
            return this;
        }

        @Override
        public PropertyCustomization build() {
            return new PropertyCustomization(this);
        }

    }

}
