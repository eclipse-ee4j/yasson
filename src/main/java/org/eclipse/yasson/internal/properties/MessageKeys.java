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

package org.eclipse.yasson.internal.properties;

import java.beans.ConstructorProperties;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;

import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

/**
 * Contains all message keys present in language property files.
 */
public enum MessageKeys {

    /**
     * An error has occurred while json deserialization.
     */
    PROCESS_FROM_JSON("processFromJson"),
    /**
     * An error has occurred while object serialization.
     */
    PROCESS_TO_JSON("processToJson"),
    /**
     * Object cannot be serialized.
     */
    CANNOT_MARSHAL_OBJECT("cannotMarshallObject"),
    /**
     * An error has occurred during runtime type resolution.
     */
    TYPE_RESOLUTION_ERROR("typeResolutionError"),
    /**
     * Generic bound not found.
     */
    GENERIC_BOUND_NOT_FOUND("genericBoundNotFound"),
    /**
     * {@link JsonValue} could not be converted to some type.
     */
    CANT_CONVERT_JSON_VALUE("cantConvertJsonValue"),
    /**
     * Root instance could not be created.
     */
    CANT_CREATE_ROOT_INSTANCE("cantCreateRootInstance"),
    /**
     * Class does not have default constructor.
     */
    NO_DEFAULT_CONSTRUCTOR("noDefaultConstructor"),
    /**
     * There has been an error while invoking getter.
     */
    INVOKING_GETTER("invokingGetter"),
    /**
     * Could not get field value.
     */
    GETTING_VALUE("gettingValue"),
    /**
     * Could not set field value.
     */
    SETTING_VALUE("settingValue"),
    /**
     * No logger name provided.
     */
    NO_LOGGER_NAME("noLoggerName"),
    /**
     * {@link ParameterizedType} superclass could not be resolved.
     */
    RESOLVE_PARAMETRIZED_TYPE("resolveParametrizedType"),
    /**
     * Instance could not be created.
     */
    CANT_CREATE_INSTANCE("cantCreateInstance"),
    /**
     * Type could not be inferred to deserialization.
     */
    INFER_TYPE_FOR_UNMARSHALL("inferTypeForUnmarshall"),
    /**
     * Implementation class is not compatible.
     */
    IMPL_CLASS_INCOMPATIBLE("implClassIncompatible"),
    /**
     * Value is not of target type.
     */
    NOT_VALUE_TYPE("notValueType"),
    /**
     * Unexpected parser event has occurred.
     */
    UNEXPECTED_PARSE_EVENT("unexpectedParseEvent"),
    /**
     * Am error has occurred while creating handles.
     */
    CREATING_HANDLES("creatingHandles"),
    /**
     * Could not get field value with method.
     */
    GETTING_VALUE_WITH("gettingValueWith"),
    /**
     * Could not set field value with method.
     */
    SETTING_VALUE_WITH("settingValueWith"),
    /**
     * String contains unpaired surrogate.
     */
    UNPAIRED_SURROGATE("unpairedSurrogate"),
    /**
     * An exception occurred while adapting object.
     */
    ADAPTER_EXCEPTION("adapterException"),
    /**
     * Adapter for current type has been found.
     */
    ADAPTER_FOUND("adapterFound"),
    /**
     * Adapter is incompatible for current type.
     */
    ADAPTER_INCOMPATIBLE("adapterIncompatible"),
    /**
     * Property order strategy not recognized.
     */
    PROPERTY_ORDER("propertyOrder"),
    /**
     * Unsupported Jsonp serializer value.
     */
    UNSUPPORTED_JSONP_SERIALIZER_VALUE("unsupportedJsonpSerializerValue"),
    /**
     * {@link JsonbConfig#FORMATTING} supports only Boolean types.
     */
    JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE("Only Boolean type values are supported for JsonbConfig.FORMATTING property."),
    /**
     * No JNDI provider found.
     */
    NO_JNDI_ENVIRONMENT("noJndiEnvironment"),
    /**
     * CDI API provider has not been found.
     */
    NO_CDI_API_PROVIDER("noCdiApiProvider"),
    /**
     * Insufficient permissions to access property.
     */
    ILLEGAL_ACCESS("illegalAccess"),
    /**
     * CDI bean manager not found, serializers and adapters will not have CDI support.
     */
    BEAN_MANAGER_NOT_FOUND_USING_DEFAULT("usingDefaultConstructorInstantiator"),
    /**
     * CDI environment is not available.
     */
    NO_CDI_ENVIRONMENT("noCdiEnvironment"),
    /**
     * Cannot serialize single value due to I-Json support is enabled.
     */
    IJSON_ENABLED_SINGLE_VALUE("iJsonEnabledSingleValue"),
    /**
     * Property not found in target class.
     */
    PROPERTY_NOT_FOUND_DESERIALIZER("propertyNotFoundDeserializer"),
    /**
     * Property could not be set to target property.
     */
    SETTING_PROPERTY_DESERIALIZER("settingPropertyDeserializer"),
    /**
     * Loading of specific class in not allowed.
     */
    CLASS_LOAD_NOT_ALLOWED("classLoadNotAllowed"),
    /**
     * Data type is not supported.
     */
    UNSUPPORTED_DATE_TYPE("dateTypeNotSupported"),
    /**
     * There has been an error during parsing number.
     */
    DATE_PARSE_ERROR("errorParsingDate"),
    /**
     * Parsing offset date from epoch millisecond, UTC zone offset will be used.
     */
    OFFSET_DATE_TIME_FROM_MILLIS("offsetDateTimeFromMillis"),
    /**
     * Target date object could not be converted to or from epoch millis.
     */
    TIME_TO_EPOCH_MILLIS_ERROR("timeToEpochMillisError"),
    /**
     * Jsonb config property contains invalid type.
     */
    JSONB_CONFIG_PROPERTY_INVALID_TYPE("configPropertyInvalidType"),
    /**
     * Conversion target type from or to String is not supported.
     */
    CONVERSION_NOT_SUPPORTED("conversionNotSupported"),
    /**
     * End of the json structure reached.
     */
    END_OF_JSON_STRUCTURE("endOfJsonStructure"),
    /**
     * Json value type could not be deserialized to the target type.
     */
    INVALID_DESERIALIZATION_JSON_TYPE("invalidDeserializationType"),
    /**
     * An error occurred while calling {@link JsonbCreator}.
     */
    ERROR_CALLING_JSONB_CREATOR("errorCallingJsonbCreator"),
    /**
     * Return type of the {@link JsonbCreator} has to be the same as target type.
     */
    INCOMPATIBLE_FACTORY_CREATOR_RETURN_TYPE("incompatibleFactoryCreatorReturnType"),
    /**
     * Only one {@link JsonbCreator} can be present in the class.
     */
    MULTIPLE_JSONB_CREATORS("multipleJsonbCreators"),
    /**
     * An internal error has occurred.
     */
    INTERNAL_ERROR("internalError"),
    /**
     * There has been an error during property serialization.
     */
    SERIALIZE_PROPERTY_ERROR("serializePropertyError"),
    /**
     * There has been an error during value deserialization.
     */
    DESERIALIZE_VALUE_ERROR("deserializeValueError"),
    /**
     * Number has unsupported format.
     */
    PARSING_NUMBER("parsingNumber"),
    /**
     * Unknown binary data strategy selected.
     */
    UNKNOWN_BINARY_DATA_STRATEGY("unknownBinaryDataStrategy"),
    /**
     * Invalid property naming strategy selected.
     */
    PROPERTY_NAMING_STRATEGY_INVALID("invalidPropertyNamingStrategy"),
    /**
     * Creator parameter has to be annotated by {@link JsonbProperty} annotation.
     */
    CREATOR_PARAMETER_NOT_ANNOTATED("creatorParameterNotAnnotated"),
    /**
     * Json property could not be mapped to the target class.
     */
    UNKNOWN_JSON_PROPERTY("unknownJsonProperty"),
    /**
     * Json does not contain all necessary properties for {@link JsonbCreator}.
     */
    JSONB_CREATOR_MISSING_PROPERTY("jsonbCreatorMissingProperty"),
    /**
     * There has been an error during zone deserialization.
     */
    ZONE_PARSE_ERROR("zoneParseError"),
    /**
     * {@link JsonbTransient} was not the only annotation on class property.
     */
    JSONB_TRANSIENT_WITH_OTHER_ANNOTATIONS("jsonbTransientWithOtherAnnotations"),
    /**
     * Target type is not {@link ParameterizedType}.
     */
    NON_PARAMETRIZED_TYPE("nonParametrizedType"),
    /**
     * Handled property has the same read/write name in target class as some other property present there.
     */
    PROPERTY_NAME_CLASH("propertyNameClash"),
    /**
     * {@link Date} is not supported I-Json is enabled.
     */
    SQL_DATE_IJSON_ERROR("sqlDateIJsonError"),
    /**
     * Recursive reference detected.
     */
    RECURSIVE_REFERENCE("recursiveReference"),
    /**
     * An error occurred while DatatypeFactory creation.
     */
    DATATYPE_FACTORY_CREATION_FAILED("datatypeFactoryCreationFailed"),
    /**
     * Bean manager provider not found.
     */
    BEAN_MANAGER_PROVIDER_NOT_FOUND("beanManagerProviderNotFound"),
    /**
     * More than one constructor annotated with {@link ConstructorProperties} declared in target class.
     */
    MULTIPLE_CONSTRUCTOR_PROPERTIES_CREATORS("multipleConstructorPropertiesCreators"),
    /**
     * Target annotation is not visible in modules or classpath.
     */
    ANNOTATION_NOT_AVAILABLE("annotationNotAvailable"),
    /**
     * Missing value property in target annotation.
     */
    MISSING_VALUE_PROPERTY_IN_ANNOTATION("missingValuePropertyInAnnotation"),
    /**
     * Target json value is not valid {@link JsonNumber}.
     */
    NUMBER_INCOMPATIBLE_VALUE_TYPE_ARRAY("numberIncompatibleValueTypeArray"),
    /**
     * Target json value is not valid {@link JsonNumber}.
     */
    NUMBER_INCOMPATIBLE_VALUE_TYPE_OBJECT("numberIncompatibleValueTypeObject");

    /**
     * Message bundle key.
     */
    private final String key;

    /**
     * Creates an instance of message bundle key.
     *
     * @param key Message key from bundle.
     */
    MessageKeys(final String key) {
        this.key = key;
    }

    /**
     * Returns message bundle key.
     *
     * @return message bundle key
     */
    public String getKey() {
        return key;
    }
}
