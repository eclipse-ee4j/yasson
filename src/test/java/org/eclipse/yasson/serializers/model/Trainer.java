/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Tomas Kraus
 ******************************************************************************/

package org.eclipse.yasson.serializers.model;

import java.util.Objects;

/**
 * Serialization and de-serialization test model: pokemon {@code Trainer}.
 */
public final class Trainer {
    public String name;
    public int age;

    public Trainer() {
        this.name = null;
        this.age = -1;
    }

    public Trainer(String name, int age) {
        this.name = name;
        this.age = age;
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
	        return true;
	    }
	    if (!(other instanceof Trainer)) {
	        return false;
	    }
	    Trainer trainer = (Trainer)other;
	    return age == trainer.age && Objects.equals(name, trainer.name);
	}

    @Override
    public int hashCode() {
        return Objects.hash(age, name);
    }

}
