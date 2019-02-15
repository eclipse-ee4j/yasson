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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;

/**
 * @author Roman Grigoriadi
 */
public class CreatorConstructorPojo {

    public String str1;

    public String str2;

    public String missing;

    public BigDecimal bigDec;

    public CreatorFactoryMethodPojo innerFactoryCreator;

    @JsonbCreator
    public CreatorConstructorPojo(@JsonbProperty("str1") String str1, @JsonbProperty("str2") String str2, @JsonbProperty("missing") String missing) {
        this.str1 = str1;
        this.str2 = str2;
        this.missing = missing;
    }

}
