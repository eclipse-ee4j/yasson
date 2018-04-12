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
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.adapters.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 * @author David Kral
 */
@JsonbTypeAdapter(BoxWithAdapterAdapter.class)
public class BoxWithAdapter {

    private String boxStrField;

    private Integer boxIntegerField;

    public BoxWithAdapter() {
    }

    public BoxWithAdapter(String boxStrField, Integer boxIntegerField) {
        this.boxStrField = boxStrField;
        this.boxIntegerField = boxIntegerField;
    }

    public String getBoxStrField() {
        return boxStrField;
    }

    public void setBoxStrField(String boxStrField) {
        this.boxStrField = boxStrField;
    }

    public Integer getBoxIntegerField() {
        return boxIntegerField;
    }

    public void setBoxIntegerField(Integer boxIntegerField) {
        this.boxIntegerField = boxIntegerField;
    }
}
