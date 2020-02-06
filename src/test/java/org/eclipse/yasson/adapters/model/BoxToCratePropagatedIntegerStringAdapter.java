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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Grigoriadi
 */
public class BoxToCratePropagatedIntegerStringAdapter extends BoxToCratePropagatedTypeArgsAdapter<Integer, String> {
    @Override
    public GenericCrate<GenericBox<List<String>>> adaptToJson(GenericBox<Integer> integerGenericBox) {
        if (integerGenericBox == null) {
            return null;
        }
        GenericCrate<GenericBox<List<String>>> crate = new GenericCrate<>();
        crate.setCrateStrField(integerGenericBox.getStrField());
        crate.setT(new GenericBox<>());
        crate.getT().setX(new ArrayList<>());
        crate.getT().getX().add(integerGenericBox.getX().toString());
        return crate;
    }

    @Override
    public GenericBox<Integer> adaptFromJson(GenericCrate<GenericBox<List<String>>> boxGenericCrate) {
        if (boxGenericCrate == null) {
            return null;
        }
        GenericBox<Integer> genericBox = new GenericBox<>();
        genericBox.setStrField(boxGenericCrate.getCrateStrField());
        genericBox.setX(Integer.parseInt(boxGenericCrate.getT().getX().get(0)));
        return genericBox;
    }
}
