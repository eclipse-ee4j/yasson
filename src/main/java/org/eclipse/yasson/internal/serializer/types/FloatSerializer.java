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

package org.eclipse.yasson.internal.serializer.types;

import java.math.BigDecimal;

import jakarta.json.stream.JsonGenerator;

/**
 * Serializer of the {@link Float} type.
 */
class FloatSerializer extends AbstractNumberSerializer<Float> {

    FloatSerializer(TypeSerializerBuilder builder) {
        super(builder);
    }

    @Override
    void writeValue(Float value, JsonGenerator generator) {
        //floats lose precision, after upcasting to doubles in jsonp
        generator.write(new BigDecimal(String.valueOf(value)));
    }

}
