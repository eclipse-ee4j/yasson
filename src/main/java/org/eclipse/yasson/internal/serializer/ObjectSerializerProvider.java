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

import org.eclipse.yasson.internal.model.JsonbPropertyInfo;

import javax.json.bind.serializer.JsonbSerializer;

/**
 * @author Roman Grigoriadi
 */
public class ObjectSerializerProvider implements ContainerSerializerProvider {

    @Override
    public JsonbSerializer<?> provideSerializer(JsonbPropertyInfo propertyInfo) {
        return new ObjectSerializer<>(propertyInfo.getWrapper(), propertyInfo.getRuntimeType(), propertyInfo.getClassModel());
    }
}
