/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.json.bind.JsonbException;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

/**
 * Utility class for resolution of generics during unmarshalling.
 */
public class ReflectionUtils {

    private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

    private ReflectionUtils() {
        throw new IllegalStateException("Utility classes should not be instantiated.");
    }

    /**
     * Get raw type by type.
     * Only for ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Empty optional is returned if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a type.
     */
    public static Optional<Class<?>> getOptionalRawType(Type type) {
        if (type instanceof Class) {
            return Optional.of((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return Optional.of((Class<?>) ((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            return Optional.of(((GenericArrayType) type).getClass());
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = TypeVariable.class.cast(type);
            if (Objects.nonNull(typeVariable.getBounds())) {
                Optional<Class<?>> specializedClass = Optional.empty();
                for (Type bound : typeVariable.getBounds()) {
                    Optional<Class<?>> boundRawType = getOptionalRawType(bound);
                    if (boundRawType.isPresent() && !Object.class.equals(boundRawType.get())) {
                        if (!specializedClass.isPresent() || specializedClass.get().isAssignableFrom(boundRawType.get())) {
                            specializedClass = Optional.of(boundRawType.get());
                        }
                    }
                }
                return specializedClass;
            }
        }
        return Optional.empty();
    }

    /**
     * Get raw type by type.
     * Resolves only ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Exception is thrown if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a raw type.
     */
    public static Class<?> getRawType(Type type) {
        return getOptionalRawType(type)
                .orElseThrow(() -> new JsonbException(Messages.getMessage(MessageKeys.TYPE_RESOLUTION_ERROR, type)));
    }

    /**
     * Get a raw type of any type.
     * If type is a {@link TypeVariable} recursively search type chain for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param chain hierarchy of all wrapping types.
     * @param type  type to resolve, typically field type or generic bound, not null.
     * @return resolved raw class
     */
    public static Class<?> resolveRawType(List<Type> chain, Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return getRawType(resolveType(chain, type));
        }
    }

    /**
     * Resolve a type by chain.
     * If type is a {@link TypeVariable} recursively search type chain for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param chain hierarchy of all wrapping types.
     * @param type  type to resolve, typically field type or generic bound, not null.
     * @return resolved type
     */
    public static Type resolveType(List<Type> chain, Type type) {
        return resolveType(chain, type, true);
    }

    private static Type resolveType(List<Type> chain, Type type, boolean warn) {
        Type toResolve = type;
        if (type instanceof GenericArrayType) {
            toResolve = ((GenericArrayType) type).getGenericComponentType();
            Type resolved = resolveType(chain, toResolve);
            return new GenericArrayTypeImpl(resolved);
        }
        if (toResolve instanceof WildcardType) {
            return resolveMostSpecificBound(chain, (WildcardType) toResolve, warn);
        } else if (toResolve instanceof TypeVariable) {
            return resolveItemVariableType(chain, (TypeVariable<?>) toResolve, warn);
        } else if (toResolve instanceof ParameterizedType) {
            return resolveTypeArguments((ParameterizedType) toResolve, chain.get(chain.size() - 1));
        }
        return type;
    }

    /**
     * Resolves type by item information and wraps it with {@link Optional}.
     *
     * @param chain hierarchy of all wrapping types.
     * @param type  type
     * @return resolved type wrapped with Optional
     */
    public static Optional<Type> resolveOptionalType(List<Type> chain, Type type) {
        try {
            return Optional.of(resolveType(chain, type, false));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    /**
     * Resolve a bounded type variable type by its wrapper types.
     * Resolution could be done only if a compile time generic information is provided, either:
     * by generic field or subclass of a generic class.
     *
     * @param chain        chain to search "runtime" generic type of a TypeVariable.
     * @param typeVariable type to search in chain for, not null.
     * @param warn         whether or not to log a warning message when bounds are not found
     * @return Type of a generic "runtime" bound, not null.
     */
    public static Type resolveItemVariableType(List<Type> chain, TypeVariable<?> typeVariable, boolean warn) {
//        if (chain == null) {
//        Optional<Class<?>> optionalRawType = getOptionalRawType(typeVariable);
//        if (optionalRawType.isPresent()) {
//            return optionalRawType.get();
//        }

        //            //Bound not found, treat it as an Object.class
//            if (warn) {
//                LOGGER.warning(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND,
//                                                   typeVariable,
//                                                   typeVariable.getGenericDeclaration()));
//            }
//            return Object.class;
//        }
        Type returnType = typeVariable;
        for (int i = chain.size() - 1; i >= 0; i--) {
            Type type = chain.get(i);
            Type tmp = new VariableTypeInheritanceSearch().searchParametrizedType(type, (TypeVariable<?>) returnType);
            if (tmp != null) {
                returnType = tmp;
            }
            // If the type is a WildcardType we need to resolve the most specific type
            if (returnType instanceof WildcardType) {
                return resolveMostSpecificBound(chain, (WildcardType) returnType, warn);
            }
            if (!(returnType instanceof TypeVariable)) {
                break;
            }
        }
        if (returnType instanceof TypeVariable) {
            //            throw new JsonbException("Could not resolve: " + unresolvedType);
            return Object.class;
        }
        return returnType;

//        //Embedded items doesn't hold information about variable types
//        if (chain instanceof EmbeddedItem) {
//            return resolveItemVariableType(chain.getWrapper(), typeVariable, warn);
//        }
//
//        ParameterizedType wrapperParameterizedType = findParameterizedSuperclass(chain.getRuntimeType());
//
//        VariableTypeInheritanceSearch search = new VariableTypeInheritanceSearch();
//        Type foundType = search.searchParametrizedType(wrapperParameterizedType, typeVariable);
//        if (foundType != null) {
//            if (foundType instanceof TypeVariable) {
//                return resolveItemVariableType(chain.getWrapper(), (TypeVariable<?>) foundType, warn);
//            }
//            return foundType;
//        }
//
//        return resolveItemVariableType(chain.getWrapper(), typeVariable, warn);
    }

    /**
     * Resolves {@link TypeVariable} arguments of generic types.
     *
     * @param typeToResolve type to resolve
     * @param typeToSearch  type to search
     * @return resolved type
     */
    public static Type resolveTypeArguments(ParameterizedType typeToResolve, Type typeToSearch) {
        final Type[] unresolvedArgs = typeToResolve.getActualTypeArguments();
        Type[] resolvedArgs = new Type[unresolvedArgs.length];
        for (int i = 0; i < unresolvedArgs.length; i++) {
            Type unresolvedArg = unresolvedArgs[i];
            if (!(unresolvedArg instanceof TypeVariable) && !(unresolvedArg instanceof GenericArrayType)) {
                resolvedArgs[i] = unresolvedArg;
            } else {
                Type variableType = unresolvedArg;
                if (variableType instanceof GenericArrayType) {
                    variableType = ((GenericArrayType) variableType).getGenericComponentType();
                }
                resolvedArgs[i] = new VariableTypeInheritanceSearch()
                        .searchParametrizedType(typeToSearch, (TypeVariable<?>) variableType);
                
                if (resolvedArgs[i] == null) {
                    Type[] bounds = ((TypeVariable<?>) variableType).getBounds();
                    if (Objects.nonNull(bounds) && bounds.length > 0) {
                        resolvedArgs[i] = bounds[0];
                    }
                }
                
                if (resolvedArgs[i] == null) {
                    if (typeToSearch instanceof Class) {
                        return Object.class;
                    }
                    //No generic information available
                    throw new IllegalStateException(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND,
                                                                        variableType,
                                                                        typeToSearch));
                }
            }
            if (resolvedArgs[i] instanceof ParameterizedType) {
                resolvedArgs[i] = resolveTypeArguments((ParameterizedType) resolvedArgs[i], typeToSearch);
            } else if (unresolvedArg instanceof GenericArrayType) {
                resolvedArgs[i] = new GenericArrayTypeImpl(resolvedArgs[i]);
            }
        }
        return Arrays.equals(resolvedArgs, unresolvedArgs)
                ? typeToResolve
                : new ResolvedParameterizedType(typeToResolve, resolvedArgs);
    }

    /**
     * Create instance with constructor.
     *
     * @param constructor const not null
     * @param <T>         type of instance
     * @return instance
     */
    public static <T> T createNoArgConstructorInstance(Constructor<T> constructor) {
        Objects.requireNonNull(constructor);
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonbException("Can't create instance", e);
        }
    }

    /**
     * Get default no argument constructor of the class.
     *
     * @param clazz    Class to get constructor from
     * @param <T>      Class generic type
     * @param required if true, throws an exception if the default constructor is missing.
     *                 If false, returns null in that case
     * @return the constructor of the class, or null. Depending on required.
     */
    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz, boolean required) {
        Objects.requireNonNull(clazz);
        return AccessController.doPrivileged((PrivilegedAction<Constructor<T>>) () -> {
            try {
                final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
                if (declaredConstructor.getModifiers() == Modifier.PROTECTED) {
                    declaredConstructor.setAccessible(true);
                }
                return declaredConstructor;
            } catch (NoSuchMethodException | RuntimeException e) {
                if (required) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz), e);
                }
                return null;
            }
        });
    }

