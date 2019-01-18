package org.eclipse.yasson.internal;

import org.junit.Assert;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * Contains java.base types inside collections.
 * Tests that yasson won't try to parse this types into ClassModel.
 */
public class CollectionsWithJavaBaseTypesTest {

    @Test
    public void testSimple() {
        JavaBasePropertiesInContainer properties = new JavaBasePropertiesInContainer();
        properties.setNames(Arrays.asList("First", "second", null));
        for(int i=0; i<3; i++) {
            List<Integer> integerList = new ArrayList<>();
            integerList.add(0);
            integerList.add(1);
            integerList.add(null);
            properties.setListOfListsOfIntegers(new ArrayList<>());
            properties.getListOfListsOfIntegers().add(integerList);
        }
        properties.setOptionalInts(new ArrayList<>());
        properties.getOptionalInts().add(Optional.of(Integer.MAX_VALUE));
        properties.getOptionalInts().add(Optional.empty());
        properties.setDates(new ArrayList<>());
        properties.getDates().add(LocalDate.of(2020, 1, 1));
        properties.getDates().add(null);

        Integer[][] innerArrayInts = new Integer[1][2];
        innerArrayInts[0][0] = 1;
        innerArrayInts[0][1] = null;
        properties.setInnerArrayInts(innerArrayInts);

        //Making all fields accessible. When used on JPMS will fail if java.base classes are parsed into class model.
        Jsonb jsonb = new JsonBindingBuilder().withConfig(new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return true;
            }

            @Override
            public boolean isVisible(Method method) {
                return true;
            }
        })).build();

        String expected = "{\"dates\":[\"2020-01-01\",null],\"innerArrayInts\":[[1,null]],\"listOfListsOfIntegers\":[[0,1,null]],\"names\":[\"First\",\"second\",null],\"optionalInts\":[2147483647,null]}";
        Assert.assertEquals(expected, jsonb.toJson(properties));

        JavaBasePropertiesInContainer result = jsonb.fromJson(expected, JavaBasePropertiesInContainer.class);
        Assert.assertEquals(properties.getNames(), result.getNames());
        Assert.assertEquals(properties.getOptionalInts(), result.getOptionalInts());
        Assert.assertEquals(properties.getListOfListsOfIntegers(), result.getListOfListsOfIntegers());
        Assert.assertArrayEquals(properties.getInnerArrayInts(), result.getInnerArrayInts());
    }

    public static final class JavaBasePropertiesInContainer {

        private List<String> names;

        private List<Optional<Integer>> optionalInts;

        private List<LocalDate> dates;

        public Integer[][] getInnerArrayInts() {
            return innerArrayInts;
        }

        public void setInnerArrayInts(Integer[][] innerArrayInts) {
            this.innerArrayInts = innerArrayInts;
        }

        private Integer[][] innerArrayInts;

        public List<LocalDate> getDates() {
            return dates;
        }

        public void setDates(List<LocalDate> dates) {
            this.dates = dates;
        }

        public List<Optional<Integer>> getOptionalInts() {
            return optionalInts;
        }

        public void setOptionalInts(List<Optional<Integer>> optionalInts) {
            this.optionalInts = optionalInts;
        }

        List<List<Integer>> listOfListsOfIntegers;

        public List<List<Integer>> getListOfListsOfIntegers() {
            return listOfListsOfIntegers;
        }

        public void setListOfListsOfIntegers(List<List<Integer>> listOfListsOfIntegers) {
            this.listOfListsOfIntegers = listOfListsOfIntegers;
        }

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }
    }
}
