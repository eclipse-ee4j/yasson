/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.collections;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.generics.model.Circle;

import java.math.BigDecimal;
import java.util.*;

/**
 * Default mapping arrays/collections/enums tests.
 *
 * @author Dmitry Kornilov
 */
public class CollectionsTest {

    @Test
    public void testMarshallCollection() {
        final Collection<Integer> collection = Arrays.asList(1, 2, 3);
        assertEquals("[1,2,3]", nullableJsonb.toJson(collection));
    }

    @Test
    public void testMarshallMap() {
        Map<String, Integer> stringIntegerMap = new LinkedHashMap<>();
        stringIntegerMap.put("1",1);
        stringIntegerMap.put("2",2);
        stringIntegerMap.put("3",3);

        assertEquals("{\"1\":1,\"2\":2,\"3\":3}", nullableJsonb.toJson(stringIntegerMap));
        assertEquals(stringIntegerMap, nullableJsonb.fromJson("{\"1\":1,\"2\":2,\"3\":3}", new LinkedHashMap<String, Integer>(){}.getClass().getGenericSuperclass()));
    }

    @Test
    public void testMarshallMapWithNulls() {
        Map<String, String> mapWithNulls = new LinkedHashMap<>();
        mapWithNulls.put("key1",null);
        mapWithNulls.put("key2",null);
        mapWithNulls.put("key3",null);

        assertEquals("{\"key1\":null,\"key2\":null,\"key3\":null}", nullableJsonb.toJson(mapWithNulls));
    }

    @Test
    public void testListOfNumbers() {
        List<Number> numberList = new ArrayList<>();
        numberList.add(1L);
        numberList.add(2f);
        numberList.add(10);

        String result = nullableJsonb.toJson(numberList, new TestTypeToken<List<Number>>(){}.getType());
    }

    @Test
    public void testListOfListsOfStrings() {
        List<List<String>> listOfListsOfStrings = new ArrayList<>();
        
        for(int i = 0; i < 10; i++) {
            List<String> stringList = new ArrayList<>();
            stringList.add("first");
            stringList.add("second");
            stringList.add("third");
            listOfListsOfStrings.add(stringList);
        }
        
        final String expected = "[[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"],[\"first\",\"second\",\"third\"]]";
        assertEquals(expected, nullableJsonb.toJson(listOfListsOfStrings));
        assertEquals(listOfListsOfStrings, nullableJsonb.fromJson(expected, new ArrayList<List<String>>(){}.getClass().getGenericSuperclass()));
    }

