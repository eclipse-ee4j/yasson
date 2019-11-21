/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;

public class FooAdapter implements JsonbAdapter<Foo, Map<String, String>>{

    @Override
    public Map<String, String> adaptToJson(Foo obj) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("bar", obj.getBar());
        return map;
    }

    @Override
    public Foo adaptFromJson( Map<String, String> obj) throws Exception {
        return new Foo(obj.get("bar").toString());
    }
    
}