    /**
     * For generic adapters like:
     * <p>
     * {@code
     * interface ContainerAdapter<T> extends JsonbAdapter<Box<T>, Crate<T>>...;
     * class IntegerBoxToCrateAdapter implements ContainerAdapter<Integer>...;
     * }
     * </p>
     * We need to find a JsonbAdapter class which will hold basic generic type arguments,
     * and resolve them if they are TypeVariables from there.
     *
     * @param classToSearch          class to resolve parameterized interface
     * @param parameterizedInterface interface to search
     * @return type of JsonbAdapter
     */
    public static ParameterizedType findParameterizedType(Class<?> classToSearch, Class<?> parameterizedInterface) {
        Class current = classToSearch;
        while (current != Object.class) {
            for (Type currentInterface : current.getGenericInterfaces()) {
                if (currentInterface instanceof ParameterizedType
                        && parameterizedInterface.isAssignableFrom(
                        ReflectionUtils.getRawType(((ParameterizedType) currentInterface).getRawType()))) {
                    return (ParameterizedType) currentInterface;
                }
            }
            current = current.getSuperclass();
        }
        throw new JsonbException(Messages.getMessage(MessageKeys.NON_PARAMETRIZED_TYPE, parameterizedInterface));
    }

    /**
     * Check if type needs resolution. If type is a class or a parametrized type with all type arguments as classes
     * than it is considered resolved. If any of types is type variable or wildcard type is not resolved.
     *
     * @param type Type to check.
     * @return True if resolved
     */
    public static boolean isResolvedType(Type type) {
        if (type instanceof ParameterizedType) {
            for (Type typeArg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (!isResolvedType(typeArg)) {
                    return false;
                }
            }
            return true;
        }
        return type instanceof Class<?>;
    }

