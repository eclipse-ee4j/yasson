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

import jakarta.json.bind.config.PropertyVisibilityStrategy;

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.model.JsonbCreator;

/**
 * Customization which could be applied on a class or package level.
 */
public class ClassCustomization extends CustomizationBase {

    private static final ClassCustomization EMPTY = new ClassCustomization(new Builder());

    private final JsonbCreator creator;
    private final String[] propertyOrder;
    private final JsonbNumberFormatter numberFormatter;
    private final JsonbDateFormatter dateTimeFormatter;
    private final PropertyVisibilityStrategy propertyVisibilityStrategy;
    private final TypeInheritanceConfiguration typeInheritanceConfiguration;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    private ClassCustomization(Builder builder) {
        super(builder);
        this.creator = builder.creator;
        this.propertyOrder = builder.propertyOrder;
        this.numberFormatter = builder.numberFormatter;
        this.dateTimeFormatter = builder.dateTimeFormatter;
        this.propertyVisibilityStrategy = builder.propertyVisibilityStrategy;
        this.typeInheritanceConfiguration = builder.typeInheritanceConfiguration;
    }

    public static ClassCustomization empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns instance of {@link JsonbCreator}.
     *
     * @return instance of creator
     */
    public JsonbCreator getCreator() {
        return creator;
    }

    /**
     * Names of properties to sort with.
     *
     * @return sorted names of properties
     */
    public String[] getPropertyOrder() {
        return propertyOrder;
    }

    /**
     * Property visibility strategy for this class model.
     *
     * @return visibility strategy
     */
    public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
        return propertyVisibilityStrategy;
    }

    @Override
    public JsonbNumberFormatter getSerializeNumberFormatter() {
        return numberFormatter;
    }

    @Override
    public JsonbNumberFormatter getDeserializeNumberFormatter() {
        return numberFormatter;
    }

    @Override
    public JsonbDateFormatter getSerializeDateFormatter() {
        return dateTimeFormatter;
    }

    @Override
    public JsonbDateFormatter getDeserializeDateFormatter() {
        return dateTimeFormatter;
    }

    public TypeInheritanceConfiguration getPolymorphismConfig() {
        return typeInheritanceConfiguration;
    }

    /**
     * The customization builder that would be used to build an instance of {@link ClassCustomization} to ensure its immutability.
     */
    public static class Builder extends CustomizationBase.Builder<Builder, ClassCustomization> {

        private JsonbCreator creator;
        private String[] propertyOrder;
        private JsonbNumberFormatter numberFormatter;
        private JsonbDateFormatter dateTimeFormatter;
        private PropertyVisibilityStrategy propertyVisibilityStrategy;
        private TypeInheritanceConfiguration typeInheritanceConfiguration;

        private Builder() {
        }

        @Override
        public Builder of(ClassCustomization customization) {
            super.of(customization);
            creator(customization.creator);
            propertyOrder(customization.propertyOrder);
            numberFormatter(customization.numberFormatter);
            dateTimeFormatter(customization.dateTimeFormatter);
            propertyVisibilityStrategy(customization.propertyVisibilityStrategy);
            return this;
        }

        public Builder creator(JsonbCreator creator) {
            this.creator = creator;
            return this;
        }

        public Builder propertyOrder(String[] propertyOrder) {
            this.propertyOrder = propertyOrder;
            return this;
        }

        public Builder numberFormatter(JsonbNumberFormatter numberFormatter) {
            this.numberFormatter = numberFormatter;
            return this;
        }

        public Builder dateTimeFormatter(JsonbDateFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
            return this;
        }

        public Builder propertyVisibilityStrategy(PropertyVisibilityStrategy propertyVisibilityStrategy) {
            this.propertyVisibilityStrategy = propertyVisibilityStrategy;
            return this;
        }

        public Builder polymorphismConfig(TypeInheritanceConfiguration typeInheritanceConfiguration) {
            this.typeInheritanceConfiguration = typeInheritanceConfiguration;
            return this;
        }

        @Override
        public ClassCustomization build() {
            return new ClassCustomization(this);
        }

    }

}
