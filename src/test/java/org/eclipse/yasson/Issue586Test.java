package org.eclipse.yasson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Verify Jsonb leaves the streams open.
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

    static final class CloseRememberingOutputStream extends OutputStream {

        private final OutputStream delegate;
        private boolean closeCalled;

        CloseRememberingOutputStream(OutputStream delegate) {
            this.delegate = delegate;
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            delegate.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
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

    static final class CloseRememberingInputStream extends InputStream {

        private final InputStream delegate;
        private boolean closeCalled;

        CloseRememberingInputStream(InputStream delegate) {
            this.delegate = delegate;
            this.closeCalled = false;
        }

        @Override
        public void close() throws IOException {
            closeCalled = true;
            delegate.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
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

    static final class CloseRememberingReader extends Reader {

        private final Reader delegate;
        private boolean closeCalled;

        CloseRememberingReader(Reader delegate) {
            this.delegate = delegate;
            this.closeCalled = false;
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
        public void close() throws IOException {
            closeCalled = true;
            delegate.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

        @Override
        public long transferTo(Writer out) throws IOException {
            return delegate.transferTo(out);
        }

    }

    static final class CloseRememberingWriter extends Writer {

        private final Writer delegate;
        private boolean closeCalled;

        CloseRememberingWriter(Writer delegate) {
            this.delegate = delegate;
            this.closeCalled = false;
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
        public void close() throws IOException {
            closeCalled = true;
            delegate.close();
        }

        boolean isCloseCalled() {
            return closeCalled;
        }

    }

}
