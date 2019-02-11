/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * @author Roman Grigoriadi
 */
public abstract class AbstractContainerSerializer<T> extends AbstractItem<T> implements JsonbSerializer<T> {

    private JsonbSerializer<?> valueSerializer;

    private Class<?> valueClass;

    private boolean nullable;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder {@link SerializerBuilder} used to build this instance
     */
    protected AbstractContainerSerializer(SerializerBuilder builder) {
        super(builder);
        nullable = builder.getJsonbContext().getConfigProperties().getConfigNullable();
    }

    /**
     * Creates a new instance.
     *
     * @param wrapper Item to serialize.
     * @param runtimeType Runtime type of the item.
     * @param classModel Class model.
     */
    public AbstractContainerSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel) {
        super(wrapper, runtimeType, classModel);
    }

    @Override
    public final void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        writeStart(generator);
        serializeInternal(obj, generator, ctx);
        writeEnd(generator);
    }


    /**
     * Checks if {@code null} value should be serialized or not.
     * 
     * @return {@code true} if {@code null} values should be serialized, {@code false} otherwise.
     */
    protected final boolean isNullable() {
        return nullable;
    }

    protected abstract void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx);

    /**
     * Write start object or start array without a key.
     *
     * @param generator JSON generator.
     */
    protected abstract void writeStart(JsonGenerator generator);

    /**
     * Writes end for object or array.
     *
     * @param generator JSON generator.
     */
    protected void writeEnd(JsonGenerator generator) {
        generator.writeEnd();
    }

    /**
     * Write start object or start array with key.
     *
     * @param key JSON key name.
     * @param generator JSON generator.
     */
    protected abstract void writeStart(String key, JsonGenerator generator);

    @SuppressWarnings("unchecked")
    protected <X> void serializerCaptor(JsonbSerializer<?> serializer, X object, JsonGenerator generator, SerializationContext ctx) {
        ((JsonbSerializer<X>) serializer).serialize(object, generator, ctx);
    }

    /**
     * Return last used serializer if last value class matches.
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
     * @param valueSerializer serializer
     * @param valueClass class of serializer object
     */
    protected void addValueSerializer(JsonbSerializer<?> valueSerializer, Class<?> valueClass) {
        Objects.requireNonNull(valueSerializer);
        Objects.requireNonNull(valueClass);
        this.valueSerializer = valueSerializer;
        this.valueClass = valueClass;
    }

    protected void serializeItem(Object item, JsonGenerator generator, SerializationContext ctx) {
        if (item == null) {
            if (isNullable()) {
                generator.writeNull();
            }
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
                ClassModel classModel = ((Marshaller)ctx).getJsonbContext().getMappingContext().getOrCreateClassModel(itemClass);
                builder.withCustomization(new ContainerCustomization(classModel.getCustomization()));
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

    protected Type getValueType(Type valueType) {
        if (valueType instanceof ParameterizedType) {
            Optional<Type> runtimeTypeOptional = ReflectionUtils.resolveOptionalType(this, ((ParameterizedType) valueType).getActualTypeArguments()[0]);
            return runtimeTypeOptional.orElse(Object.class);
        }
        return Object.class;
    }
}
