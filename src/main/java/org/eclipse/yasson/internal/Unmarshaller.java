/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.yasson.internal;


import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.serializer.CurrentItem;
import org.eclipse.yasson.internal.serializer.DefaultSerializers;
import org.eclipse.yasson.internal.serializer.DeserializerBuilder;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * JSONB unmarshaller.
 * Uses {@link JsonParser} to navigate through json string.
 *
 * @author Roman Grigoriadi
 */
public class Unmarshaller extends ProcessingContext implements DeserializationContext {

    /**
     * Creates instance of unmarshaller.
     *
     * @param jsonbContext context to use
     */
    public Unmarshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
    }

    private CurrentItem<?> current;

    @Override
    public <T> T deserialize(Class<T> clazz, JsonParser parser) {
        return deserializeItem(clazz, parser);
    }

    @Override
    public <T> T deserialize(Type type, JsonParser parser) {
        return deserializeItem(type, parser);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeItem(Type type, JsonParser parser) {
        DeserializerBuilder deserializerBuilder = new DeserializerBuilder(jsonbContext).withWrapper(current)
                .withType(type).withJsonValueType(getRootEvent(parser));
        Class<?> rawType = ReflectionUtils.getRawType(type);
        if (!DefaultSerializers.getInstance().isKnownType(rawType)) {
            ClassModel classModel = getMappingContext().getOrCreateClassModel(rawType);
            deserializerBuilder.withCustomization(classModel.getCustomization());
        }

        return (T) deserializerBuilder.build().deserialize(parser, this, type);
    }

    /**
     * Get root value event, either for new deserialization process, or deserialization sub-process invoked from
     * custom user deserializer.
     */
    private JsonParser.Event getRootEvent(JsonParser parser) {
        if (parser.getLocation().getStreamOffset() == 0) {
            return parser.next();
        }
        final JsonParser.Event lastEvent = ((JsonbParser) parser).getCurrentLevel().getLastEvent();
        return lastEvent == JsonParser.Event.KEY_NAME ? parser.next() : lastEvent;
    }

    /**
     * Get currently processed json item.
     *
     * @return current item
     */
    public CurrentItem<?> getCurrent() {
        return current;
    }

    /**
     * Set currently processed item.
     *
     * @param current current item
     */
    public void setCurrent(CurrentItem<?> current) {
        this.current = current;
    }

}
