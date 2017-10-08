/** Free */
package com.rtzan.drools;

import com.rtzan.drools.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ProductEventListener extends DefaultEventListener {

    private List<Product> products = new ArrayList<>();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public final List<Product> getProductList() {
        return new ArrayList<>(products);
    }

    @Override
    public void reset() {
        super.reset();
        products.clear();
    }

    protected void afterActivationFired(String ruleName, List<Object> objects) {
        if (objects == null || objects.isEmpty() || !(objects.get(0) instanceof Product)) {
            return;
        }
        //products.addAll(objects.stream().map(o -> (Product) o).collect(Collectors.toList()));
    }
}
