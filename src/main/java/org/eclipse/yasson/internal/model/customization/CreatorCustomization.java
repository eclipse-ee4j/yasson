/*
 * Copyright (c) 2019, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.lang.reflect.Type;

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.model.PropertyModel;

/**
 * Customization for creator (constructor / factory methods) parameters.
 */
public class CreatorCustomization extends CustomizationBase {

    private final JsonbNumberFormatter numberFormatter;
    private final JsonbDateFormatter dateFormatter;
    private final boolean required;
    private final Type parameterType;
    private PropertyModel propertyModel;

    /**
     * Creates new creator customization instance.
     *
     * @param builder builder of the customization
     */
    private CreatorCustomization(Builder builder) {
        super(builder);
        this.numberFormatter = builder.numberFormatter;
        this.dateFormatter = builder.dateFormatter;
        this.required = builder.required;
        this.parameterType = builder.parameterType;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        if (numberFormatter != null) {
            return numberFormatter;
        } else if (propertyModel != null) {
            return propertyModel.getCustomization().getDeserializeNumberFormatter();
        }
        return null;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        if (dateFormatter != null) {
            return dateFormatter;
        } else if (propertyModel != null) {
            return propertyModel.getCustomization().getDeserializeDateFormatter();
        }
        return null;
    }

    @Override
    public AdapterBinding getDeserializeAdapterBinding() {
        if (inheritsPropertyBindings()) {
            return propertyModel.getCustomization().getDeserializeAdapterBinding();
        }
        return super.getDeserializeAdapterBinding();
    }

    @Override
    public DeserializerBinding<?> getDeserializerBinding() {
        if (inheritsPropertyBindings()) {
            return propertyModel.getCustomization().getDeserializerBinding();
        }
        return super.getDeserializerBinding();
    }

    /**
     * Whether the parameter takes the adapter and deserializer of the referenced property. It does when it declares
     * neither itself and the property is deserialized as the parameter's type, which the property's ones produce.
     */
    private boolean inheritsPropertyBindings() {
        return super.getDeserializeAdapterBinding() == null
                && super.getDeserializerBinding() == null
                && propertyModel != null
                && propertyModel.getPropertyDeserializationType().equals(parameterType);
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported for creator parameters.");
    }

    /**
     * Set property referenced model.
     *
     * @param propertyModel referenced property model
     */
    public void setPropertyModel(PropertyModel propertyModel) {
        this.propertyModel = propertyModel;
    }

    public boolean isRequired() {
        return required;
    }

    public static final class Builder extends CustomizationBase.Builder<Builder, CreatorCustomization> {

        private JsonbNumberFormatter numberFormatter;
        private JsonbDateFormatter dateFormatter;
        private boolean required = false;
        private Type parameterType;

        private Builder() {
        }

        @Override
        public Builder of(CreatorCustomization customization) {
            super.of(customization);
            numberFormatter = customization.numberFormatter;
            dateFormatter = customization.dateFormatter;
            parameterType = customization.parameterType;
            return this;
        }

        public Builder numberFormatter(JsonbNumberFormatter numberFormatter) {
            this.numberFormatter = numberFormatter;
            return this;
        }

        public Builder dateFormatter(JsonbDateFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder parameterType(Type parameterType) {
            this.parameterType = parameterType;
            return this;
        }

        @Override
        public CreatorCustomization build() {
            return new CreatorCustomization(this);
        }

    }

}
