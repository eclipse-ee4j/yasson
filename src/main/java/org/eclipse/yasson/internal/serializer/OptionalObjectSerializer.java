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

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.ProcessingContext;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Common serializer logic for java Optionals.
 *
 * @author Roman Grigoriadi
 * @param <T> instantiated Optional type
 */
public class OptionalObjectSerializer<T extends Optional<?>> implements CurrentItem<T>, JsonbSerializer<T> {
    private final JsonBindingModel wrapperModel;

    private final CurrentItem<?> wrapper;

    private final Type optionalValueType;

    /**
     * Creates a new instance.
     *
     * @param builder Builder to initialize the instance.
     */
    public OptionalObjectSerializer(SerializerBuilder builder) {
        this.wrapper = builder.getWrapper();
        this.wrapperModel = builder.getModel();
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

    @Override
    public JsonBindingModel getWrapperModel() {
        return wrapperModel;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        JsonbContext jsonbContext = ((ProcessingContext) ctx).getJsonbContext();
        if (obj == null || !obj.isPresent()) {
            if (!wrapperModel.getCustomization().isNillable()) {
                return;
            }
            generator.writeNull();
            return;
        }
        Object optionalValue = obj.get();
        final JsonbSerializer<?> serializer = new SerializerBuilder(jsonbContext).withObjectClass(optionalValue.getClass())
                .withType(optionalValueType).withWrapper(wrapper).withModel(wrapperModel).build();
        serialCaptor(serializer, optionalValue, generator, ctx);
    }

    private <T> void serialCaptor(JsonbSerializer<?> serializer, T object, JsonGenerator generator, SerializationContext context) {
        ((JsonbSerializer<T>) serializer).serialize(object, generator, context);
    }
}
