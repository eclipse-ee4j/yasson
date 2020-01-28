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

package org.eclipse.yasson.internal.concurrent;

/**
 * A result produced by marshalling or unmarshalling operation called on jsonb.
 * @param <T> For marshalling its String, for unmarshalling Customer.
 */
class JsonProcessingResult<T> {
    private final T result;

    private final String dispatcherThreadName;

    private final String jobId;

    public JsonProcessingResult(T result, String dispatcherThreadName, String jobId) {
        this.result = result;
        this.dispatcherThreadName = dispatcherThreadName;
        this.jobId = jobId;
    }

    public T getResult() {
        return result;
    }

    public String getDispatcherThreadName() {
        return dispatcherThreadName;
    }

    public String getJobId() {
        return jobId;
    }
}
