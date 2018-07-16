/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.net.URI;

/**
 *
 * @author David Kral
 */
public class ParameterNameTester {
    @JsonbProperty("string")
    public final String name;
    @JsonbProperty("someParam")
    public final String secondParam;

    @JsonbCreator
    public ParameterNameTester(@JsonbProperty("string") String name, @JsonbProperty("someParam") String secondParam) {
        this.name = name;
        this.secondParam = secondParam;
    }
}
