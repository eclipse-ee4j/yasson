/*
 * Copyright (c) 2020 IBM and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

public class PathTypeDeserializer extends AbstractValueTypeDeserializer<Path> {
    public PathTypeDeserializer(Customization customization) {
        super(Path.class, customization);
    }
    
    @Override
    protected Path deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        return Paths.get(jsonValue);
    }
}