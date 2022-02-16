/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;

/**
 * Common properties of {@link ClassCustomization} and {@link PropertyCustomization}.
 */
abstract class CustomizationBase implements Customization, ComponentBoundCustomization {

    private final AdapterBinding adapterBinding;
    private final SerializerBinding<?> serializerBinding;
    private final DeserializerBinding<?> deserializerBinding;
    private final boolean nillable;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    CustomizationBase(Builder<?, ?> builder) {
        this.nillable = builder.nillable;
        this.adapterBinding = builder.adapterBinding;
        this.serializerBinding = builder.serializerBinding;
        this.deserializerBinding = builder.deserializerBinding;
    }

    /**
     * Returns true if <i>nillable</i> customization is present.
     *
     * @return True if <i>nillable</i> customization is present.
     */
    public boolean isNillable() {
        return nillable;
    }

    public AdapterBinding getSerializeAdapterBinding() {
        return adapterBinding;
    }

    @Override
    public AdapterBinding getDeserializeAdapterBinding() {
        return adapterBinding;
    }

    /**
     * Serializer wrapper with resolved generic info.
     *
     * @return serializer wrapper
     */
    public SerializerBinding<?> getSerializerBinding() {
        return serializerBinding;
    }

    /**
     * Deserializer wrapper with resolved generic info.
     *
     * @return deserializer wrapper
     */
    public DeserializerBinding<?> getDeserializerBinding() {
        return deserializerBinding;
    }

    @SuppressWarnings("unchecked")
    abstract static class Builder<T extends Builder<T, B>, B extends CustomizationBase> {

        private AdapterBinding adapterBinding;
        private SerializerBinding<?> serializerBinding;
        private DeserializerBinding<?> deserializerBinding;
        private boolean nillable;

        Builder() {
        }

        public T of(B customization) {
            adapterBinding = customization.getDeserializeAdapterBinding();
            serializerBinding = customization.getSerializerBinding();
            deserializerBinding = customization.getDeserializerBinding();
            nillable = customization.isNillable();
            return (T) this;
        }

        public T adapterBinding(AdapterBinding adapterBinding) {
            this.adapterBinding = adapterBinding;
            return (T) this;
        }

        public T serializerBinding(SerializerBinding<?> serializerBinding) {
            this.serializerBinding = serializerBinding;
            return (T) this;
        }

        public T deserializerBinding(DeserializerBinding<?> deserializerBinding) {
            this.deserializerBinding = deserializerBinding;
            return (T) this;
        }

        public T nillable(boolean nillable) {
            this.nillable = nillable;
            return (T) this;
        }

        public abstract B build();

    }

}
