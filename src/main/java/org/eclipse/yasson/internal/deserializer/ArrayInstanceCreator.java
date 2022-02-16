/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer;

import java.lang.reflect.Array;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Creator of the array instance based upon the array type.
 */
abstract class ArrayInstanceCreator implements ModelDeserializer<JsonParser> {

    private static final Map<Class<?>, Function<ModelDeserializer<JsonParser>, ArrayInstanceCreator>> CACHE;

    static {
        CACHE = Map.of(boolean[].class, BooleanArrayCreator::new,
                       byte[].class, ByteArrayCreator::new,
                       char[].class, CharArrayCreator::new,
                       double[].class, DoubleArrayCreator::new,
                       float[].class, FloatArrayCreator::new,
                       int[].class, IntegerArrayCreator::new,
                       long[].class, LongArrayCreator::new,
                       short[].class, ShortArrayCreator::new);
    }

    private final ModelDeserializer<JsonParser> delegate;

    private ArrayInstanceCreator(ModelDeserializer<JsonParser> delegate) {
        this.delegate = delegate;
    }

    static ArrayInstanceCreator create(Class<?> arrayType, Class<?> componentClass, ModelDeserializer<JsonParser> delegate) {
        if (CACHE.containsKey(arrayType)) {
            return CACHE.get(arrayType).apply(delegate);
        }
        return new ObjectArrayCreator(delegate, componentClass);
    }

    static ModelDeserializer<JsonParser> createBase64Deserializer(String strategy,
                                                                  ModelDeserializer<JsonParser> delegate) {
        return new Base64ByteArray(strategy, delegate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        Collection<Object> collection = (Collection<Object>) delegate.deserialize(value, context);
        return resolveArrayInstance(collection);
    }

    protected abstract Object resolveArrayInstance(Collection<Object> collection);

    private static final class IntegerArrayCreator extends ArrayInstanceCreator {

        private IntegerArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            int[] intArray = new int[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                intArray[i] = (int) obj;
                i++;
            }
            return intArray;
        }

    }

    private static final class ByteArrayCreator extends ArrayInstanceCreator {

        private ByteArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            byte[] byteArray = new byte[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                byteArray[i] = (byte) obj;
                i++;
            }
            return byteArray;
        }

    }

    private static final class ShortArrayCreator extends ArrayInstanceCreator {

        private ShortArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            short[] shortArray = new short[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                shortArray[i] = (short) obj;
                i++;
            }
            return shortArray;
        }

    }

    private static final class LongArrayCreator extends ArrayInstanceCreator {

        private LongArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            long[] longArray = new long[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                longArray[i] = (long) obj;
                i++;
            }
            return longArray;
        }

    }

    private static final class FloatArrayCreator extends ArrayInstanceCreator {

        private FloatArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            float[] floatArray = new float[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                floatArray[i] = (float) obj;
                i++;
            }
            return floatArray;
        }

    }

    private static final class DoubleArrayCreator extends ArrayInstanceCreator {

        private DoubleArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            double[] doubleArray = new double[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                doubleArray[i] = (double) obj;
                i++;
            }
            return doubleArray;
        }

    }

    private static final class BooleanArrayCreator extends ArrayInstanceCreator {

        private BooleanArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            boolean[] booleanArray = new boolean[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                booleanArray[i] = (boolean) obj;
                i++;
            }
            return booleanArray;
        }

    }

    private static final class CharArrayCreator extends ArrayInstanceCreator {

        private CharArrayCreator(ModelDeserializer<JsonParser> delegate) {
            super(delegate);
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            char[] charArray = new char[collection.size()];
            int i = 0;
            for (Object obj : collection) {
                charArray[i] = (char) obj;
                i++;
            }
            return charArray;
        }

    }

    private static final class ObjectArrayCreator extends ArrayInstanceCreator {

        private final Class<?> componentClass;

        private ObjectArrayCreator(ModelDeserializer<JsonParser> delegate, Class<?> componentClass) {
            super(delegate);
            this.componentClass = componentClass;
        }

        @Override
        protected Object resolveArrayInstance(Collection<Object> collection) {
            Object[] objectArray = (Object[]) Array.newInstance(componentClass, collection.size());
            int i = 0;
            for (Object obj : collection) {
                objectArray[i] = obj;
                i++;
            }
            return objectArray;
        }

    }

    private static final class Base64ByteArray implements ModelDeserializer<JsonParser> {

        private final Base64.Decoder decoder;
        private final ModelDeserializer<JsonParser> delegate;

        private Base64ByteArray(String strategy,
                                ModelDeserializer<JsonParser> delegate) {
            this.decoder = getDecoder(strategy);
            this.delegate = delegate;
        }

        public Base64.Decoder getDecoder(String strategy) {
            switch (strategy) {
            case BinaryDataStrategy.BASE_64:
                return Base64.getDecoder();
            case BinaryDataStrategy.BASE_64_URL:
                return Base64.getUrlDecoder();
            default:
                throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Invalid strategy: " + strategy));
            }
        }

        @Override
        public Object deserialize(JsonParser value, DeserializationContextImpl context) {
            return decoder.decode((String) delegate.deserialize(value, context));
        }
    }
}
