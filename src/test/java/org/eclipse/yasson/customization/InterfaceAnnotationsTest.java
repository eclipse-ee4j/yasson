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

package org.eclipse.yasson.customization;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.customization.model.InterfacedPojoB;
import org.eclipse.yasson.customization.model.InterfacedPojoImpl;

/**
 * @author Roman Grigoriadi
 */
public class InterfaceAnnotationsTest {

    @Test
    public void testJsonbPropertyIfcInheritance() {
        InterfacedPojoB pojo = new InterfacedPojoImpl();
        pojo.setPropertyA("AA");
        pojo.setPropertyB("BB");

        final String json = "{\"propA\":\"AA\",\"propB\":\"BB\"}";
        assertEquals(json, defaultJsonb.toJson(pojo));
    }
}
