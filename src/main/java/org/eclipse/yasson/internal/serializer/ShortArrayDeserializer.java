/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.yasson.internal.Unmarshaller;

/**
 * Array unmarshaller item implementation for small short.
 */
public class ShortArrayDeserializer extends AbstractArrayDeserializer<short[]> {

    private final List<Short> items = new ArrayList<>();

    /**
     * Creates new short array deserializer.
     *
     * @param builder deserializer builder
     */
    protected ShortArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public short[] getInstance(Unmarshaller unmarshaller) {
        final int size = items.size();
        final short[] shortArray = new short[size];
        for (int i = 0; i < size; i++) {
            shortArray[i] = items.get(i);
        }
        return shortArray;
    }
}
