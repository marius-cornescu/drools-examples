package com.rtzan.drools.model;

public class Product {
	private int price;
	private String name;
	private Customer customer;
	
	public Product(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String toString() {
		return "product: " + name + ", price: " + price;
	}
}
