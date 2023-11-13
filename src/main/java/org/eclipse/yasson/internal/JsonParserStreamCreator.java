/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonParserStreamCreator {

    private final JsonParser parser;
    private final boolean nextBeforeCreationOfValueStream;
    private final Supplier<Boolean> canProduceArrayStream;
    private final Supplier<Boolean> canProduceObjectStream;
    private final Supplier<Boolean> canProduceValueStream;

    public JsonParserStreamCreator(JsonParser parser, boolean nextBeforeCreationOfValueStream, Supplier<Boolean> canProduceArrayStream,
            Supplier<Boolean> canProduceObjectStream,
            Supplier<Boolean> canProduceValueStream) {

        this.parser = Objects.requireNonNull(parser);
        this.nextBeforeCreationOfValueStream = nextBeforeCreationOfValueStream;
        this.canProduceArrayStream = canProduceArrayStream;
        this.canProduceObjectStream = canProduceObjectStream;
        this.canProduceValueStream = canProduceValueStream;
    }

    /**
     * Creates new {@link Stream} from values from {@link Supplier}. The stream delivers the values as long as supplier delivers non-null values
     *
     * @param supplier supplier of the values
     * @param <T>      type of the values which are delivered by the supplier and the stream
     * @return stream of values from given supplier
     */
    private static <T> Stream<T> streamFromSupplier(Supplier<T> supplier) {
        return Stream.iterate(Objects.requireNonNull(supplier).get(), Objects::nonNull, value -> supplier.get());
    }

    public Stream<JsonValue> getArrayStream() {
        if (canProduceArrayStream.get()) {
            return streamFromSupplier(() -> (parser.hasNext() && parser.next() != JsonParser.Event.END_ARRAY) ? parser.getValue() : null);
        } else {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Outside of array context"));
        }
    }

    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        if (canProduceObjectStream.get()) {
            return streamFromSupplier(() -> {
                JsonParser.Event e = parser.next();
                if (e == JsonParser.Event.END_OBJECT) {
                    return null;
                } else if (e != JsonParser.Event.KEY_NAME) {
                    throw new JsonException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Cannot read object key"));
                } else {
                    String key = parser.getString();
                    if (!parser.hasNext()) {
                        throw new JsonException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Cannot read object value"));
                    } else {
                        parser.next();
                        return new AbstractMap.SimpleImmutableEntry<>(key, parser.getValue());
                    }
                }
            });
        } else {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Outside of object context"));
        }
    }

    public Stream<JsonValue> getValueStream() {
        if (canProduceValueStream.get()) {
            if (nextBeforeCreationOfValueStream) {
                parser.next();
            }

            return streamFromSupplier(() -> {
                if (parser.hasNext()) {
                    return parser.getValue();
                } else {
                    return null;
                }
            });
        } else {
            throw new IllegalStateException(
                    Messages.getMessage(MessageKeys.INTERNAL_ERROR, "getValueStream can be only called at the root level of JSON structure"));
        }
    }
}
