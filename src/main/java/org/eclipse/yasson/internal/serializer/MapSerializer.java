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
import java.util.Map;
import java.util.Optional;

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.ReflectionUtils;

/**
 * Serializer for maps.
 * 
 * @author Roman Grigoriadi
 */
public class MapSerializer<T extends Map<?,?>> extends AbstractContainerSerializer<T> implements EmbeddedItem {


    protected MapSerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void serializeInternal(T obj, JsonGenerator generator, SerializationContext ctx) {
        for (Map.Entry<?,?> entry : obj.entrySet()) {
            final Object key = entry.getKey();
            if (key == null) {
                if (!isNullable()) {
                    continue;
                }
            }
            final String keysString = String.valueOf(key);
            final Object value = entry.getValue();
            if (value == null) {
                if (isNullable()) {
                    generator.writeNull(keysString);
                }
                continue;
            }
            generator.writeKey(keysString);
            serializeItem(value, generator, ctx);
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

    @Override
    protected Type getValueType(Type valueType) {
        if (valueType instanceof ParameterizedType) {
            Optional<Type> runtimeTypeOptional = ReflectionUtils.resolveOptionalType(this, ((ParameterizedType) valueType).getActualTypeArguments()[1]);
            return runtimeTypeOptional.orElse(Object.class);
        }
        return Object.class;
    }
}
