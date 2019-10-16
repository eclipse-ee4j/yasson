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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Array unmarshaller item implementation for small long.
 */
public class LongArrayDeserializer extends AbstractArrayDeserializer<long[]> {

    private final List<Long> items = new ArrayList<>();

    /**
     * Creates new array of long array deserializer.
     *
     * @param builder deserializer builder
     */
    protected LongArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public long[] getInstance(Unmarshaller unmarshaller) {
        final int size = items.size();
        final long[] longArray = new long[size];
        for (int i = 0; i < size; i++) {
            longArray[i] = items.get(i);
        }
        return longArray;
    }
}
