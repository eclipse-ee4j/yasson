/*******************************************************************************
 * Copyright (c) 2019 Payara Services and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  Payara Services - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.serializers.model;

/**
 * @author Matt Gill
 */
public class RecursiveObject {

    public Integer id;

    public RecursiveObject child;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((child == null) ? 0 : child.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecursiveObject other = (RecursiveObject) obj;
        if (child == null) {
            if (other.child != null)
                return false;
        } else if (!child.equals(other.child))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static RecursiveObject construct(int depth) {
        RecursiveObject object = new RecursiveObject();
        RecursiveObject parent = object;
        for (int i = 0; i < depth; i++) {
            parent.child = new RecursiveObject();
            parent = parent.child;
        }
        parent.id = 1;
        return object;
    }
}
