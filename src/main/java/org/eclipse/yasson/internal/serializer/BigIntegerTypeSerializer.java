/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.yasson.internal.model.customization.Customization;

import javax.json.stream.JsonGenerator;
import java.math.BigInteger;

/**
 * Serializer for {@link BigInteger} type.
 *
 * @author David Kral
 */
public class BigIntegerTypeSerializer extends AbstractNumberSerializer<BigInteger> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public BigIntegerTypeSerializer(Customization customization) {
        super(customization);
    }

    @Override
    protected void serializeNonFormatted(BigInteger obj, JsonGenerator generator, String key) {
        if (BigNumberUtil.isIEEE754(obj)) {
            generator.write(key, obj);
        } else {
            generator.write(key, obj.toString());
        }
    }

    @Override
    protected void serializeNonFormatted(BigInteger obj, JsonGenerator generator) {
        if (BigNumberUtil.isIEEE754(obj)) {
            generator.write(obj);
        } else {
            generator.write(obj.toString());
        }
    }
}
