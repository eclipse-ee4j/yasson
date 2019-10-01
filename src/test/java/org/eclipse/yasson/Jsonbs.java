package org.eclipse.yasson;

import javax.json.bind.*;
import org.eclipse.yasson.internal.*;

public class Jsonbs {
	public static final Jsonb defaultJsonb = JsonbBuilder.create();
	public static final Jsonb bindingJsonb = new JsonBindingBuilder().build();
}