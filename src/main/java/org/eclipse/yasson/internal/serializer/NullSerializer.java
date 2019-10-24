/*******************************************************************************
 * Copyright (c) 2019 Payara Services and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Patrik Duditš
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializer of null value.
 */
public class NullSerializer implements JsonbSerializer<Object> {
    @Override
    public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
        generator.writeNull();
    }
}
