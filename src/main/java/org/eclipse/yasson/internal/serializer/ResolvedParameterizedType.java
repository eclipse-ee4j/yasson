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

package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link ParameterizedType} implementation containing array of resolved TypeVariable type args.
 */
public class ResolvedParameterizedType implements ParameterizedType {

    /**
     * Original parameterized type.
     */
    private final ParameterizedType original;

    /**
     * Resolved args by runtime type.
     */
    private final Type[] resolvedTypeArgs;

    /**
     * Creates a new instance.
     *
     * @param original         Original type.
     * @param resolvedTypeArgs Resolved type arguments.
     */
    public ResolvedParameterizedType(ParameterizedType original, Type[] resolvedTypeArgs) {
        this.original = original;
        this.resolvedTypeArgs = resolvedTypeArgs;
    }

    /**
     * Type arguments with resolved TypeVariables.
     *
     * @return type args
     */
    @Override
    public Type[] getActualTypeArguments() {
        return resolvedTypeArgs;
    }

    @Override
    public Type getRawType() {
        return original.getRawType();
    }

    @Override
    public Type getOwnerType() {
        return original.getOwnerType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(original.toString());
        if (resolvedTypeArgs != null && resolvedTypeArgs.length > 0) {
            sb.append(" resolved arguments: [");
            for (Type typeArg : resolvedTypeArgs) {
                sb.append(String.valueOf(typeArg));
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType that = (ParameterizedType) o;
        return this.getRawType().equals(that.getRawType())
                && Objects.equals(this.getOwnerType(), that.getOwnerType())
                && Arrays.equals(resolvedTypeArgs, that.getActualTypeArguments());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(resolvedTypeArgs)
                ^ (getOwnerType() == null ? 0 : getOwnerType().hashCode())
                ^ (getRawType() == null ? 0 : getRawType().hashCode());
    }
}
