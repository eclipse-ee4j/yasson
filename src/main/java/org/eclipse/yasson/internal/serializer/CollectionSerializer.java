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

import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Serializer for collections.
 *
 * @author Roman Grigoriadi
 */
public class CollectionSerializer<T extends Collection> extends AbstractContainerSerializer<T> implements EmbeddedItem {

    private final JsonBindingModel containerModel;

    protected CollectionSerializer(SerializerBuilder builder) {
        super(builder);
        Type collectionValueType = getValueType();
        containerModel = new ContainerModel(collectionValueType, resolveContainerModelCustomization(collectionValueType, builder.getJsonbContext()));
    }

    private Type getValueType() {
        return getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;
    }

    @Override
    protected void serializeInternal(T collection, JsonGenerator generator, SerializationContext ctx) {
        for (Object item : collection) {
            serializeItem(item, generator, ctx, containerModel);
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartArray();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartArray(key);
    }
}
