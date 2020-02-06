/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

/**
 * Serializer for arrays of longs.
 */
public class LongArraySerializer extends AbstractArraySerializer<long[]> {

    /**
     * Creates new array of long array serializer.
     *
     * @param builder serializer builder
     */
    protected LongArraySerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    protected void serializeInternal(long[] arr, JsonGenerator generator, SerializationContext ctx) {
        for (long obj : arr) {
            generator.write(obj);
        }
    }
}
