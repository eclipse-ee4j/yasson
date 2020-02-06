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

package org.eclipse.yasson.defaultmapping.lambda;

/**
 * Class used to control serialization of lambda expression generated from functional interfaces with no defaults.
 */
public class Robot implements Addressable {

    private String name;

    Robot(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
