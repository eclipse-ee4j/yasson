/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson;

import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * Adds methods to Jsonb that are operating directly with {@link JsonGenerator} or {@link JsonParser} types.
 * <p>
 * {@link javax.json.spi.JsonProvider} operates on top of the
 * {@link java.io.InputStream} / {@link java.io.OutputStream} or {@link java.io.Reader} / {@link java.io.Writer}
 * and creates generator / parser instances during runtime.
 * </p>
 * <p>
 * This interface accepts instantiated generators and parsers with different input / output sources.
 * </p>
 */
public interface Jsonb extends javax.json.bind.Jsonb {


    /**
     * Reads in a JSON data with a specified {@link JsonParser} and return the
     * resulting content tree. Provided json parser must be fully initialized,
     * no further configurations will be applied.
     *
     * @param jsonParser
     *      The json parser instance to be used to read JSON data.
     * @param type
     *      Type of the content tree's root object.
     * @param <T>
     *      Type of the content tree's root object.
     *
     * @return the newly created root object of the java content tree
     *
     * @throws JsonbException
     *     If any unexpected error(s) occur(s) during deserialization.
     * @throws NullPointerException
     *      If any of the parameters is {@code null}.
     */
    <T> T fromJson(JsonParser jsonParser, Class<T> type) throws JsonbException;

    /**
     * Writes the object content tree with a specified {@link JsonGenerator}.
     * Provided json generator must be fully initialized, no further configurations are applied.
     *
     * @param object
     *      The object content tree to be serialized.
     * @param jsonGenerator
     *      The json generator to write JSON data. Upon a successful completion,
     *      the generator will be closed by this method.
     *
     * @throws JsonbException If any unexpected problem occurs during the
     * serialization.
     * @throws NullPointerException
     *      If any of the parameters is {@code null}.
     *
     * @since JSON Binding 1.0
     */
    void toJson(Object object, JsonGenerator jsonGenerator) throws JsonbException;

    /**
     * Writes the object content tree with a specified {@link JsonGenerator}.
     * Provided json generator must be fully initialized, no further configurations are applied.
     *
     * @param object
     *      The object content tree to be serialized.
     *
     * @param runtimeType
     *      Runtime type of the content tree's root object.
     *
     * @param jsonGenerator
     *      The json generator to write JSON data. Upon a successful completion,
     *      the generator will be closed by this method.
     *
     * @throws JsonbException If any unexpected problem occurs during the
     * serialization.
     * @throws NullPointerException
     *      If any of the parameters is {@code null}.
     *
     * @since JSON Binding 1.0
     */
    void toJson(Object object, Type runtimeType, JsonGenerator jsonGenerator) throws JsonbException;
}
