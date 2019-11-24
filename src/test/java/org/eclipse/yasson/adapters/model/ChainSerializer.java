/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class ChainSerializer implements JsonbSerializer<Chain>{
    
    public static final String RECURSIVE_REFERENCE_ERROR = "There is a recursive reference";
    
    @Override
    public void serialize(Chain obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        if(obj.getHas() != null) {
            ctx.serialize("has", obj.getHas(), generator);
        }
        if(obj.getLinksTo() != null) {
            ctx.serialize("linksTo", obj.getLinksTo(), generator);
        }
        generator.write("name", obj.getName());
        generator.writeEnd();
    }
    
}
