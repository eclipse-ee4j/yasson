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
		if (other instanceof Pokemon) {
			return this.cp == ((Pokemon) other).cp
					&& ((this.name == null && ((Pokemon) other).name == null) || (this.name != null
							&& ((Pokemon) other).name != null && this.name.equals(((Pokemon) other).name)))
					&& ((this.type == null && ((Pokemon) other).type == null) || (this.type != null
							&& ((Pokemon) other).type != null && this.type.equals(((Pokemon) other).type)));

		}
	return false;
    }

	public boolean equals(String name, String type, int cp) {
		return this.cp == cp
				&& ((this.name == null && name == null)
						|| (this.name != null && name != null && this.name.equals(name)))
				&& ((this.type == null && type == null)
						|| (this.type != null && type != null && this.type.equals(type)));
	}

    @Override
    public int hashCode() {
	return 71 * cp + 37 * (name != null ? name.hashCode() : 0) + 17 * (type != null ? type.hashCode() : 0);
    }

}
