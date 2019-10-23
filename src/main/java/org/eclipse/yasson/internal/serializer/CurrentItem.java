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
 * David Kral
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;

import org.eclipse.yasson.internal.RuntimeTypeInfo;
import org.eclipse.yasson.internal.model.ClassModel;

/**
 * Currently processing item.
 *
 * @param <T> item type
 */
public interface CurrentItem<T> extends RuntimeTypeInfo {

    /**
     * Class model containing property for this item.
     *
     * @return Class model.
     */
    ClassModel getClassModel();

    /**
     * Item wrapper. Null only in case of a root item.
     *
     * @return Wrapper item of this item.
     */
    CurrentItem<?> getWrapper();

}
