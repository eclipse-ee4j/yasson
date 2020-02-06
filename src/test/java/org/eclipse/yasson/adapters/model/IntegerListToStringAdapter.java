/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.adapter.JsonbAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class IntegerListToStringAdapter implements JsonbAdapter<List<Integer>, String> {

    @Override
    public String adaptToJson(List<Integer> integers) {
        StringBuilder sb = new StringBuilder();
        for (Integer integer : integers) {
            if (!sb.toString().isEmpty()) {
                sb.append("#");
            }
            sb.append(integer);
        }
        return sb.toString();
    }

    @Override
    public List<Integer> adaptFromJson(String s) {
        String[] items = s.split("#");
        List<Integer> integerList = new ArrayList<>();
        for (String item : items) {
            integerList.add(Integer.parseInt(item));
        }
        return integerList;
    }

}
