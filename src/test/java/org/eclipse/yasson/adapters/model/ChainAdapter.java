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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;

public class ChainAdapter implements JsonbAdapter<Chain, Map<String, Object>>{

    @Override
    public Map<String, Object> adaptToJson(Chain obj) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("has", obj.getHas());
        map.put("linksTo", obj.getLinksTo());
        map.put("name", obj.getName());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Chain adaptFromJson(Map<String, Object> obj) throws Exception {
        if(obj != null) {
            Chain chain = new Chain((String) obj.get("name"));
            chain.setHas((Foo) obj.get("has"));
            adaptFromJson((Map<String, Object>) obj.get("linksTo"));
            return chain;
        } else {
            return null;
        }
        
    }
    
}