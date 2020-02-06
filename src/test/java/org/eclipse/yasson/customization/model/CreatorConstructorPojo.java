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
public class CreatorConstructorPojo {

    public String str1;

    public String str2;

    public BigDecimal bigDec;

    public CreatorFactoryMethodPojo innerFactoryCreator;

    @JsonbCreator
    public CreatorConstructorPojo(@JsonbProperty("str1") String str1, @JsonbProperty("str2") String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

}
