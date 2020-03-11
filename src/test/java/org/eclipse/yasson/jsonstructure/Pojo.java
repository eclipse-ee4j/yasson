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

package org.eclipse.yasson.jsonstructure;

import java.math.BigDecimal;
import java.util.List;

public final class Pojo {

    private String stringProperty;
    private InnerPojo inner;
    private BigDecimal bigDecimalProperty;
    private Long longProperty;

    private List<String> strings;
    private List<BigDecimal> bigDecimals;
    private List<Boolean> booleans;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public BigDecimal getBigDecimalProperty() {
        return bigDecimalProperty;
    }

    public void setBigDecimalProperty(BigDecimal bigDecimalProperty) {
        this.bigDecimalProperty = bigDecimalProperty;
    }

    public InnerPojo getInner() {
        return inner;
    }

    public void setInner(InnerPojo inner) {
        this.inner = inner;
    }

    public Long getLongProperty() {
        return longProperty;
    }

    public void setLongProperty(Long longProperty) {
        this.longProperty = longProperty;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public List<BigDecimal> getBigDecimals() {
        return bigDecimals;
    }

    public void setBigDecimals(List<BigDecimal> bigDecimals) {
        this.bigDecimals = bigDecimals;
    }

    public List<Boolean> getBooleans() {
        return booleans;
    }

    public void setBooleans(List<Boolean> booleans) {
        this.booleans = booleans;
    }
}
