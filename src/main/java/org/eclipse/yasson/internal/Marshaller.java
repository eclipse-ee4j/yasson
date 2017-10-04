/*******************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.internal;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.AbstractValueTypeSerializer;
import org.eclipse.yasson.internal.serializer.ContainerSerializerProvider;
import org.eclipse.yasson.internal.serializer.SerializerBuilder;
import org.eclipse.yasson.internal.serializer.ContainerModel;
import org.eclipse.yasson.internal.model.JsonBindingModel;
import org.eclipse.yasson.internal.model.JsonbPropertyInfo;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * JSONB marshaller. Created each time marshalling operation called.
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class Marshaller extends ProcessingContext implements SerializationContext {

    private static final Logger logger = Logger.getLogger(Marshaller.class.getName());

    private Type runtimeType;

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext Current context.
     * @param rootRuntimeType Type of root object.
     */
    public Marshaller(JsonbContext jsonbContext, Type rootRuntimeType) {
        super(jsonbContext);
        this.runtimeType = rootRuntimeType;
    }

    /**
     * Creates Marshaller for generation to String.
     *
     * @param jsonbContext Current context.
     */
    public Marshaller(JsonbContext jsonbContext) {
        super(jsonbContext);
        this.runtimeType = null;
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        try {
            final ContainerModel model = new ContainerModel(runtimeType != null ? runtimeType : object.getClass(), null);
            serializeRoot(object, jsonGenerator, model);
        } catch (JsonbException e) {
            logger.severe(e.getMessage());
            throw e;
        } finally {
            try {
                jsonGenerator.close();
            } catch (JsonGenerationException jge) {
                logger.severe(jge.getMessage());
            }
        }
    }

    @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        final ContainerModel model = new ContainerModel(object.getClass(), null);
        generator.writeKey(key);
        serializeRoot(object, generator, model);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        final ContainerModel model = new ContainerModel(object.getClass(), null);
        serializeRoot(object, generator, model);
    }

    /**
     * Serializes root element.
     *
     * @param <T> Root type
     * @param root Root.
     * @param generator JSON generator.
     * @param model Binding model.
     */
    @SuppressWarnings("unchecked")
    public <T> void serializeRoot(T root, JsonGenerator generator, JsonBindingModel model) {
        final JsonbSerializer<T> rootSerializer = (JsonbSerializer<T>) getRootSerializer(root.getClass(), model);
        if (jsonbContext.getConfigProperties().isStrictIJson() &&
                rootSerializer instanceof AbstractValueTypeSerializer) {
            throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
        }
        rootSerializer.serialize(root, generator, this);
    }

    private JsonbSerializer<?> getRootSerializer(Class<?> rootClazz, JsonBindingModel model) {
        final ContainerSerializerProvider serializerProvider = getMappingContext().getSerializerProvider(rootClazz);
        if (serializerProvider != null) {
            return serializerProvider
                    .provideSerializer(new JsonbPropertyInfo()
                            .withRuntimeType(runtimeType)
                            .withClassModel(getMappingContext().
                                    getClassModel(rootClazz))
                            .withJsonBindingModel(model));
        }
        return new SerializerBuilder(jsonbContext)
                .withObjectClass(rootClazz)
                .withType(model.getType())
                .withModel(model).build();
    }

}
