package com.rtzan.drools.model;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Receipt {
    private final Customer customer;

    private final Map<String, Integer> combo = new HashMap<>();

    public Receipt(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Map<String, Integer> getCombo() {
        return combo;
    }

    public void addCombo(String name, int price) {
        combo.put(name, price);
    }
}
