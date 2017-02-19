/*******************************************************************************
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.yasson.model.JsonBindingModel;

import javax.json.stream.JsonGenerator;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for {@link Instant} type.
 *
 * @author David Kral
 */
public class InstantTypeSerializer extends AbstractValueTypeSerializer<Instant> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public InstantTypeSerializer(JsonBindingModel model) {
        super(model);
    }

    @Override
    protected void serialize(Instant obj, JsonGenerator generator, String key, Marshaller marshaller) {
        generator.write(key, formatInstant(obj));
    }

    @Override
    protected void serialize(Instant obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(formatInstant(obj));
    }

    private String formatInstant(Instant obj) {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(obj);
    }
}
