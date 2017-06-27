package com.cat.entity;

import java.util.HashSet;
import java.util.Set;


/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User implements java.io.Serializable {

	// Fields

	private Integer userId;
	private Bookstore bookstore;
	private String userName;
	private String phone;
	private String password;
	private String gender;
	private Integer score;
	private String headPic;

	private Set badbooks = new HashSet(0);

	private Set books = new HashSet(0);

	private Set histories = new HashSet(0);

	// Constructors

	/** default constructor */
	public User() {
	}

	/** full constructor */
	public User(Bookstore bookstore, String userName, String phone,
			String password, String gender, Integer score, String headPic,
			Set badbooks, Set books, Set histories) {
		this.bookstore = bookstore;
		this.userName = userName;
		this.phone = phone;
		this.password = password;
		this.gender = gender;
		this.score = score;
		this.headPic = headPic;
		this.badbooks = badbooks;
		this.books = books;
		this.histories = histories;
	}

	// Property accessors

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Bookstore getBookstore() {
		return this.bookstore;
	}

	public void setBookstore(Bookstore bookstore) {
		this.bookstore = bookstore;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getHeadPic() {
		return this.headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public Set getBadbooks() {
		return this.badbooks;
	}

	public void setBadbooks(Set badbooks) {
		this.badbooks = badbooks;
	}

	public Set getBooks() {
		return this.books;
	}

	public void setBooks(Set books) {
		this.books = books;
	}

	public Set getHistories() {
		return this.histories;
	}

	public void setHistories(Set histories) {
		this.histories = histories;
	}

}