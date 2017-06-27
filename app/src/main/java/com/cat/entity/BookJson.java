package com.cat.entity;

public class BookJson {
	private Integer bookId;
	private String isbn;
	private String describes;
	private Integer storeId;
	private Integer userId;
	private String userName;
	private String storeName;
	private String headPic;
	private String storeDescribe;
	private String author;
	private String title;
	private String publisher;
	private String bookImageURI;
	private String douBanURI;
	private String binding;
	private String price;
	private String page;
	private String rating;
	private String storeTime;
	public Integer getBookId() {
		return bookId;
	}
	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getDescribes() {
		return describes;
	}
	public void setDescribes(String describes) {
		this.describes = describes;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public String getStoreDescribe() {
		return storeDescribe;
	}
	public void setStoreDescribe(String storeDescribe) {
		this.storeDescribe = storeDescribe;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getBookImageURI() {
		return bookImageURI;
	}
	public void setBookImageURI(String bookImageURI) {
		this.bookImageURI = bookImageURI;
	}
	public String getDouBanURI() {
		return douBanURI;
	}
	public void setDouBanURI(String douBanURI) {
		this.douBanURI = douBanURI;
	}
	public String getBinding() {
		return binding;
	}
	public void setBinding(String binding) {
		this.binding = binding;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getStoreTime() {
		return storeTime;
	}
	public void setStoreTime(String storeTime) {
		this.storeTime = storeTime;
	}
	@Override
	public String toString() {
		return "BookJson [bookId=" + bookId + ", isbn=" + isbn + ", describes=" + describes + ", storeId=" + storeId
				+ ", userId=" + userId + ", userName=" + userName + ", storeName=" + storeName + ", headPic=" + headPic
				+ ", storeDescribe=" + storeDescribe + ", author=" + author + ", title=" + title + ", publisher="
				+ publisher + ", bookImageURI=" + bookImageURI + ", douBanURI=" + douBanURI + ", binding=" + binding
				+ ", price=" + price + ", page=" + page + ", rating=" + rating + ", storeTime=" + storeTime + "]";
	}
	public BookJson(Integer bookId, String isbn, String describes, Integer storeId, Integer userId, String userName,
					String storeName, String headPic, String storeDescribe, String author, String title, String publisher,
					String bookImageURI, String douBanURI, String binding, String price, String page, String rating,
					String storeTime) {
		super();
		this.bookId = bookId;
		this.isbn = isbn;
		this.describes = describes;
		this.storeId = storeId;
		this.userId = userId;
		this.userName = userName;
		this.storeName = storeName;
		this.headPic = headPic;
		this.storeDescribe = storeDescribe;
		this.author = author;
		this.title = title;
		this.publisher = publisher;
		this.bookImageURI = bookImageURI;
		this.douBanURI = douBanURI;
		this.binding = binding;
		this.price = price;
		this.page = page;
		this.rating = rating;
		this.storeTime = storeTime;
	}
	public BookJson() {
		super();
		// TODO Auto-generated constructor stub
	}



}
