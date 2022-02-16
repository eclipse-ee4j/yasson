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

package org.eclipse.yasson.internal.deserializer;

import org.eclipse.yasson.internal.DeserializationContextImpl;

/**
 * Type deserializer.
 * <br>
 * All the instances are required to be reusable and without any states
 * stored in the class fields.
 *
 * @param <T> represents the content value this deserializer is using
 */
public interface ModelDeserializer<T> {

    /**
     * Deserialize provided value or delegate deserialization to the next deserializer.
     *
     * @param value   value to be deserialized
     * @param context deserialization context
     * @return deserialized value
     */
    Object deserialize(T value, DeserializationContextImpl context);

}
