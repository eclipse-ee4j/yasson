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

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.model.JsonBindingModel;

import java.lang.reflect.Type;
import java.time.Period;

/**
 * Deserializer for {@link Period} type.
 * 
 * @author David Kral
 */
public class PeriodTypeDeserializer extends AbstractValueTypeDeserializer<Period> {

    /**
     * Creates a new instance.
     *
     * @param model Binding model.
     */
    public PeriodTypeDeserializer(JsonBindingModel model) {
        super(Period.class, model);
    }

    @Override
    protected Period deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return Period.parse(jsonValue);
    }
}
