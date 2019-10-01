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
 * Andrew Guibert
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.basic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.json.bind.JsonbBuilder;

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
	    assertEquals("{\"foo\":\"foo\",\"now\":0}", JsonbBuilder.create().toJson(new Widget()));
	}
	
	@Test
	public void testSetWithNoArgs() {
		assertEquals("{\"foo\":\"foo\",\"now\":1511576115722}", JsonbBuilder.create().toJson(new Widget().setNow()));
	}
	
}
