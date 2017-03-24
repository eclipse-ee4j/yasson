package org.eclipse.yasson.defaultmapping;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Assert;
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

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1970, 0, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendar));
        Assert.assertEquals("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", jsonString);

        ScalarValueWrapper<Calendar> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", new TestTypeToken<ScalarValueWrapper<Calendar>>() {}.getType());

        Assert.assertEquals(calendar.toInstant(), result.getValue().toInstant());
    }

    @Test
    public void testStrictDate() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withStrictIJSON(true));

        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 0, 0, 0);
        calendar.clear(Calendar.MILLISECOND);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendar.getTime()));
        Assert.assertTrue(jsonString.matches("\\{\"value\":\"1970-01-01T00:00:00Z\\+[0-9]{2}:[0-9]{2}\"}"));

        ScalarValueWrapper<Date> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+01:00\"}", new TestTypeToken<ScalarValueWrapper<Date>>(){}.getType());
        Assert.assertEquals(0, result.getValue().compareTo(calendar.getTime()));

    }
}
