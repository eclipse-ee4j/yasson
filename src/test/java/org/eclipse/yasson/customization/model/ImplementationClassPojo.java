/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.yasson.customization.model;

import org.eclipse.yasson.ImplementationClass;

public class ImplementationClassPojo {

    @ImplementationClass(Dog.class)
    private Animal animal;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}
