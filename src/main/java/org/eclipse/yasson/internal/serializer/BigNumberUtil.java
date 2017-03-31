/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class for checking of IEEE-754 standard in big numbers
 *
 * @author David Kral
 */
class BigNumberUtil {

    // 53 means max bit value of number with sign bit included
    private static final int MAX_BIT_SIZE = 53;

    // -1022 is the lowest range of the exponent
    // more https://en.wikipedia.org/wiki/Exponent_bias
    private static final int MIN_RANGE = -1022;

    // 1023 is the highest range of the exponent
    // more https://en.wikipedia.org/wiki/Exponent_bias
    private static final int MAX_RANGE = 1023;

    /**
     * Checks whether the value of {@link BigDecimal} matches format IEEE-754
     *
     * @param value value which is going to be checked
     * @return true if value matches format IEEE-754
     */
    static boolean isIEEE754(BigDecimal value) {
        //scale of the number
        int scale = value.scale();
        //bit value of number without scale
        int valBits = value.unscaledValue().abs().bitLength();
        //bit value of scaled number
        int intBitsScaled = value.toBigInteger().bitLength();
        // Number whose bit length is than 53 or is not in range is considered as non IEEE 754-2008 binary64 compliant
        return valBits <= MAX_BIT_SIZE && intBitsScaled <= MAX_BIT_SIZE && MIN_RANGE <= scale && scale <= MAX_RANGE;
    }

    /**
     * Checks whether the value of {@link BigInteger} matches format IEEE-754
     *
     * @param value value which is going to be checked
     * @return true if value matches format IEEE-754
     */
    static boolean isIEEE754(BigInteger value) {
        // Number whose bit length is than 53 is considered as non IEEE 754-2008 binary64 compliant
        return value.abs().bitLength() <= MAX_BIT_SIZE;
    }

}
