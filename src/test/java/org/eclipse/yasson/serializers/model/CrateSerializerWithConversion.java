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

package org.eclipse.yasson.serializers.model;

import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;

/**
 * @author Roman Grigoriadi
 */
public class CrateSerializerWithConversion extends CrateSerializer {

    @Override
    public void serialize(Crate obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        generator.write("crateStr", "REPLACED crate str");
        ctx.serialize("crateInner", obj.crateInner, generator);
        ctx.serialize("crateInnerList", obj.crateInnerList, generator);
        generator.write("crateBigDec", new BigDecimal("54321"));
        ctx.serialize("date-converted", obj.date, generator);
        generator.writeEnd();
    }
}
