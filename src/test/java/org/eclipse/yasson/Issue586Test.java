/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verify Jsonb leaves the streams open.
 *
 * @author Philippe Marschall
 */
public class Issue586Test {

    @Test
    public void fromJsonInputStreamClass() {
        CloseRememberingInputStream stream = new CloseRememberingInputStream(asInputStream("{\"field\":\"first\"}"));
        assertNotNull(Jsonbs.defaultJsonb.fromJson(stream, KeyValue.class));
        assertFalse(stream.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void fromJsonInputStreamType() {
        CloseRememberingInputStream stream = new CloseRememberingInputStream(asInputStream("{\"field\":\"first\"}"));
        assertNotNull(Jsonbs.defaultJsonb.fromJson(stream, (Type) KeyValue.class));
        assertFalse(stream.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void fromJsonReaderClass() {
        CloseRememberingReader stream = new CloseRememberingReader(asReader("{\"field\":\"first\"}"));
        assertNotNull(Jsonbs.defaultJsonb.fromJson(stream, KeyValue.class));
        assertFalse(stream.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void fromJsonReaderType() {
        CloseRememberingReader reader = new CloseRememberingReader(asReader("{\"field\":\"first\"}"));
        assertNotNull(Jsonbs.defaultJsonb.fromJson(reader, (Type) KeyValue.class));
        assertFalse(reader.isCloseCalled(), "close() should not be called by ");
    }
    @Test
    public void toJsonOutputStreamObject() {
        CloseRememberingOutputStream stream = new CloseRememberingOutputStream(OutputStream.nullOutputStream());
        Jsonbs.defaultJsonb.toJson(new KeyValue("first"), stream);
        assertFalse(stream.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void toJsonOutputStreamObjectAndType() {
        CloseRememberingOutputStream stream = new CloseRememberingOutputStream(OutputStream.nullOutputStream());
        Jsonbs.defaultJsonb.toJson(new KeyValue("first"), KeyValue.class, stream);
        assertFalse(stream.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void toJsonWriterObject() {
        CloseRememberingWriter writer = new CloseRememberingWriter(Writer.nullWriter());
        Jsonbs.defaultJsonb.toJson(new KeyValue("first"), writer);
        assertFalse(writer.isCloseCalled(), "close() should not be called by ");
    }

    @Test
    public void toJsonWriterObjectAndType() {
        CloseRememberingWriter writer= new CloseRememberingWriter(Writer.nullWriter());
        Jsonbs.defaultJsonb.toJson(new KeyValue("first"), KeyValue.class, writer);
        assertFalse(writer.isCloseCalled(), "close() should not be called by ");
    }

    private static InputStream asInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.US_ASCII));
    }

    private static Reader asReader(String s) {
        return new StringReader(s);
    }

    public static class KeyValue {

        public String field;

        public KeyValue() {

        }

        public KeyValue(String field) {
            this.field = field;
        }
    }

    static final class CloseRememberingOutputStream extends FilterOutputStream {

        private boolean closeCalled;

        CloseRememberingOutputStream(OutputStream out) {
            super(out);
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            super.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

    }

    static final class CloseRememberingInputStream extends FilterInputStream {

        private boolean closeCalled;

        CloseRememberingInputStream(InputStream in) {
            super(in);
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            super.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

    }

    static final class CloseRememberingReader extends FilterReader {

        private boolean closeCalled;

        CloseRememberingReader(Reader in) {
            super(in);
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            super.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

    }

    static final class CloseRememberingWriter extends FilterWriter {

        private boolean closeCalled;

        CloseRememberingWriter(Writer out) {
            super(out);
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            super.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

    }

}
