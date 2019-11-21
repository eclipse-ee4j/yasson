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

package org.eclipse.yasson.internal.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.json.bind.JsonbException;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Object holding reference to Constructor / Method for custom object creation.
 */
public class JsonbCreator {

    private final Executable executable;

    private final CreatorModel[] params;

    /**
     * Creates a new instance.
     *
     * @param executable    Executable.
     * @param creatorModels Parameters.
     */
    public JsonbCreator(Executable executable, CreatorModel[] creatorModels) {
        this.executable = executable;
        this.params = creatorModels;
    }

    /**
     * Create instance by either constructor or factory method, with provided parameter values and a Class to call on.
     *
     * @param params parameters to be passed into constructor / factory method
     * @param on     class to call onto
     * @param <T>    Type of class / instance
     * @return instance
     */
    @SuppressWarnings("unchecked")
    public <T> T call(Object[] params, Class<T> on) {
        try {
            if (executable instanceof Constructor) {
                return ((Constructor<T>) executable).newInstance(params);
            } else {
                return (T) ((Method) executable).invoke(on, params);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ERROR_CALLING_JSONB_CREATOR, on), e);
        }
    }

    /**
     * True if param name is one of creator params.
     *
     * @param paramName Param name to check.
     * @return True if found.
     */
    public boolean contains(String paramName) {
        return findByName(paramName) != null;
    }

    /**
     * Find creator parameter by name.
     *
     * @param paramName parameter name as it appear in json document.
     * @return Creator parameter.
     */
    public CreatorModel findByName(String paramName) {
        for (CreatorModel param : params) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }

    /**
     * Parameters of this creator.
     *
     * @return Parameters.
     */
    public CreatorModel[] getParams() {
        return params;
    }
}
