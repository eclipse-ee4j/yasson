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
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Serializer for maps.
 * 
 * @author Roman Grigoriadi
 */
public class MapSerializer<T extends Map<?,?>> extends AbstractContainerSerializer<T> implements EmbeddedItem {

    private final JsonBindingModel containerModel;

    protected MapSerializer(SerializerBuilder builder) {
        super(builder);
        Type mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;

        containerModel = new ContainerModel(mapValueRuntimeType,
                resolveContainerModelCustomization(mapValueRuntimeType, builder.getJsonbContext()));
    }

    @Override
    protected void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx) {
        for (Map.Entry<?,?> entry : obj.entrySet()) {
            final String keysString = String.valueOf(entry.getKey());
            final Object value = entry.getValue();
            if (value == null) {
                generator.writeNull(keysString);
                return;
            }
            generator.writeKey(keysString);
            serializeItem(value, generator, ctx, containerModel);
        }
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartObject();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartObject(key);
    }
}
