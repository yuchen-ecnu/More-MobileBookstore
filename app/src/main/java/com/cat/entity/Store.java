package com.cat.entity;

/**
 * Store entity. @author MyEclipse Persistence Tools
 */

public class Store implements java.io.Serializable {

	// Fields

	private StoreId id;

	// Constructors

	/** default constructor */
	public Store() {
	}

	/** full constructor */
	public Store(StoreId id) {
		this.id = id;
	}

	// Property accessors

	public StoreId getId() {
		return this.id;
	}

	public void setId(StoreId id) {
		this.id = id;
	}

}