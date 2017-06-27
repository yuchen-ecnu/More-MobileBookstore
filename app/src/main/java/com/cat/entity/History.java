package com.cat.entity;

import java.util.Date;

/**
 * History entity. @author MyEclipse Persistence Tools
 */

public class History implements java.io.Serializable {

	// Fields

	private Integer historyId;
	private Book book;
	private User user;
	private Date time;

	// Constructors

	/** default constructor */
	public History() {
	}

	/** minimal constructor */
	public History(Integer historyId) {
		this.historyId = historyId;
	}

	/** full constructor */
	public History(Integer historyId, Book book, User user, Date time) {
		this.historyId = historyId;
		this.book = book;
		this.user = user;
		this.time = time;
	}

	// Property accessors

	public Integer getHistoryId() {
		return this.historyId;
	}

	public void setHistoryId(Integer historyId) {
		this.historyId = historyId;
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