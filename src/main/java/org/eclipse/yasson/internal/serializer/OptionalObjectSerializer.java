/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ProcessingContext;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Common serializer logic for java Optionals.
 *
 * @param <T> instantiated Optional type
 */
public class OptionalObjectSerializer<T extends Optional<?>> implements CurrentItem<T>, JsonbSerializer<T> {
    private final Customization customization;

    private final CurrentItem<?> wrapper;

    private final Type optionalValueType;

    /**
     * Creates a new instance.
     *
     * @param builder Builder to initialize the instance.
     */
    public OptionalObjectSerializer(SerializerBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.customization = builder.getCustomization();
        this.optionalValueType = resolveOptionalType(builder.getRuntimeType());
    }

    private Type resolveOptionalType(Type runtimeType) {
        if (runtimeType instanceof ParameterizedType) {
            return ((ParameterizedType) runtimeType).getActualTypeArguments()[0];
        }
        return Object.class;
    }

    @Override
    public ClassModel getClassModel() {
        return null;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    @Override
    public Type getRuntimeType() {
        return optionalValueType;
    }

    public Customization getCustomization() {
        return customization;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        JsonbContext jsonbContext = ((ProcessingContext) ctx).getJsonbContext();
        if (handleEmpty(obj, Optional::isPresent, customization, generator, (Marshaller) ctx)) {
            return;
        }
        Object optionalValue = obj.get();
        final JsonbSerializer<?> serializer = new SerializerBuilder(jsonbContext).withObjectClass(optionalValue.getClass())
                .withType(optionalValueType).withWrapper(wrapper).withCustomization(customization).build();
        serialCaptor(serializer, optionalValue, generator, ctx);
    }

    static <T> boolean handleEmpty(T value,
                                   Predicate<T> presentCheck,
                                   Customization customization,
                                   JsonGenerator generator,
                                   Marshaller marshaller) {
        if (value == null || !presentCheck.test(value)) {
            if (customization != null) {
                if (customization.isNillable()) {
                    generator.writeNull();
                    return true;
                }
            } else {
                marshaller.getJsonbContext().getConfigProperties().getNullSerializer().serialize(value, generator, marshaller);
            }
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void serialCaptor(JsonbSerializer<?> serializer,
                                  T object,
                                  JsonGenerator generator,
                                  SerializationContext context) {
        ((JsonbSerializer<T>) serializer).serialize(object, generator, context);
    }
}
