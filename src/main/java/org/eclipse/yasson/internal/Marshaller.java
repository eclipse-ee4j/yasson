/*******************************************************************************
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.AbstractValueTypeSerializer;
import org.eclipse.yasson.internal.serializer.ContainerSerializerProvider;
import org.eclipse.yasson.internal.serializer.DefaultSerializers;
import org.eclipse.yasson.internal.serializer.SerializerBuilder;
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

    private final Type runtimeType;

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
     * @param close if generator should be closed
     */
    public void marshall(Object object, JsonGenerator jsonGenerator, boolean close) {
        try {
            serializeRoot(object, jsonGenerator);
        } catch (JsonbException e) {
            logger.severe(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, e.getMessage()), e);
        } finally {
            try {
                if (close) {
                    jsonGenerator.close();
                }
            } catch (JsonGenerationException jge) {
                logger.severe(jge.getMessage());
            }
        }
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     * Closes the generator on completion.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshall(Object object, JsonGenerator jsonGenerator) {
        marshall(object,jsonGenerator,true);
    }

    /**
     * Marshals given object to provided Writer or OutputStream.
     * Leaves generator open for further interaction after completion.
     *
     * @param object object to marshall
     * @param jsonGenerator generator to use
     */
    public void marshallWithoutClose(Object object, JsonGenerator jsonGenerator) {
        marshall(object,jsonGenerator,false);
    }

    @Override
    public <T> void serialize(String key, T object, JsonGenerator generator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(object);
        generator.writeKey(key);
        serializeRoot(object, generator);
    }

    @Override
    public <T> void serialize(T object, JsonGenerator generator) {
        Objects.requireNonNull(object);
        serializeRoot(object, generator);
    }

    /**
     * Serializes root element.
     *
     * @param <T> Root type
     * @param root Root.
     * @param generator JSON generator.
     */
    @SuppressWarnings("unchecked")
    public <T> void serializeRoot(T root, JsonGenerator generator) {
        if (root == null) {
            getJsonbContext().getConfigProperties().getNullSerializer().serialize(null, generator, this);
            return;
        }
        final JsonbSerializer<T> rootSerializer = (JsonbSerializer<T>) getRootSerializer(root.getClass());
        if (jsonbContext.getConfigProperties().isStrictIJson() &&
                rootSerializer instanceof AbstractValueTypeSerializer) {
            throw new JsonbException(Messages.getMessage(MessageKeys.IJSON_ENABLED_SINGLE_VALUE));
        }
        rootSerializer.serialize(root, generator, this);
    }

    private JsonbSerializer<?> getRootSerializer(Class<?> rootClazz) {
        final ContainerSerializerProvider serializerProvider = getMappingContext().getSerializerProvider(rootClazz);
        if (serializerProvider != null) {
            return serializerProvider
                    .provideSerializer(new JsonbPropertyInfo()
                            .withRuntimeType(runtimeType));
        }
        SerializerBuilder serializerBuilder = new SerializerBuilder(jsonbContext)
                .withObjectClass(rootClazz)
                .withType(runtimeType);

        if (!DefaultSerializers.getInstance().isKnownType(rootClazz)) {
            ClassModel classModel = getMappingContext().getOrCreateClassModel(rootClazz);
            serializerBuilder.withCustomization(classModel.getCustomization());
        }
        return serializerBuilder.build();
    }

}