    private static ParameterizedType findParameterizedSuperclass(Type type) {
        if (type == null || type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (!(type instanceof Class)) {
            throw new JsonbException("Can't resolve ParameterizedType superclass for: " + type);
        }
        return findParameterizedSuperclass(((Class) type).getGenericSuperclass());
    }

    /**
     * Resolves a wildcard most specific upper or lower bound.
     *
     * @param chain         Type.
     * @param wildcardType Wildcard type.
     * @return The most specific type.
     */
    private static Type resolveMostSpecificBound(List<Type> chain, WildcardType wildcardType, boolean warn) {
        Class<?> result = Object.class;
        for (Type upperBound : wildcardType.getUpperBounds()) {
            result = getMostSpecificBound(chain, result, upperBound, warn);
        }
        for (Type lowerBound : wildcardType.getLowerBounds()) {
            result = getMostSpecificBound(chain, result, lowerBound, warn);
        }
        return result;
    }

    private static Class<?> getMostSpecificBound(List<Type> chain, Class<?> result, Type bound, boolean warn) {
        if (bound == Object.class) {
            return result;
        }
        //if bound is type variable search recursively for wrapper generic expansion
        Type resolvedBoundType = bound instanceof TypeVariable ? resolveType(chain, bound, warn) : bound;
        Class<?> boundRawType = getRawType(resolvedBoundType);
        //resolved class is a subclass of a result candidate
        if (result.isAssignableFrom(boundRawType)) {
            result = boundRawType;
        }
        return result;
    }

    public static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type genericComponentType;

        // private constructor enforces use of static factory
        private GenericArrayTypeImpl(Type ct) {
            genericComponentType = ct;
        }

        /**
         * Returns a {@code Type} object representing the component type
         * of this array.
         *
         * @return a {@code Type} object representing the component type
         *     of this array
         * @since 1.5
         */
        public Type getGenericComponentType() {
            return genericComponentType; // return cached component type
        }

        public String toString() {
            return getGenericComponentType().getTypeName() + "[]";
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GenericArrayType) {
                GenericArrayType that = (GenericArrayType) o;

                return Objects.equals(genericComponentType, that.getGenericComponentType());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(genericComponentType);
        }
    }
}
