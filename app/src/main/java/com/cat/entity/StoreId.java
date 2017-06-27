package com.cat.entity;

/**
 * StoreId entity. @author MyEclipse Persistence Tools
 */

public class StoreId implements java.io.Serializable {

	// Fields

	private Book book;
	private Bookstore bookstore;

	// Constructors

	/** default constructor */
	public StoreId() {
	}

	/** full constructor */
	public StoreId(Book book, Bookstore bookstore) {
		this.book = book;
		this.bookstore = bookstore;
	}

	// Property accessors

	public Book getBook() {
		return this.book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Bookstore getBookstore() {
		return this.bookstore;
	}

	public void setBookstore(Bookstore bookstore) {
		this.bookstore = bookstore;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof StoreId))
			return false;
		StoreId castOther = (StoreId) other;

		return ((this.getBook() == castOther.getBook()) || (this.getBook() != null && castOther.getBook() != null
				&& this.getBook().equals(castOther.getBook())))
				&& ((this.getBookstore() == castOther.getBookstore()) || (this.getBookstore() != null
						&& castOther.getBookstore() != null && this.getBookstore().equals(castOther.getBookstore())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getBook() == null ? 0 : this.getBook().hashCode());
		result = 37 * result + (getBookstore() == null ? 0 : this.getBookstore().hashCode());
		return result;
	}

}