package org.eclipse.yasson;

import javax.json.bind.*;
import org.eclipse.yasson.internal.*;

public class Jsonbs {
	public static final Jsonb defaultJsonb = JsonbBuilder.create();
	public static final Jsonb bindingJsonb = new JsonBindingBuilder().build();
	public static final Jsonb formattingJsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(Boolean.TRUE));
	public static final Jsonb nullableJsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(Boolean.TRUE));
    public static final YassonJsonb yassonJsonb = (YassonJsonb) JsonbBuilder.create();
	public static final YassonJsonb bindingYassonJsonb = (YassonJsonb) new JsonBindingProvider().create().build();
}