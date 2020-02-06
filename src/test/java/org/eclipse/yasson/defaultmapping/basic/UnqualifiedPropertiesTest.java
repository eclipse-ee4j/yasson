/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.basic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

public class UnqualifiedPropertiesTest {
	
	public static class Widget {
		
		public long now;

	    public String getFoo() {
	        return "foo";
	    }

	    public String getBar(final int baz) {
	        return "bar" + baz;
	    }
	    
	    public boolean isPositive(int num) {
	    	return num > 0;
	    }
	    
	    public Widget setNow() {
	    	now = 1511576115722L;
	    	return this;
	    }
	}
	
	@Test
	public void testGetWithArgs() {
	    assertEquals("{\"foo\":\"foo\",\"now\":0}", defaultJsonb.toJson(new Widget()));
	}
	
	@Test
	public void testSetWithNoArgs() {
		assertEquals("{\"foo\":\"foo\",\"now\":1511576115722}", defaultJsonb.toJson(new Widget().setNow()));
	}
}
