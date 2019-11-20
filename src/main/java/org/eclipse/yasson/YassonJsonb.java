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

package org.eclipse.yasson;

import java.lang.reflect.Type;

import javax.json.JsonStructure;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

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
public interface YassonJsonb extends javax.json.bind.Jsonb {

    /**
     * Reads in a JSON data with a specified {@link JsonParser} and return the
     * resulting content tree. Provided json parser must be fully initialized,
     * no further configurations will be applied.
     *
     * @param jsonParser The json parser instance to be used to read JSON data.
     * @param type       Type of the content tree's root object.
     * @param <T>        Type of the content tree's root object.
     * @return the newly created root object of the java content tree
     * @throws JsonbException       If any unexpected error(s) occur(s) during deserialization.
     */
    <T> T fromJson(JsonParser jsonParser, Class<T> type) throws JsonbException;

    /**
     * Reads in a JSON data with a specified {@link JsonParser} and return the
     * resulting content tree. Provided json parser must be fully initialized,
     * no further configurations will be applied.
     *
     * @param jsonParser  The json parser instance to be used to read JSON data.
     * @param runtimeType Runtime type of the content tree's root object.
     * @param <T>         Type of the content tree's root object.
     * @return the newly created root object of the java content tree
     * @throws JsonbException       If any unexpected error(s) occur(s) during deserialization.
     */
    <T> T fromJson(JsonParser jsonParser, Type runtimeType) throws JsonbException;

    /**
     * Reads a {@link JsonStructure} and and converts it into
     * resulting java content tree.
     *
     * @param jsonStructure {@link JsonStructure} to be used as a source for conversion.
     * @param type          Type of the content tree's root object.
     * @param <T>           Type of the content tree's root object.
     * @return the newly created root object of the java content tree
     * @throws JsonbException       If any unexpected error(s) occur(s) during conversion.
     */
    <T> T fromJsonStructure(JsonStructure jsonStructure, Class<T> type) throws JsonbException;

    /**
     * Reads a {@link JsonStructure} and and converts it into
     * resulting java content tree.
     *
     * @param jsonStructure {@link JsonStructure} to be used as a source for conversion.
     * @param runtimeType   Runtime type of the content tree's root object.
     * @param <T>           Type of the content tree's root object.
     * @return the newly created root object of the java content tree
     * @throws JsonbException       If any unexpected error(s) occur(s) during deserialization.
     */
    <T> T fromJsonStructure(JsonStructure jsonStructure, Type runtimeType) throws JsonbException;

    /**
     * Writes the object content tree with a specified {@link JsonGenerator}.
     * Provided json generator must be fully initialized, no further configurations are applied.
     *
     * @param object        The object content tree to be serialized.
     * @param jsonGenerator The json generator to write JSON data. The generator is not closed
     *                      on a completion for further interaction.
     * @throws JsonbException       If any unexpected problem occurs during the
     *                              serialization.
     * @since JSON Binding 1.0
     */
    void toJson(Object object, JsonGenerator jsonGenerator) throws JsonbException;

    /**
     * Writes the object content tree with a specified {@link JsonGenerator}.
     * Provided json generator must be fully initialized, no further configurations are applied.
     *
     * @param object        The object content tree to be serialized.
     * @param runtimeType   Runtime type of the content tree's root object.
     * @param jsonGenerator The json generator to write JSON data. The generator is not closed
     *                      on a completion for further interaction.
     * @throws JsonbException       If any unexpected problem occurs during the
     *                              serialization.
     * @since JSON Binding 1.0
     */
    void toJson(Object object, Type runtimeType, JsonGenerator jsonGenerator) throws JsonbException;

    /**
     * Serializes the object content tree to a {@link javax.json.JsonStructure}.
     *
     * @param object The object content tree to be serialized.
     * @return The {@link JsonStructure} serialized from java content tree.
     * @throws JsonbException       If any unexpected problem occurs during the
     *                              serialization.
     * @since JSON Binding 1.0
     */
    JsonStructure toJsonStructure(Object object) throws JsonbException;

    /**
     * Serializes the object content tree to a {@link javax.json.JsonStructure}.
     *
     * @param object      The object content tree to be serialized.
     * @param runtimeType Runtime type of the content tree's root object.
     * @return The {@link JsonStructure} serialized from java content tree.
     * @throws JsonbException       If any unexpected problem occurs during the
     *                              serialization.
     * @since JSON Binding 1.0
     */
    JsonStructure toJsonStructure(Object object, Type runtimeType) throws JsonbException;
}
