/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;


import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.JsonBindingModel;

import javax.json.stream.JsonGenerator;
import java.util.UUID;

/**
 * Serializer for {@link UUID} type.
 */
public class UUIDTypeSerializer extends AbstractValueTypeSerializer<UUID> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public UUIDTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected void serialize(UUID obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(obj.toString());
    }
}
