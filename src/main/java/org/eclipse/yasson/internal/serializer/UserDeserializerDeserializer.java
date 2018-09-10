/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.JsonbParser;
import org.eclipse.yasson.internal.JsonbRiParser;
import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.UserDeserializerParser;

import javax.json.stream.JsonParser;

/**
 * Item for processing types, to which deserializer is bound.
 *
 * @author Roman Grigoriadi
 */
public class UserDeserializerDeserializer<T> extends AbstractContainerDeserializer<T> {

    private DeserializerBinding<?> deserializerBinding;

    private T deserializerResult;

    /**
     * Create instance of current item with its builder.
     * Contains user provided component for custom deserialization.
     * Decorates calls to JsonParser, with validation logic so user can't left parser cursor
     * in wrong position after returning from deserializerBinding.
     *
     * @param builder {@link DeserializerBuilder} used to build this instance
     * @param deserializerBinding Deserializer.
     */
    protected UserDeserializerDeserializer(DeserializerBuilder builder, DeserializerBinding<?> deserializerBinding) {
        super(builder);
        this.deserializerBinding = deserializerBinding;
    }

    @Override
    public void appendResult(Object result) {
        //ignore internal deserialize() call in custom deserializer
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getInstance(Unmarshaller unmarshaller) {
        return deserializerResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserializeInternal(JsonbParser parser, Unmarshaller context) {
        parserContext = moveToFirst(parser);
        JsonParser.Event lastEvent = parserContext.getLastEvent();
        final UserDeserializerParser userDeserializerParser = new UserDeserializerParser(parser);
        deserializerResult = (T) deserializerBinding.getJsonbDeserializer().deserialize(userDeserializerParser, context, getRuntimeType());
        //Avoid moving parser to the end of the object, if deserializer was for one value only.
        if (lastEvent == JsonParser.Event.START_ARRAY || lastEvent == JsonParser.Event.START_OBJECT) {
            userDeserializerParser.advanceParserToEnd();
        }
    }

    @Override
    protected void deserializeNext(JsonParser parser, Unmarshaller context) {
        throw new UnsupportedOperationException("Not supported for user deserializer");
    }

    /**
     * Don't move anywhere in case of user deserializer.
     */
    @Override
    protected JsonbRiParser.LevelContext moveToFirst(JsonbParser parser) {
        return parser.getCurrentLevel();
    }

}