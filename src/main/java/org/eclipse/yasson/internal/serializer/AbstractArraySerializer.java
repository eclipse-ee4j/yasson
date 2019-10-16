/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.serializer;

import javax.json.stream.JsonGenerator;

/**
 * Common serializer for arrays.
 *
 * @param <T> Type to serialize.
 */
public abstract class AbstractArraySerializer<T> extends AbstractContainerSerializer<T> implements EmbeddedItem {

    /**
     * Creates new instance of array serializer.
     *
     * @param builder serializer builder
     */
    protected AbstractArraySerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void writeStart(JsonGenerator generator) {
        generator.writeStartArray();
    }

    @Override
    protected void writeStart(String key, JsonGenerator generator) {
        generator.writeStartArray(key);
    }
}
