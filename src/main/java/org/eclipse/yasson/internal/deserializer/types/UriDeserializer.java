/*
 * Copyright (c) 2021, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Deserializer of the {@link URI} type.
 */
class UriDeserializer extends TypeDeserializer {

    UriDeserializer(TypeDeserializerBuilder builder) {
        super(builder);
    }

    @Override
    Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.URI_PARSE_ERROR, value), e);
        }
    }
}
