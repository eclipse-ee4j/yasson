/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.jsonp;

import javax.json.JsonNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Test class implementing {@link JsonNumber} interface.
 *
 * @author Dmitry Kornilov
 */
final public class JsonpLong implements JsonNumber {
    private final long num;
    private BigDecimal bigDecimal;  // assigning it lazily on demand

    public JsonpLong(long num) {
        this.num = num;
    }

    @Override
    public int intValue() {
        return bigDecimalValue().intValue();
    }

    @Override
    public int intValueExact() {
        return bigDecimalValue().intValueExact();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue().toBigIntegerExact();
    }


    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public long longValue() {
        return num;
    }

    @Override
    public long longValueExact() {
        return num;
    }

    @Override
    public double doubleValue() {
        return num;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        // reference assignments are atomic. At the most some more temp
        // BigDecimal objects are created
        BigDecimal bd = bigDecimal;
        if (bd == null) {
            bigDecimal = bd = new BigDecimal(num);
        }
        return bd;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    @Override
    public String toString() {
        return Long.toString(num);
    }

}
