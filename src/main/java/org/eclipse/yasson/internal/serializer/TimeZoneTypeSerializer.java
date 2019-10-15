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

import java.util.TimeZone;

import javax.json.stream.JsonGenerator;

import org.eclipse.yasson.internal.Marshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

/**
 * Serializer for {@link TimeZone} type.
 */
public class TimeZoneTypeSerializer extends AbstractValueTypeSerializer<TimeZone> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public TimeZoneTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected void serialize(TimeZone obj, JsonGenerator generator, Marshaller marshaller) {
        generator.write(obj.getID());
    }
}
