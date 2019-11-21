/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal;

import java.util.HashSet;
import java.util.Set;

/**
 * Jsonb processing (serializing/deserializing) context.
 * Instance is thread bound (in contrast to {@link JsonbContext}.
 */
public abstract class ProcessingContext {

    private final JsonbContext jsonbContext;

    /**
     * Used to avoid StackOverflowError, when adapted / serialized object
     * contains contains instance of its type inside it or when object has recursive reference.
     */
    private final Set<Object> currentlyProcessedObjects = new HashSet<>();

    /**
     * Parent instance for marshaller and unmarshaller.
     *
     * @param jsonbContext context of Jsonb
     */
    public ProcessingContext(JsonbContext jsonbContext) {
        this.jsonbContext = jsonbContext;
    }

    /**
     * Jsonb context.
     *
     * @return jsonb context
     */
    public JsonbContext getJsonbContext() {
        return jsonbContext;
    }

    /**
     * Mapping context.
     *
     * @return mapping context
     */
    public MappingContext getMappingContext() {
        return getJsonbContext().getMappingContext();
    }

    /**
     * Adds currently processed object to the {@link Set}.
     *
     * @param object processed object
     * @return if object was added
     */
    public boolean addProcessedObject(Object object) {
        return this.currentlyProcessedObjects.add(object);
    }

    /**
     * Removes processed object from the {@link Set}.
     *
     * @param object processed object
     * @return if object was removed
     */
    public boolean removeProcessedObject(Object object) {
        return currentlyProcessedObjects.remove(object);
    }

}
