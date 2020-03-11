/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

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
