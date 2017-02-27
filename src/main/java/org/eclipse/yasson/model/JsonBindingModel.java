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

package org.eclipse.yasson.model;

import org.eclipse.yasson.model.customization.Customization;

import java.lang.reflect.Type;

/**
 * Represents data binding logic to read write data from/to object or a collection.
 *
 * @author Roman Grigoriadi
 */
public interface JsonBindingModel {

    /**
     * Introspected customization of a property or class.
     *
     * @return immutable property customization
     */
    Customization getCustomization();

    /**
     * Type of a property, either bean property type or collection / array component type.
     *
     * @return class type
     */
    Type getType();

    /**
     * Returns a name of json key that will be written by marshaller.
     *
     * @return name of json key
     */
    String getWriteName();

    /**
     * Current context of json generator.
     *
     * @return context
     */
    JsonContext getContext();

}
