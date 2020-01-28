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
 * Serialization and de-serialization test model: {@code Pokemon}.
 */
public final class Pokemon {
	public String name;
	public String type;
	public int cp;

	public Pokemon() {
		this.name = null;
		this.type = null;
		this.cp = -1;
	}

	public Pokemon(String name, String type, int cp) {
		this.name = name;
		this.type = type;
		this.cp = cp;
	}

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Pokemon)) {
            return false;
        }
        Pokemon pokemon = (Pokemon)other;
        return cp == pokemon.cp
                && Objects.equals(name, pokemon.name)
                && Objects.equals(type, pokemon.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cp, name, type);
    }

}
