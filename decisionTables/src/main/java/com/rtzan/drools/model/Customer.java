package com.rtzan.drools.model;

public class Customer {

    private final String name;
    private Cart cart;
    private String coupon;
    private boolean isNew;

    public Customer(String name) {
        this.name = name;
        this.isNew = true;
    }

    public boolean getIsNew() {
        return isNew;
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
                ", isNew=" + isNew +
                '}';
    }

}
