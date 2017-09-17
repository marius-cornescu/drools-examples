package com.rtzan.drools.model;

public class TestObject {
	
	private String name;
	

	public TestObject(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
