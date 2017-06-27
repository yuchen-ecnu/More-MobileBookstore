package com.cat.entity;

import java.util.Date;

/**
 * Badbook entity. @author MyEclipse Persistence Tools
 */

public class Badbook implements java.io.Serializable {

	// Fields

	private Integer badId;
	private Book book;
	private User user;
	private Date time;

	// Constructors

	/** default constructor */
	public Badbook() {
	}

	/** minimal constructor */
	public Badbook(Integer badId) {
		this.badId = badId;
	}

	/** full constructor */
	public Badbook(Integer badId, Book book, User user, Date time) {
		this.badId = badId;
		this.book = book;
		this.user = user;
		this.time = time;
	}

	// Property accessors

	public Integer getBadId() {
		return this.badId;
	}

	public void setBadId(Integer badId) {
		this.badId = badId;
	}

	public Book getBook() {
		return this.book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}