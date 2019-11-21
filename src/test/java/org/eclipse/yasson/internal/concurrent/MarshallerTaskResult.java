/*
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.concurrent;

/**
 * Wraps a resulted JSON string with jsonb configuration type, to know against which json to check result.
 */
class MarshallerTaskResult {
    private final String producedJson;
    private final MultiTenancyTest.ConfigurationType configurationType;

    public MarshallerTaskResult(String producedJson, MultiTenancyTest.ConfigurationType configurationType) {
        this.producedJson = producedJson;
        this.configurationType = configurationType;
    }

    public String getProducedJson() {
        return producedJson;
    }

    public MultiTenancyTest.ConfigurationType getConfigurationType() {
        return configurationType;
    }
}
