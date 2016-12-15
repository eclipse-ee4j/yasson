/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.basic.model;

/**
 * Encapsulates different types of boolean values as a field so that the boolean value's serialization and
 * deserialization could be tested.
 *
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class BooleanModel {
    public Boolean field1;
    public boolean field2;

    public BooleanModel() {
    }

    public BooleanModel(boolean field1, Boolean field2) {
        this.field2 = field2;
        this.field1 = field1;
    }
}
