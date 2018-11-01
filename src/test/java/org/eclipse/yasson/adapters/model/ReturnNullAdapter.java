/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson.adapters.model;

import javax.json.bind.adapter.JsonbAdapter;

public class ReturnNullAdapter implements JsonbAdapter<Number, String> {
    @Override
    public String adaptToJson(Number obj) throws Exception {
        return null;
    }

    @Override
    public Number adaptFromJson(String obj) throws Exception {
        return null;
    }
}
