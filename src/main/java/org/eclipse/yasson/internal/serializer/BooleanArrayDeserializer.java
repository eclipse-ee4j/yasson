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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Array unmarshaller item implementation for booleans.
 */
public class BooleanArrayDeserializer extends AbstractArrayDeserializer<boolean[]> {

    private final List<Boolean> items = new ArrayList<>();

    /**
     * Creates new instance of boolean array deserializer.
     *
     * @param builder deserializer builder
     */
    protected BooleanArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public boolean[] getInstance(Unmarshaller unmarshaller) {
        final int size = items.size();
        final boolean[] byteArray = new boolean[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = items.get(i);
        }
        return byteArray;
    }
}
