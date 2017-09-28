/*
 */
package org.eclipse.yasson.internal.model.customization.visibility;

import javax.json.bind.JsonbBuilder;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class PrivateFieldVisibilityStrategyTest {

    @Test
    public void serializeIntoStringWithoutCustomStrategy() {
        String serialized = JsonbBuilder.create().
                toJson(new Message("duke"));
        System.out.println("retVal = " + serialized);
        assertThat(serialized, containsString("duke"));
    }
}