    @Test
    public void listOfMapsOfListsOfMaps() {
        List<Map<String, List<Map<String, Integer>>>> listOfMapsOfListsOfMaps = new ArrayList<>();
        
        for(int i = 0; i < 3; i++) {
            Map<String, List<Map<String, Integer>>> mapOfListsOfMap = new HashMap<>();
            
            for(int j = 0; j < 3; j++) {
                List<Map<String, Integer>> listOfMaps = new ArrayList<>();
                
                for(int k = 0; k < 3; k++) {
                    Map<String, Integer> stringIntegerMap = new HashMap<>();
                    stringIntegerMap.put("first", 1);
                    stringIntegerMap.put("second", 2);
                    stringIntegerMap.put("third", 3);
                    listOfMaps.add(stringIntegerMap);
                }
                mapOfListsOfMap.put(String.valueOf(j), listOfMaps);
            }
            listOfMapsOfListsOfMaps.add(mapOfListsOfMap);
        }
        
        String expected = "[{\"0\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"1\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"2\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}]},{\"0\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"1\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"2\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}]},{\"0\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"1\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}],\"2\":[{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2},{\"third\":3,\"first\":1,\"second\":2}]}]";
        assertEquals(expected, nullableJsonb.toJson(listOfMapsOfListsOfMaps));
        ArrayList<Map<String, List<Map<String, Integer>>>> result = nullableJsonb.fromJson(expected, new TestTypeToken<ArrayList<Map<String, List<Map<String, Integer>>>>>(){}.getType());
        assertEquals(listOfMapsOfListsOfMaps, result);
    }

    @Test
    public void testAnyCollection() {
        final Deque<String> deque = new ArrayDeque<>();
        deque.add("dequeueFirst");
        deque.add("dequeueSecond");
        String expected = "[\"dequeueFirst\",\"dequeueSecond\"]";
        assertEquals(expected, nullableJsonb.toJson(deque));
        Deque<String> dequeueResult = nullableJsonb.fromJson(expected, new TestTypeToken<ArrayDeque<String>>(){}.getType());
        assertEquals("dequeueFirst", dequeueResult.getFirst());
        assertEquals("dequeueSecond", dequeueResult.getLast());

        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("setFirst");
        linkedHashSet.add("setSecond");
        expected = "[\"setFirst\",\"setSecond\"]";
        assertEquals(expected, nullableJsonb.toJson(linkedHashSet));
        LinkedHashSet<String> linkedHashSetResult = nullableJsonb.fromJson(expected, new TestTypeToken<LinkedHashSet<String>>(){}.getType());
        Iterator<String> iterator = linkedHashSetResult.iterator();
        assertEquals("setFirst", iterator.next());
        assertEquals("setSecond", iterator.next());

        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("listFirst");
        linkedList.add("listSecond");
        expected = "[\"listFirst\",\"listSecond\"]";
        assertEquals(expected, nullableJsonb.toJson(linkedList));
        LinkedList<String> linkedListResult = nullableJsonb.fromJson(expected, new TestTypeToken<LinkedList<String>>(){}.getType());
        iterator = linkedListResult.iterator();
        assertEquals("listFirst", iterator.next());
        assertEquals("listSecond", iterator.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallArray() {
        //support of arrays of types that JSON Binding is able to serialize
        //Byte[], Short[], Integer[] Long[], Float[], Double[], BigInteger[], BigDecimal[], Number[]
        //Object[], JsonArray[], JsonObject[], JsonStructure[]
        //String[], Character[]
        //byte[], short[], int[], long[], float[], double[], char[], boolean[]
        //java.net.URL[], java.net.URI[]
        //Map[], Collection[], other collections ...
        //enum, EnumSet, EnumMap
        //support of multidimensional arrays

        final Byte[] byteArray = {1, 2, 3};
        assertEquals("[1,2,3]", nullableJsonb.toJson(byteArray));

        final Integer[] integerArray = {1, 2, 3};
        assertEquals("[1,2,3]", nullableJsonb.toJson(integerArray));

        final String[] stringArray = {"first", "second", "third"};
        assertEquals("[\"first\",\"second\",\"third\"]", nullableJsonb.toJson(stringArray));

        Character[] charArr = {'a', 'b', 'c'};
        assertEquals("[\"a\",\"b\",\"c\"]", nullableJsonb.toJson(charArr));

        final byte[] bytePrimitivesArray = {1, 2, 3};
        assertEquals("[1,2,3]", nullableJsonb.toJson(bytePrimitivesArray));

        final int[] intArray = {1, 2, 3};
        assertEquals("[1,2,3]", nullableJsonb.toJson(intArray));

        final String[][] stringMultiArray = {{"first", "second"},{"third", "fourth"}};
        assertEquals("[[\"first\",\"second\"],[\"third\",\"fourth\"]]", nullableJsonb.toJson(stringMultiArray));

        final Map<String, Object>[][] mapMultiArray = new LinkedHashMap[2][2];
        mapMultiArray[0][0] = new LinkedHashMap<>(1);
        mapMultiArray[0][0].put("0", 0);
        mapMultiArray[0][1] = new LinkedHashMap<>(1);
        mapMultiArray[0][1].put("0", 1);
        mapMultiArray[1][0] = new LinkedHashMap<>(1);
        mapMultiArray[1][0].put("1", 0);
        mapMultiArray[1][1] = new LinkedHashMap<>(1);
        mapMultiArray[1][1].put("1", 1);

        assertEquals("[[{\"0\":0},{\"0\":1}],[{\"1\":0},{\"1\":1}]]", nullableJsonb.toJson(mapMultiArray));
    }

    @Test
    public void testMarshallEnumSet() {
        final EnumSet<Language> languageEnumSet = EnumSet.of(Language.Czech, Language.Slovak);

        final String result = nullableJsonb.toJson(languageEnumSet);
        assertTrue("[\"Czech\",\"Slovak\"]".equals(result) || "[\"Slovak\",\"Czech\"]".equals(result));
        assertEquals(languageEnumSet, nullableJsonb.fromJson(result, new TestTypeToken<EnumSet<Language>>() {}.getType()));
    }

    @Test
    public void testMarshallEnumMap() {
        final EnumMap<Language, String> languageEnumMap = new EnumMap<>(Language.class);
        languageEnumMap.put(Language.Russian, "ru");
        languageEnumMap.put(Language.English, "en");

        final String result = nullableJsonb.toJson(languageEnumMap);
        assertTrue("{\"Russian\":\"ru\",\"English\":\"en\"}".equals(result) ||
                "{\"English\":\"en\",\"Russian\":\"ru\"}".equals(result));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRawCollection() {
        List rawList = new ArrayList();
        rawList.add("first");
        Circle circle = new Circle();
        circle.setRadius(2.0);
        circle.setArea(1.0);
        rawList.add(circle);

        String expected = "[\"first\",{\"area\":1.0,\"radius\":2.0}]";
        assertEquals(expected, nullableJsonb.toJson(rawList));
        List result = nullableJsonb.fromJson(expected, List.class);
        assertEquals("first", result.get(0));
        assertEquals(new BigDecimal("2.0"), ((Map)result.get(1)).get("radius"));
        assertEquals(new BigDecimal("1.0"), ((Map)result.get(1)).get("area"));
    }

    @Test
    public void testNavigableMap() {
        NavigableMap<String, String> map = new TreeMap<>();
        map.put("first", "abc");
        map.put("second", "def");
        final String json = nullableJsonb.toJson(map);
        assertEquals("{\"first\":\"abc\",\"second\":\"def\"}", json);

        NavigableMap<String, String> result = nullableJsonb.fromJson(json, new TestTypeToken<NavigableMap<String, String>>() {}.getType());
        assertEquals(TreeMap.class, result.getClass());
        assertEquals("abc", result.get("first"));
        assertEquals("def", result.get("second"));
    }

    @Test
    public void testSortedMap() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("first", "abc");
        map.put("second", "def");
        final String json = nullableJsonb.toJson(map);
        assertEquals("{\"first\":\"abc\",\"second\":\"def\"}", json);

        SortedMap<String, String> result = nullableJsonb.fromJson(json, new TestTypeToken<SortedMap<String, String>>() {}.getType());
        assertEquals(TreeMap.class, result.getClass());
        assertEquals("abc", result.get("first"));
        assertEquals("def", result.get("second"));
    }
}
