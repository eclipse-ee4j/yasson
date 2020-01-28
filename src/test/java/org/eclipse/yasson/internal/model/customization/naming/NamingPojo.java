/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model.customization.naming;

/**
 * @author Roman Grigoriadi
 */
public class NamingPojo {

    public NamingPojo() {
    }

    public NamingPojo(String upperCasedProperty, String _startingWithUnderscoreProperty, String CAPS_UNDERSCORE_PROPERTY) {
        this.upperCasedProperty = upperCasedProperty;
        this._startingWithUnderscoreProperty = _startingWithUnderscoreProperty;
        this.CAPS_UNDERSCORE_PROPERTY = CAPS_UNDERSCORE_PROPERTY;
    }

    public String upperCasedProperty;
    public String _startingWithUnderscoreProperty;
    public String CAPS_UNDERSCORE_PROPERTY;
}
