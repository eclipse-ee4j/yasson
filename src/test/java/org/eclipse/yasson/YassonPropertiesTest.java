package org.eclipse.yasson;

import org.junit.Test;

import static org.eclipse.yasson.YassonProperties.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.eclipse.yasson.YassonProperties.NULL_ROOT_SERIALIZER;
import static org.eclipse.yasson.YassonProperties.USER_TYPE_MAPPING;
import static org.eclipse.yasson.YassonProperties.ZERO_TIME_PARSE_DEFAULTING;
import static org.junit.Assert.assertEquals;

/**
 * Tests that the names of configuration fields in {@link YassonProperties} do not change.
 *
 * @author Simulant (nfaupel.dev@gmail.com)
 */
public class YassonPropertiesTest {

    @Test
    public void testFailOnUnknownProperties() {
        assertEquals("jsonb.fail-on-unknown-properties", FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void testUserTypeMapping() {
        assertEquals("jsonb.user-type-mapping", USER_TYPE_MAPPING);
    }

    @Test
    public void testZeroTimeDefaulting() {
        assertEquals("jsonb.zero-time-defaulting", ZERO_TIME_PARSE_DEFAULTING);
    }

    @Test
    public void testNullRootSerializer() {
        assertEquals("yasson.null-root-serializer", NULL_ROOT_SERIALIZER);
    }
}
