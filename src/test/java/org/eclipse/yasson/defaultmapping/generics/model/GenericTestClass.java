/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.generics.model;

/**
 * Test class used in generics tests.
 *
 * @author Dmitry Kornilov
 */
public class GenericTestClass<T,U> {
    public T field1;
    public U field2;

    public GenericTestClass() {}

    public T getField1() {
        return field1;
    }

    public void setField1(T field1) {
        this.field1 = field1;
    }

    public U getField2() {
        return field2;
    }

    public void setField2(U field2) {
        this.field2 = field2;
    }
}
