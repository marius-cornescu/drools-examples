/** Free */
package com.rtzan.drools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.rtzan.drools.model.Customer;


public class CustomerEventListener extends DefaultEventListener {

    private List<Customer> customerList = new ArrayList<>();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public final List<Customer> getCustomerList() {
        return new ArrayList<>(customerList);
    }

    @Override
    public void reset() {
        super.reset();
        customerList.clear();
    }

    protected void afterActivationFired(String ruleName, List<Object> objects) {
        customerList.addAll(objects.stream().map(o -> (Customer) o).collect(Collectors.toList()));
    }
}
