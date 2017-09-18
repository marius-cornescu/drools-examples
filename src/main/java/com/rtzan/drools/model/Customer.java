package com.rtzan.drools.model;

public class Customer {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final String name;
    private Cart cart;
    private String coupon;
    private boolean flagged;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Customer(String name) {
        this.name = name;
        this.flagged = true;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public void addItem(Product product, int qty) {
        if (cart == null) {
            cart = new Cart(this);
        }
        cart.addItem(product, qty);
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public Cart getCart() {
        return cart;
    }

    @Override
    public String toString() {
        return "Customer{" +
            "name='" + name + '\'' +
            ", cart=" + cart +
            ", coupon='" + coupon + '\'' +
            ", flagged=" + flagged + '}';
    }

}
