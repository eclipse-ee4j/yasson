/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ProcessingContext;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonbPropertyInfo;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Serializer for adapted object.
 * Converts object using components first, than serializes result with standard process.
 *
 * @param <T> source type
 * @param <A> adapted type
 */
public class AdaptedObjectSerializer<T, A> implements CurrentItem<T>, JsonbSerializer<T> {

    private final ClassModel classModel;

    private final AdapterBinding adapterInfo;

    /**
     * Creates AdapterObjectSerializer.
     *
     * @param classModel Class model.
     * @param adapter    Adapter.
     */
    public AdaptedObjectSerializer(ClassModel classModel, AdapterBinding adapter) {
        this.classModel = classModel;
        this.adapterInfo = adapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        ProcessingContext context = (ProcessingContext) ctx;
        try {
            if (context.addProcessedObject(obj)) {
                final JsonbAdapter<T, A> adapter = (JsonbAdapter<T, A>) adapterInfo.getAdapter();
                A adapted = adapter.adaptToJson(obj);
                if (adapted == null) {
                    generator.writeNull();
                    return;
                }
                final JsonbSerializer<A> serializer = resolveSerializer((Marshaller) ctx, adapted);
                serializer.serialize(adapted, generator, ctx);
            } else {
                throw new JsonbException(Messages.getMessage(MessageKeys.RECURSIVE_REFERENCE, obj.getClass()));
            }
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION,
                                                         adapterInfo.getBindingType(),
                                                         adapterInfo.getToType(),
                                                         adapterInfo.getAdapter().getClass()), e);
        } finally {
            context.removeProcessedObject(obj);
        }
    }

    @SuppressWarnings("unchecked")
    private JsonbSerializer<A> resolveSerializer(Marshaller ctx, A adapted) {
        final ContainerSerializerProvider cached = ctx.getMappingContext().getSerializerProvider(adapted.getClass());
        if (cached != null) {
            return (JsonbSerializer<A>) cached.provideSerializer(new JsonbPropertyInfo()
                                                                         .withWrapper(this)
                                                                         .withRuntimeType(classModel == null
                                                                                                  ? null
                                                                                                  : classModel.getType()));
        }
        return (JsonbSerializer<A>) new SerializerBuilder(ctx.getJsonbContext())
                .withObjectClass(adapted.getClass())
                .withCustomization(classModel == null ? null : classModel.getClassCustomization())
                .withWrapper(this)
                .build();
    }

    @Override
    public ClassModel getClassModel() {
        return null;
    }

    @Override
    public CurrentItem<?> getWrapper() {
        return null;
    }

    @Override
    public Type getRuntimeType() {
        return null;
    }
}
