/*
 * Copyright (c) 2016, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.io.FilterWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import jakarta.json.JsonStructure;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.YassonJsonb;
import org.eclipse.yasson.internal.jsonstructure.JsonGeneratorToStructureAdapter;
import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;

/**
 * Implementation of Jsonb interface.
 */
public class JsonBinding implements YassonJsonb {

    private final JsonbContext jsonbContext;

    JsonBinding(JsonBindingBuilder builder) {
        this.jsonbContext = new JsonbContext(builder.getConfig(), builder.getProvider().orElseGet(JsonProvider::provider));
        Set<Class<?>> eagerInitClasses = this.jsonbContext.getConfigProperties().getEagerInitClasses();
        for (Class<?> eagerInitClass : eagerInitClasses) {
            // Eagerly initialize requested ClassModels and Serializers
            jsonbContext.getChainModelCreator().deserializerChain(eagerInitClass);
            jsonbContext.getSerializationModelCreator().serializerChain(eagerInitClass, true, true);
        }
    }

    private <T> T deserialize(final Type type, final JsonParser parser, final DeserializationContextImpl unmarshaller) {
        return unmarshaller.deserialize(type, parser);
    }

    @Override
    public <T> T fromJson(String str, Class<T> type) throws JsonbException {
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(new StringReader(str))) {
            final DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(String str, Type type) throws JsonbException {
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(new StringReader(str))) {
            DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonbException {
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(reader)) {
            DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(Reader reader, Type type) throws JsonbException {
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(reader)) {
            DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> clazz) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        try (JsonParser parser = inputStreamParser(stream)) {
            return deserialize(clazz, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Type type) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        try (JsonParser parser = inputStreamParser(stream)) {
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJsonStructure(JsonStructure jsonStructure, Class<T> type) throws JsonbException {
        try (JsonParser parser = new JsonStructureToParserAdapter(jsonStructure)) {
            return deserialize(type, parser, new DeserializationContextImpl(jsonbContext));
        }
    }

    @Override
    public <T> T fromJsonStructure(JsonStructure jsonStructure, Type runtimeType) throws JsonbException {
        try (JsonParser parser = new JsonStructureToParserAdapter(jsonStructure)) {
            return deserialize(runtimeType, parser, new DeserializationContextImpl(jsonbContext));
        }
    }

    private JsonParser inputStreamParser(InputStream stream) {
        return jsonbContext.getJsonParserFactory()
                .createParser(stream,
                              Charset.forName((String) jsonbContext.getConfig()
                                      .getProperty(JsonbConfig.ENCODING).orElse("UTF-8")));
    }

    @Override
    public String toJson(Object object) throws JsonbException {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = writerGenerator(writer)) {
            new SerializationContextImpl(jsonbContext).marshall(object, generator);
        }
        return writer.toString();
    }

    @Override
    public String toJson(Object object, Type type) throws JsonbException {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = writerGenerator(writer)) {
            new SerializationContextImpl(jsonbContext, type).marshall(object, generator);
        }
        return writer.toString();
    }

    @Override
    public void toJson(Object object, Writer writer) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext);
        try (JsonGenerator generator = writerGenerator(new CloseSuppressingWriter(writer))) {
            marshaller.marshallWithoutClose(object, generator);
        }
    }

    @Override
    public void toJson(Object object, Type type, Writer writer) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext, type);
        try (JsonGenerator generator = writerGenerator(new CloseSuppressingWriter(writer))) {
            marshaller.marshallWithoutClose(object, generator);
        }
    }

    private JsonGenerator writerGenerator(Writer writer) {
        Map<String, ?> factoryProperties = jsonbContext.createJsonpProperties(jsonbContext.getConfig());
        if (factoryProperties.isEmpty()) {
            return jsonbContext.getJsonProvider().createGenerator(writer);
        }
        return jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(writer);
    }

    @Override
    public void toJson(Object object, OutputStream stream) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext);
        try (JsonGenerator generator = streamGenerator(stream)) {
            marshaller.marshall(object, generator);
        }
    }

    @Override
    public void toJson(Object object, Type type, OutputStream stream) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext, type);
        try (JsonGenerator generator = streamGenerator(stream)) {
            marshaller.marshall(object, generator);
        }
    }

    @Override
    public <T> T fromJson(JsonParser jsonParser, Class<T> type) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        return unmarshaller.deserialize(type, jsonParser);
    }

    @Override
    public <T> T fromJson(JsonParser jsonParser, Type runtimeType) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        return unmarshaller.deserialize(runtimeType, jsonParser);
    }

    @Override
    public void toJson(Object object, JsonGenerator jsonGenerator) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext);
        marshaller.marshallWithoutClose(object, jsonGenerator);
    }

    @Override
    public void toJson(Object object, Type runtimeType, JsonGenerator jsonGenerator) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext, runtimeType);
        marshaller.marshallWithoutClose(object, jsonGenerator);
    }

    @Override
    public JsonStructure toJsonStructure(Object object) throws JsonbException {
        JsonGeneratorToStructureAdapter structureGenerator = new JsonGeneratorToStructureAdapter(jsonbContext.getJsonProvider());
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext);
        marshaller.marshall(object, structureGenerator);
        return structureGenerator.getRootStructure();
    }

    @Override
    public JsonStructure toJsonStructure(Object object, Type runtimeType) throws JsonbException {
        JsonGeneratorToStructureAdapter structureGenerator = new JsonGeneratorToStructureAdapter(jsonbContext.getJsonProvider());
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext, runtimeType);
        marshaller.marshall(object, structureGenerator);
        return structureGenerator.getRootStructure();
    }

    private JsonGenerator streamGenerator(OutputStream stream) {
        Map<String, ?> factoryProperties = jsonbContext.createJsonpProperties(jsonbContext.getConfig());
        final String encoding = (String) jsonbContext.getConfig().getProperty(JsonbConfig.ENCODING).orElse("UTF-8");
        return jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties)
                .createGenerator(stream, Charset.forName(encoding));
    }

    @Override
    public void close() throws Exception {
        jsonbContext.getComponentInstanceCreator().close();
    }

    private static class CloseSuppressingWriter extends FilterWriter {

        protected CloseSuppressingWriter(final Writer in) {
            super(in);
        }

        @Override
        public void close() {
            // do not close
        }

    }

}
