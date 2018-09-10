/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David Kral - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.internal.properties;

/**
 * Contains all message keys present in language property files
 *
 * @author David Kral
 */
public enum MessageKeys {

    PROCESS_FROM_JSON("processFromJson"),
    PROCESS_TO_JSON("processToJson"),
    CANNOT_MARSHAL_OBJECT("cannotMarshallObject"),
    TYPE_RESOLUTION_ERROR("typeResolutionError"),
    GENERIC_BOUND_NOT_FOUND("genericBoundNotFound"),
    CANT_CONVERT_JSON_VALUE("cantConvertJsonValue"),
    CANT_CREATE_ROOT_INSTANCE("cantCreateRootInstance"),
    NO_DEFAULT_CONSTRUCTOR("noDefaultConstructor"),
    INVOKING_GETTER("invokingGetter"),
    GETTING_VALUE("gettingValue"),
    SETTING_VALUE("settingValue"),
    NO_LOGGER_NAME("noLoggerName"),
    RESOLVE_PARAMETRIZED_TYPE("resolveParametrizedType"),
    CANT_CREATE_INSTANCE("cantCreateInstance"),
    INFER_TYPE_FOR_UNMARSHALL("inferTypeForUnmarshall"),
    IMPL_CLASS_INCOMPATIBLE("implClassIncompatible"),
    NOT_VALUE_TYPE("notValueType"),
    UNEXPECTED_PARSE_EVENT("unexpectedParseEvent"),
    CREATING_HANDLES("creatingHandles"),
    GETTING_VALUE_WITH("gettingValueWith"),
    SETTING_VALUE_WITH("settingValueWith"),
    UNPAIRED_SURROGATE("unpairedSurrogate"),
    ADAPTER_EXCEPTION("adapterException"),
    ADAPTER_FOUND("adapterFound"),
    ADAPTER_INCOMPATIBLE("adapterIncompatible"),
    PROPERTY_ORDER("propertyOrder"),
    UNSUPPORTED_JSONP_SERIALIZER_VALUE("unsupportedJsonpSerializerValue"),
    JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE("Only Boolean type values are supported for JsonbConfig.FORMATTING property."),
    BEAN_MANAGER_NOT_FOUND_JNDI("beanManagerNotFoundJndi"),
    BEAN_MANAGER_NOT_FOUND_NO_PROVIDER("beanManagerNotFoundNoProvider"),
    BEAN_MANAGER_NOT_FOUND_USING_DEFAULT("usingDefaultConstructorInstantiator"),
    IJSON_ENABLED_SINGLE_VALUE("iJsonEnabledSingleValue"),
    PROPERTY_NOT_FOUND_DESERIALIZER("propertyNotFoundDeserializer"),
    SETTING_PROPERTY_DESERIALIZER("settingPropertyDeserializer"),
    CLASS_LOAD_NOT_ALLOWED("classLoadNotAllowed"),
    UNSUPPORTED_DATE_TYPE("dateTypeNotSupported"),
    DATE_PARSE_ERROR("errorParsingDate"),
    OFFSET_DATE_TIME_FROM_MILLIS("offsetDateTimeFromMillis"),
    TIME_TO_EPOCH_MILLIS_ERROR("timeToEpochMillisError"),
    JSONB_CONFIG_PROPERTY_INVALID_TYPE("configPropertyInvalidType"),
    CONVERSION_NOT_SUPPORTED("conversionNotSupported"),
    END_OF_JSON_STRUCTURE("endOfJsonStructure"),
    INVALID_DESERIALIZATION_JSON_TYPE("invalidDeserializationType"),
    ERROR_CALLING_JSONB_CREATOR("errorCallingJsonbCreator"),
    INCOMPATIBLE_FACTORY_CREATOR_RETURN_TYPE("incompatibleFactoryCreatorReturnType"),
    MULTIPLE_JSONB_CREATORS("multipleJsonbCreators"),
    INTERNAL_ERROR("internalError"),
    DESERIALIZE_VALUE_ERROR("deserializeValueError"),
    PARSING_NUMBER("parsingNumber"),
    UNKNOWN_BINARY_DATA_STRATEGY("unknownBinaryDataStrategy"),
    PROPERTY_NAMING_STRATEGY_INVALID("invalidPropertyNamingStrategy"),
    CREATOR_PARAMETER_NOT_ANNOTATED("creatorParameterNotAnnotated"),
    UNKNOWN_JSON_PROPERTY("unknownJsonProperty"),
    JSONB_CREATOR_MISSING_PROPERTY("jsonbCreatorMissingProperty"),
    ZONE_PARSE_ERROR("zoneParseError"),
    JSONB_TRANSIENT_WITH_OTHER_ANNOTATIONS("jsonbTransientWithOtherAnnotations"),
    NON_PARAMETRIZED_TYPE("nonParametrizedType"),
    PROPERTY_NAME_CLASH("propertyNameClash"),
    SQL_DATE_IJSON_ERROR("sqlDateIJsonError"),
    RECURSIVE_REFERENCE("recursiveReference"),
    DATATYPE_FACTORY_CREATION_FAILED("datatypeFactoryCreationFailed"),
    ;

    /** Message bundle key. */
    final String key;

    /**
     * Creates an instance of message bundle key.
     * @param key Message key from bundle.
     */
    MessageKeys(final String key) {
        this.key = key;
    }

}
