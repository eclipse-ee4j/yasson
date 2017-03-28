package org.eclipse.yasson.defaultmapping;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.ScalarValueWrapper;
import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Roman Grigoriadi
 */
public class IJsonTest {

    private Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withStrictIJSON(true));;

    @Test
    public void testStrictCalendar() {
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.clear(Calendar.MILLISECOND);

        String jsonString = jsonb.toJson(new ScalarValueWrapper<>(calendar.getTime()));
        Assert.assertTrue(jsonString.matches("\\{\"value\":\"1970-01-01T00:00:00Z\\+[0-9]{2}:[0-9]{2}\"}"));

        ScalarValueWrapper<Date> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<Date>>(){}.getType());
        Assert.assertEquals(0, result.getValue().compareTo(calendar.getTime()));

    }

    @Test
    public void testStrictInstant() {
        Instant instant = LocalDateTime.of(2017, 3, 24, 12,0,0).toInstant(ZoneOffset.MIN);
        final String json = jsonb.toJson(new ScalarValueWrapper<>(instant));
        Assert.assertEquals("{\"value\":\"2017-03-25T06:00:00Z+00:00\"}", json);
        ScalarValueWrapper<Instant> result = jsonb.fromJson("{\"value\":\"2017-03-25T06:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<Instant>>() {}.getType());
        Assert.assertEquals(instant, result.getValue());
    }

    @Test
    public void testLocalDate() {
        final LocalDate localDate = LocalDate.of(1970, 1, 1);
        final String json = jsonb.toJson(new ScalarValueWrapper<>(localDate));
        Assert.assertEquals("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", json);

        ScalarValueWrapper<LocalDate> result = jsonb.fromJson("{\"value\":\"1970-01-01T00:00:00Z+00:00\"}", new TestTypeToken<ScalarValueWrapper<LocalDate>>() {
        }.getType());


        Assert.assertEquals(localDate, result.getValue());
    }

}
