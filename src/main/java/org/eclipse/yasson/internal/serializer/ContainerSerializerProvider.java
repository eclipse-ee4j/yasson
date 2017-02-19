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

import org.eclipse.yasson.model.JsonbPropertyInfo;

import javax.json.bind.serializer.JsonbSerializer;

/**
 * Provides container serializer instance.
 *
 * @author Roman Grigoriadi
 */
public interface ContainerSerializerProvider {

    /**
     * Provides container serializer instance for given property.
     *
     * @param propertyInfo Property to create serializer for.
     * @return Serializer instance.
     */
    JsonbSerializer<?> provideSerializer(JsonbPropertyInfo propertyInfo);
}
