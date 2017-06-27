package com.cat.entity;

import java.util.HashSet;
import java.util.Set;


/**
 * Bookstore entity. @author MyEclipse Persistence Tools
 */

public class Bookstore implements java.io.Serializable {

	// Fields

	private Integer storeId;
	private Address address;
	private String storeName;
	private String storeDescribe;

	private Set books = new HashSet(0);

	private Set users = new HashSet(0);

	// Constructors

	/** default constructor */
	public Bookstore() {
	}

	/** minimal constructor */
	public Bookstore(Integer storeId) {
		this.storeId = storeId;
	}

	/** full constructor */
	public Bookstore(Integer storeId, Address address, String storeName,
			String storeDescribe, Set books, Set users) {
		this.storeId = storeId;
		this.address = address;
		this.storeName = storeName;
		this.storeDescribe = storeDescribe;
		this.books = books;
		this.users = users;
	}

	// Property accessors

	public Integer getStoreId() {
		return this.storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getStoreName() {
		return this.storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreDescribe() {
		return this.storeDescribe;
	}

	public void setStoreDescribe(String storeDescribe) {
		this.storeDescribe = storeDescribe;
	}

	public Set getBooks() {
		return this.books;
	}

	public void setBooks(Set books) {
		this.books = books;
	}

	public Set getUsers() {
		return this.users;
	}

	public void setUsers(Set users) {
		this.users = users;
	}

}