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

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;

/**
 * Base class for container serializers (list, array, etc.).
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractContainerSerializer<T> extends AbstractItem<T> implements JsonbSerializer<T> {

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
     * @param wrapper Item to serialize.
     * @param runtimeType Runtime type of the item.
     * @param classModel Class model.
     * @param wrapperModel Binding model.
     */
    public AbstractContainerSerializer(CurrentItem<?> wrapper, Type runtimeType, ClassModel classModel, JsonBindingModel wrapperModel) {
        super(wrapper, runtimeType, classModel, wrapperModel);
    }

    @Override
    public final void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
        writeStart(generator);
        serializeInternal(obj, generator, ctx);
        writeEnd(generator);
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

    protected <X> void serializerCaptor(JsonbSerializer<?> serializer, X object, JsonGenerator generator, SerializationContext ctx) {
        ((JsonbSerializer<X>) serializer).serialize(object, generator, ctx);
    }
}
