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

package org.eclipse.yasson.defaultmapping.basic.model;

/**
 * Encapsulates Number type of field so the serialization and deserialization of number can be tested
 *
 * @author David Kral
 */
public class BigDecimalInNumber {

    private Number bigDecValue;

    public BigDecimalInNumber(){
    }

    public Number getBigDecValue() {
        return bigDecValue;
    }

    public void setBigDecValue(Number bigDecValue) {
        this.bigDecValue = bigDecValue;
    }
}
