package org.eclipse.yasson.defaultmapping;

import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Roman Grigoriadi
 */
public class IJsonTest {

    @Test
    public void testStrictCalendar() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withStrictIJSON(true));

        Calendar calendarProperty = Calendar.getInstance();
        calendarProperty.set(1970, 0, 1, 0, 0, 0);
        calendarProperty.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        final ScalarValueWrapper<Calendar> calendarScalarValueWrapper = new ScalarValueWrapper<>(calendarProperty);
        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendarProperty));
        Assert.assertEquals("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", jsonString);
    }

    @Test
    @Ignore
    public void testStrictDate() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withStrictIJSON(true));

        String jsonString = jsonb.toJson(new ScalarValueWrapper<Date>(new Date(1990, 0, 1, 0, 0, 0)));
        Assert.assertEquals("{\"value\":\"1970-01-01T11:06:04Z+01:00\"}", jsonString);
    }
}
