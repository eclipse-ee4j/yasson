/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.specific;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.defaultmapping.specific.model.Address;
import org.eclipse.yasson.defaultmapping.specific.model.Customer;
import org.eclipse.yasson.defaultmapping.specific.model.Street;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public abstract class CustomerTest {

    protected void assertCustomerValues(Customer customer, String customerName) {
        assertEquals(customerName, customer.getName());
        assertEquals(Integer.valueOf(33), customer.getAge());
        /*assertEquals(1, customer.getAddresses().size());
        for (Address address : customer.getAddresses()) {
            assertEquals("Prague", address.getTown());
            assertEquals("Zoubkova88", address.getStreet().getName());
            assertEquals(Integer.valueOf(111), address.getStreet().getNumber());
        }*/

        if (customer.getStrings() != null) {
            assertEquals(2, customer.getStrings().size());
            assertEquals("green", customer.getStrings().get(0));
            assertEquals("yellow", customer.getStrings().get(1));
        }

        if (customer.getIntegers() != null) {
            assertEquals(2, customer.getIntegers().size());
            assertEquals(Integer.valueOf(0),  customer.getIntegers().get(0));
            assertEquals(Integer.valueOf(1), customer.getIntegers().get(1));
        }

        /*assertEquals(2, customer.getStringIntegerMap().size());
        assertEquals(Integer.valueOf(1), customer.getStringIntegerMap().get("first"));
        assertEquals(Integer.valueOf(2), customer.getStringIntegerMap().get("second"));*/
    }

    protected static Customer createCustomer(String customerName) {
        Street street = new Street("Zoubkova", 111);
        Address address = new Address(street, "Prague");
        Customer customer = new Customer(33, customerName);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        customer.setAddresses(addresses);

        List<String> strings = new ArrayList<>();
        strings.add("green");
        strings.add("yellow");
        customer.setStrings(strings);

        List<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(1);
        customer.setIntegers(integers);

        Map<String, Integer> stringIntegerMap = new HashMap<>();
        stringIntegerMap.put("first", 1);
        stringIntegerMap.put("second", 2);
        customer.setStringIntegerMap(stringIntegerMap);

        List<List<Integer>> listOfListsOfIntegers = new ArrayList<>();
        for(int i=0; i<3; i++) {
            List<Integer> integerList = new ArrayList<>();
            integerList.add(0);
            integerList.add(1);
            integerList.add(2);
            listOfListsOfIntegers.add(integerList);
        }

        customer.setListOfListsOfIntegers(listOfListsOfIntegers);

        return customer;
    }
}
