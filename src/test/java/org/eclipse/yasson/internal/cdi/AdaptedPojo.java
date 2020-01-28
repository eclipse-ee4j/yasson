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

package org.eclipse.yasson.internal.cdi;

import jakarta.json.bind.annotation.JsonbTypeAdapter;

/**
 * @author Roman Grigoriadi
 */

public class AdaptedPojo {

    @JsonbTypeAdapter(CdiDependentAdapter.class)
    public String adaptedValue1 = "1111";

    @JsonbTypeAdapter(CdiDependentAdapter.class)
    public String adaptedValue2 = "1001";

    @JsonbTypeAdapter(NonCdiAdapter.class)
    public String adaptedValue3 = "1010";


}
