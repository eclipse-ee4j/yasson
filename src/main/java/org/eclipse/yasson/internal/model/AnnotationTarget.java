/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model;

/**
 * Represents the place in which a JSON annotation is applied. Some business functionalities are different based on whether
 * annotation (e.g.
 * {@link javax.json.bind.annotation.JsonbTransient}, {@link org.eclipse.yasson.internal.serializer.JsonbNumberFormatter}, etc
 * .) is being applied on
 * getter method, setter method or directly on the property.
 */
public enum AnnotationTarget {
    /**
     * Indicates annotation has been applied on class level.
     */
    CLASS,

    /**
     * Indicates annotation has been applied on property level.
     */
    PROPERTY,

    /**
     * Indicates annotation has been applied on the getter method of the property.
     */
    GETTER,

    /**
     * Indicates annotation has been applied on the setter method of the property.
     */
    SETTER
}
