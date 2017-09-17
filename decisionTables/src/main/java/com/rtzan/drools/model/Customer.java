package com.rtzan.drools.model;

public class Customer {
	private Cart cart;
	private String coupon;
	private boolean isNew;
	
	public static Customer newCustomer() {
		Customer customer = new Customer();
		customer.isNew = true;
		return customer;
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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Customer new? ")
		   .append(isNew)
		   .append("\nCoupon: ")
		   .append(coupon)
		   .append("\n")
		   .append(cart);
		return sb.toString();
	}
}
