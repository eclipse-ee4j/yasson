/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Patrik Dudits
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.ProcessingContext;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Deserialize optional object.
 *
 * @author Roman Grigoriadi
 */
public class OptionalObjectDeserializer implements JsonbDeserializer<Optional<?>> {

    private final CurrentItem<?> wrapper;

    private final Type optionalValueType;

    public OptionalObjectDeserializer(DeserializerBuilder deserializerBuilder) {
        this.wrapper = deserializerBuilder.getWrapper();
        this.optionalValueType = resolveOptionalType(deserializerBuilder.getRuntimeType());
    }

    @Override
    public Optional<?> deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonbContext jsonbContext = ((ProcessingContext) ctx).getJsonbContext();
        final JsonParser.Event lastEvent = ((JsonbParser) parser).getCurrentLevel().getLastEvent();
        if (lastEvent == JsonParser.Event.VALUE_NULL) {
            return Optional.empty();
        }
        JsonbDeserializer deserializer = new DeserializerBuilder(jsonbContext).withType(optionalValueType)
                .withWrapper(wrapper).withJsonValueType(lastEvent).build();
        return Optional.of(deserializer.deserialize(parser, ctx, optionalValueType));
    }


    private Type resolveOptionalType(Type runtimeType) {
        if (runtimeType instanceof ParameterizedType) {
            return ((ParameterizedType) runtimeType).getActualTypeArguments()[0];
        }
        return Object.class;
    }
}
