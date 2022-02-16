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

package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.SerializationContextImpl;

/**
 * Key name writer. Writes key name of the property if present.
 */
public class KeyWriter implements ModelSerializer {

    private final ModelSerializer delegate;

    /**
     * Create new instance.
     *
     * @param delegate delegate to be called after the key is written
     */
    public KeyWriter(ModelSerializer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
        if (context.getKey() != null) {
            generator.writeKey(context.getKey());
            context.setKey(null);
        }
        delegate.serialize(value, generator, context);
    }

}
