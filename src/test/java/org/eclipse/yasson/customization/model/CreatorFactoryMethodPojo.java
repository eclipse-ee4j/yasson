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
public class CreatorFactoryMethodPojo {

    public final String str1;

    public final String str2;

    public final String missing;

    public BigDecimal bigDec;

    private CreatorFactoryMethodPojo(String str1, String str2, String missing) {
        this.str1 = str1;
        this.str2 = str2;
        this.missing = missing;
    }
    @JsonbCreator
    public static CreatorFactoryMethodPojo getInstance(@JsonbProperty("par1") String str1, @JsonbProperty("par2")String str2, @JsonbProperty("missing") String missing) {
        return new CreatorFactoryMethodPojo(str1, str2, missing);
    }
}
