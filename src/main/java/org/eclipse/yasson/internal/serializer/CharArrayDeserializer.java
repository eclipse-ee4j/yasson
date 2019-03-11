/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.Unmarshaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Array unmarshaller item implementation for char.
 *
 * @author Bernd Zeitler
 */
public class CharArrayDeserializer extends AbstractArrayDeserializer<char[]> {

    private final List<Character> items = new ArrayList<>();

    protected CharArrayDeserializer(DeserializerBuilder builder) {
        super(builder);
    }

    @Override
    protected List<?> getItems() {
        return items;
    }

    @Override
    public char[] getInstance(Unmarshaller unmarshaller) {
        final int size = items.size();
        final char[] charArray = new char[size];
        for(int i=0; i<size; i++) {
            charArray[i] = items.get(i);
        }
        return charArray;
    }
}
