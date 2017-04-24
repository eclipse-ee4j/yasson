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

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.adapter.AdapterBinding;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.unmarshaller.CurrentItem;
import org.eclipse.yasson.model.ClassModel;
import org.eclipse.yasson.model.customization.Customization;
import org.eclipse.yasson.model.JsonBindingModel;
import org.eclipse.yasson.model.JsonContext;
import org.eclipse.yasson.model.JsonbPropertyInfo;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;

/**
 * Serializer for adapted object.
 * Converts object using adapter first, than serializes result with standard process.
 *
 * @author Roman Grigoriadi
 */
public class AdaptedObjectSerializer<T, A> implements CurrentItem<T>, JsonbSerializer<T> {

    private final static class AdaptedObjectSerializerModel implements JsonBindingModel {

        private final JsonBindingModel wrapperSerializerModel;

        private final Type adaptedType;

        public AdaptedObjectSerializerModel(JsonBindingModel wrapperSerializerModel, Type adaptedType) {
            this.wrapperSerializerModel = wrapperSerializerModel;
            this.adaptedType = adaptedType;
        }

        @Override
        public String getWriteName() {
            return wrapperSerializerModel.getWriteName();
        }

        /**
         * Array context if root.
         */
        @Override
        public JsonContext getContext() {
            return wrapperSerializerModel != null ?
                    wrapperSerializerModel.getContext() : JsonContext.JSON_ARRAY;
        }

        /**
         * Get wrapper customization or empty if wrapper not present (root).
         */
        @Override
        public Customization getCustomization() {
            return wrapperSerializerModel != null ?
            wrapperSerializerModel.getCustomization() : null;
        }

        @Override
        public Type getType() {
            return adaptedType;
        }
    }

    private final JsonBindingModel model;

    private final AdapterBinding adapterInfo;

    /**
     * Creates AdapterObjectSerializer.
     *
     * @param model Binding model.
     * @param adapter Adapter.
     */
    public AdaptedObjectSerializer(JsonBindingModel model, AdapterBinding adapter) {
        this.model = new AdaptedObjectSerializerModel(model, adapter.getToType());
        this.adapterInfo = adapter;
    }

    @Override
    public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        try {
            final JsonbAdapter<T, A> adapter = (JsonbAdapter<T, A>) adapterInfo.getAdapter();
            A adapted = adapter.adaptToJson(obj);
            final JsonbSerializer<A> serializer = resolveSerializer((Marshaller) ctx, adapted);
            serializer.serialize(adapted, generator, ctx);
        } catch (Exception e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, adapterInfo.getBindingType(), adapterInfo.getToType(), adapterInfo.getAdapter().getClass()), e);
        }
    }

    @SuppressWarnings("unchekced")
    private JsonbSerializer<A> resolveSerializer(Marshaller ctx, A adapted) {
        final ContainerSerializerProvider cached = ctx.getMappingContext().getSerializerProvider(adapted.getClass());
        if (cached != null) {
            return (JsonbSerializer<A>) cached.provideSerializer(new JsonbPropertyInfo().withWrapper(this).withRuntimeType(adapted.getClass()).withJsonBindingModel(model));
        }
        return (JsonbSerializer<A>) new SerializerBuilder(ctx.getJsonbContext()).withObjectClass(adapted.getClass()).withModel(model).withWrapper(this).build();
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
    public JsonBindingModel getWrapperModel() {
        return model;
    }

    @Override
    public Type getRuntimeType() {
        return null;
    }
}
