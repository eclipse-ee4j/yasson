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

package org.eclipse.yasson.customization.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;

/**
 * @author Roman Grigoriadi
 */
public class CreatorFactoryMethodPojo {

    public final String str1;

    public final String str2;

    public BigDecimal bigDec;

    private CreatorFactoryMethodPojo(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }
    @JsonbCreator
    public static CreatorFactoryMethodPojo getInstance(@JsonbProperty("par1") String str1, @JsonbProperty("par2")String str2) {
        return new CreatorFactoryMethodPojo(str1, str2);
    }
}
