/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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

import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 * Serializes byte array as JSON array of ints.
 *
 * @author Bernd Zeitler
 */
public class CharArraySerializer extends AbstractArraySerializer<char[]> {

    protected CharArraySerializer(SerializerBuilder builder) {
        super(builder);
    }

    @Override
    public void serializeContainer(char[] obj, JsonGenerator generator, SerializationContext ctx) {
        for (char c : obj) {
            generator.write(Character.valueOf(c).toString());
        }
    }

}
