/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
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
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(new CloseSuppressingReader(reader))) {
            DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(Reader reader, Type type) throws JsonbException {
        try (JsonParser parser = jsonbContext.getJsonProvider().createParser(new CloseSuppressingReader(reader))) {
            DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
            return deserialize(type, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Class<T> clazz) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        try (JsonParser parser = inputStreamParser(new CloseSuppressingInputStream(stream))) {
            return deserialize(clazz, parser, unmarshaller);
        }
    }

    @Override
    public <T> T fromJson(InputStream stream, Type type) throws JsonbException {
        DeserializationContextImpl unmarshaller = new DeserializationContextImpl(jsonbContext);
        try (JsonParser parser = inputStreamParser(new CloseSuppressingInputStream(stream))) {
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
        try (JsonGenerator generator = streamGenerator(new CloseSuppressingOutputStream(stream))) {
            marshaller.marshall(object, generator);
        }
    }

    @Override
    public void toJson(Object object, Type type, OutputStream stream) throws JsonbException {
        final SerializationContextImpl marshaller = new SerializationContextImpl(jsonbContext, type);
        try (JsonGenerator generator = streamGenerator(new CloseSuppressingOutputStream(stream))) {
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

    /**
     * {@link OutputStream} that suppresses {@link OutputStream#close()}.
     */
    static final class CloseSuppressingOutputStream extends OutputStream {

        private final OutputStream delegate;

        CloseSuppressingOutputStream(OutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() {
            // suppress
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            delegate.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            delegate.write(b, off, len);
        }

    }

    /**
     * {@link InputStream} that suppresses {@link InputStream#close()}.
     */
    static final class CloseSuppressingInputStream extends InputStream {

        private final InputStream delegate;

        CloseSuppressingInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() {
            // suppress
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void mark(int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public long transferTo(OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }

    }

    /**
     * {@link Reader} that suppresses {@link Reader#close()}.
     */
    static final class CloseSuppressingReader extends Reader {

        private final Reader delegate;

        CloseSuppressingReader(Reader delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read(CharBuffer target) throws IOException {
            return delegate.read(target);
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(char[] cbuf) throws IOException {
            return delegate.read(cbuf);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return delegate.read(cbuf, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public boolean ready() throws IOException {
            return delegate.ready();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            delegate.mark(readAheadLimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public void close() {
            // suppress
        }

        @Override
        public long transferTo(Writer out) throws IOException {
            return delegate.transferTo(out);
        }

    }

    /**
     * {@link Writer} that suppresses {@link Writer#close()}.
     */
    static final class CloseSuppressingWriter extends Writer {

        private final Writer delegate;

        CloseSuppressingWriter(Writer delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(int c) throws IOException {
            delegate.write(c);
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            delegate.write(cbuf);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            delegate.write(cbuf, off, len);
        }

        @Override
        public void write(String str) throws IOException {
            delegate.write(str);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            delegate.write(str, off, len);
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            return delegate.append(csq);
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) throws IOException {
            return delegate.append(csq, start, end);
        }

        @Override
        public Writer append(char c) throws IOException {
            return delegate.append(c);
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        @Override
        public void close() {
            // suppress
        }

    }

}
