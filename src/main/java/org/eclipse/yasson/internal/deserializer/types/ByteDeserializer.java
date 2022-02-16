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

package org.eclipse.yasson.internal.deserializer.types;

/**
 * Deserializer of the {@link Byte} type.
 */
class ByteDeserializer extends AbstractNumberDeserializer<Byte> {

    ByteDeserializer(TypeDeserializerBuilder builder) {
        super(builder, true);
    }

    @Override
    Byte parseNumberValue(String value) {
        return Byte.parseByte(value);
    }

}
