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
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Deserializer for {@link Number} type.
 * 
 * @author David Kral
 */
public class NumberTypeDeserializer extends AbstractValueTypeDeserializer<Number> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public NumberTypeDeserializer(Customization customization) {
        super(Number.class, customization);
    }

    @Override
    protected Number deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return new BigDecimal(jsonValue);
    }
}
