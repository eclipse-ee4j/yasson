/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * Bias property is not readable by putting field and getter in other class than setter.
 *
 * @author Roman Grigoriadi
 */
public class PartialOverride extends PartialOverrideBase {

    @Override
    public void setIntValue(int intValue) {
        super.setIntValue(intValue);
    }

    @Override
    public void setStrValue(String strValue) {
        super.setStrValue(strValue);
    }
}
