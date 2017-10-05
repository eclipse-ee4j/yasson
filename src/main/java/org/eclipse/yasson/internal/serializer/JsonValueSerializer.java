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

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

/**
 * Serializer for {@link JsonValue} type.
 *
 * @author Roman Grigoriadi
 */
public class JsonValueSerializer extends AbstractValueTypeSerializer<JsonValue> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public JsonValueSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected void serialize(JsonValue obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(obj);
    }
}
