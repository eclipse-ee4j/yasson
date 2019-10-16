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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.customization.ClassCustomizationBuilder;
import org.eclipse.yasson.internal.model.customization.ContainerCustomization;

/**
 * Base class for container serializers (list, array, etc.).
 *
 * @param <T> container value type
 */
public abstract class AbstractContainerSerializer<T> extends AbstractItem<T> implements JsonbSerializer<T> {

    private JsonbSerializer<?> valueSerializer;

    private Class<?> valueClass;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link SerializerBuilder} used to build this instance
     */
    protected AbstractContainerSerializer(SerializerBuilder builder) {
        super(builder);
    }

    /**
     * Creates a new instance.
     *
     * @param wrapper     Item to serialize.
     * @param runtimeType Runtime type of the item.
     * @param classModel  Class model.
     */
    public AbstractContainerSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel) {
        super(wrapper, runtimeType, classModel);
    }

    /**
     * Process container before serialization begins.
     * Does nothing by default.
     *
     * @param obj item to be serialized
     */
    protected void beforeSerialize(T obj) {
    }

    /**
     * Write start of an object or an array without a key.
     *
     * @param generator JSON format generator
     */
    protected abstract void writeStart(JsonGenerator generator);

    /**
     * Write start of an object or an array with a key.
     *
     * @param key       JSON key name.
     * @param generator JSON format generator
     */
    protected abstract void writeStart(String key, JsonGenerator generator);

    /**
     * Writes end of an object or an array.
     *
     * @param generator JSON format generator
     */
    protected void writeEnd(JsonGenerator generator) {
        generator.writeEnd();
    }

    /**
     * Serialize content of provided container.
     *
     * @param obj       container to be serialized
     * @param generator JSON format generator
     * @param ctx       JSON serialization context
     */
    protected abstract void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx);

    @Override
    public final void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        beforeSerialize(obj);
        writeStart(generator);
        serializeInternal(obj, generator, ctx);
        writeEnd(generator);
    }

    /**
     * Serializes container object item.
     *
     * @param serializer serializer of the object
     * @param object     object to serialize
     * @param generator  json generator
     * @param ctx        context
     * @param <X>        type of object
     */
    @SuppressWarnings("unchecked")
    protected <X> void serializerCaptor(JsonbSerializer<?> serializer,
                                        X object,
                                        JsonGenerator generator,
                                        SerializationContext ctx) {
        ((JsonbSerializer<X>) serializer).serialize(object, generator, ctx);
    }

    /**
     * Return last used serializer if last value class matches.
     *
     * @param valueClass class of the serialized object
     * @return cached serializer or null
     */
    protected JsonbSerializer<?> getValueSerializer(Class<?> valueClass) {
        if (valueSerializer != null && valueClass == this.valueClass) {
            return valueSerializer;
        }
        return null;
    }

    /**
     * Cache a serializer and serialized object class for next use.
     *
     * @param valueSerializer serializer
     * @param valueClass      class of serializer object
     */
    protected void addValueSerializer(JsonbSerializer<?> valueSerializer, Class<?> valueClass) {
        Objects.requireNonNull(valueSerializer);
        Objects.requireNonNull(valueClass);
        this.valueSerializer = valueSerializer;
        this.valueClass = valueClass;
    }

    /**
     * Serializes container object.
     *
     * @param item      container
     * @param generator json generator
     * @param ctx       context
     */
    protected void serializeItem(Object item, JsonGenerator generator, SerializationContext ctx) {
        if (item == null) {
            generator.writeNull();
            return;
        }
        Class<?> itemClass = item.getClass();
        //Not null when generic type is present or previous item is of same type
        JsonbSerializer<?> serializer = getValueSerializer(itemClass);

        //Raw collections + lost generic information
        if (serializer == null) {
            Type instanceValueType = getValueType(getRuntimeType());
            instanceValueType = instanceValueType.equals(Object.class) ? itemClass : instanceValueType;

            SerializerBuilder builder = new SerializerBuilder(((Marshaller) ctx).getJsonbContext());
            builder.withObjectClass(itemClass);
            builder.withWrapper(this);
            builder.withType(instanceValueType);

            if (!DefaultSerializers.getInstance().isKnownType(itemClass)) {
                //Need for class level annotations + user adapters/serializers bound to type
                ClassModel classModel = ((Marshaller) ctx).getJsonbContext().getMappingContext().getOrCreateClassModel(itemClass);
                builder.withCustomization(new ContainerCustomization(classModel.getClassCustomization()));
            } else {
                //Still need to override isNillable to true with ContainerCustomization for all serializers
                //to preserve collections and array null elements
                builder.withCustomization(new ContainerCustomization(new ClassCustomizationBuilder()));
            }
            serializer = builder.build();

            //Cache last used value serializer in case of next item is the same type.
            addValueSerializer(serializer, itemClass);
        }
        serializerCaptor(serializer, item, generator, ctx);
    }

    /**
     * Value type of the container.
     *
     * @param valueType value type
     * @return raw value type
     */
    protected Type getValueType(Type valueType) {
        if (valueType instanceof ParameterizedType) {
            Optional<Type> runtimeTypeOptional = ReflectionUtils
                    .resolveOptionalType(this, ((ParameterizedType) valueType).getActualTypeArguments()[0]);
            return runtimeTypeOptional.orElse(Object.class);
        }
        return Object.class;
    }
}
