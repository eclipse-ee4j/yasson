/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link Byte} type.
 */
public class ByteTypeSerializer extends AbstractNumberSerializer<Byte> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public ByteTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected void serializeNonFormatted(Byte obj, JsonGenerator generator, String key) {
        generator.write(key, obj);
    }

    @Override
    protected void serializeNonFormatted(Byte obj, JsonGenerator generator) {
        generator.write(obj);
    }
}
