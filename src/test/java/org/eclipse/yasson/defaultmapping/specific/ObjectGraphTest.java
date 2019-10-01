/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.specific;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.defaultmapping.specific.model.Customer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class ObjectGraphTest extends CustomerTest {
    private static final String EXPECTED = "{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"friends\":{\"firstFriend\":{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Jasons first friend\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]},\"secondFriend\":{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Jasons second friend\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]}},\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Root Jason Customer\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]}";

    @Test
    public void testObjectToJson() {
        Customer customer = createCustomer("Root Jason Customer");

        Map<String, Customer> friends = new HashMap<>();
        friends.put("firstFriend", createCustomer("Jasons first friend"));
        friends.put("secondFriend", createCustomer("Jasons second friend"));
        customer.setFriends(friends);

        assertEquals(EXPECTED, bindingJsonb.toJson(customer));
    }

    @Test
    public void testObjectFromJson() {
        Customer customer = bindingJsonb.fromJson(EXPECTED, Customer.class);
        assertCustomerValues(customer, "Root Jason Customer");
        assertEquals(2, customer.getFriends().size());
        assertCustomerValues(customer.getFriends().get("firstFriend"), "Jasons first friend");
        assertCustomerValues(customer.getFriends().get("secondFriend"), "Jasons second friend");
    }
}
