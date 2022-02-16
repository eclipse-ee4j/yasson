/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.json.bind.annotation.JsonbTypeInfo;

/**
 * Type inheritance configuration.
 */
public class TypeInheritanceConfiguration {

    private final String fieldName;
    private final boolean inherited;
    private final Map<Class<?>, String> aliases;
    private final Class<?> definedType;
    private final TypeInheritanceConfiguration parentConfig;

    private TypeInheritanceConfiguration(Builder builder) {
        this.fieldName = builder.fieldName;
        this.inherited = builder.inherited;
        this.aliases = Map.copyOf(builder.aliases);
        this.parentConfig = builder.parentConfig;
        this.definedType = builder.definedType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isInherited() {
        return inherited;
    }

    public Map<Class<?>, String> getAliases() {
        return aliases;
    }

    public Class<?> getDefinedType() {
        return definedType;
    }

    public TypeInheritanceConfiguration getParentConfig() {
        return parentConfig;
    }

    public static final class Builder {

        private Map<Class<?>, String> aliases = new HashMap<>();
        private String fieldName = JsonbTypeInfo.DEFAULT_KEY_NAME;
        private boolean inherited = false;
        private Class<?> definedType;
        private TypeInheritanceConfiguration parentConfig;

        private Builder() {
        }

        public Builder inherited(boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public Builder fieldName(String fieldName) {
            this.fieldName = Objects.requireNonNull(fieldName);
            return this;
        }

        public Builder alias(Class<?> clazz, String alias) {
            this.aliases.put(clazz, alias);
            return this;
        }

        public Builder parentConfig(TypeInheritanceConfiguration parentConfig) {
            this.parentConfig = parentConfig;
            return this;
        }

        public Builder definedType(Class<?> definedType) {
            this.definedType = definedType;
            return this;
        }

        public Builder of(TypeInheritanceConfiguration typeInheritanceConfiguration) {
            this.fieldName = typeInheritanceConfiguration.fieldName;
            this.aliases = new HashMap<>(typeInheritanceConfiguration.aliases);
            this.inherited = typeInheritanceConfiguration.inherited;
            this.parentConfig = typeInheritanceConfiguration.parentConfig;
            this.definedType = typeInheritanceConfiguration.definedType;
            return this;
        }

        public TypeInheritanceConfiguration build() {
            return new TypeInheritanceConfiguration(this);
        }
    }

}
