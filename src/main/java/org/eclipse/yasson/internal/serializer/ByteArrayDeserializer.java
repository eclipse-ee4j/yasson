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
 * Array unmarshaller item implementation for small int.
 */
public class ByteArrayDeserializer extends AbstractArrayDeserializer<byte[]> {

    private final List<Byte> items = new ArrayList<>();

    /**
     * Creates new instance of byte array deserializer.
     *
     * @param builder deserializer builder
     */
    protected ByteArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public byte[] getInstance(Unmarshaller unmarshaller) {
        final int size = items.size();
        final byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = items.get(i);
        }
        return byteArray;
    }
}
