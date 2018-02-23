/** *****************************************************************************
 * Copyright (c) 2015, 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Maxence Laurent
 ***************************************************************************** */
package org.eclipse.yasson;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Maxence Laurent
 */
public class DefaultGetterInInterface {

    private Jsonb jsonb;

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    public static interface Defaulted {

        default public String getGetterA() {
            return "valueA";
        }
    }

    public static class PojoWithDefault implements Defaulted {
    }

    @Test
    public void testWithDefault() {
        PojoWithDefault pojo = new PojoWithDefault();
        String result = jsonb.toJson(pojo);
        System.out.println("JSON: " + result);
        Assert.assertEquals("{\"getterA\":\"valueA\"}", result);
    }
}
