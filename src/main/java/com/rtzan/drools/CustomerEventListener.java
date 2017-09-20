package com.rtzan.drools;

import java.util.List;

import com.rtzan.drools.model.Customer;


public interface CustomerEventListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    List<Customer> getCustomerList();

    void reset();
}
