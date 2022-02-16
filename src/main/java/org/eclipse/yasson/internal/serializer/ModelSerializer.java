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
 * Type serializer.
 * <br>
 * All the instances are required to be reusable and without any states
 * stored in the class fields.
 */
public interface ModelSerializer {

    /**
     * Serialize provided value or delegate serialization to the next serializer.
     *
     * @param value     value to be serialized
     * @param generator json generator
     * @param context   serialization context
     */
    void serialize(Object value, JsonGenerator generator, SerializationContextImpl context);

}
