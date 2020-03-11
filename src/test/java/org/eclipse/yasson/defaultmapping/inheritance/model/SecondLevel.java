/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class SecondLevel extends FirstLevel {

    private String inSecondLevel;

    //hides inFirstLevel in FirstLevelGeneric
    private String inFirstLevel;

    public String getInSecondLevel() {
        return inSecondLevel;
    }

    public void setInSecondLevel(String inSecondLevel) {
        this.inSecondLevel = inSecondLevel;
    }

    @Override
    public String getInFirstLevel() {
        return inFirstLevel;
    }

    @Override
    public void setInFirstLevel(String inFirstLevel) {
        this.inFirstLevel = inFirstLevel;
    }
}
