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

import java.lang.reflect.Constructor;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.ClassMultiReleaseExtension;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Creator of the class instance with the default constructor.
 */
class DefaultObjectInstanceCreator implements ModelDeserializer<JsonParser> {

    private final ModelDeserializer<JsonParser> delegate;
    private final Constructor<?> defaultConstructor;
    private final JsonbException exception;

    DefaultObjectInstanceCreator(ModelDeserializer<JsonParser> delegate,
                                 Class<?> clazz,
                                 Constructor<?> defaultConstructor) {
        this.delegate = delegate;
        this.defaultConstructor = defaultConstructor;
        if (clazz.isInterface()) {
            this.exception = new JsonbException(Messages.getMessage(MessageKeys.INFER_TYPE_FOR_UNMARSHALL, clazz.getName()));
        } else if (defaultConstructor == null) {
            this.exception = ClassMultiReleaseExtension.exceptionToThrow(clazz)
                    .orElse(new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz)));
        } else {
            this.exception = null;
        }
    }

    @Override
    public Object deserialize(JsonParser value, DeserializationContextImpl context) {
        if (exception != null) {
            throw exception;
        }
        Object instance = ReflectionUtils.createNoArgConstructorInstance(defaultConstructor);
        context.setInstance(instance);
        return delegate.deserialize(value, context);
    }
}
