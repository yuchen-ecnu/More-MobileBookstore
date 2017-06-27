package com.cat.entity;

public class SearchBookJson {
	private Integer userId;
	private String userName;
	private String phone;
	private Integer bookId;
	private String image;
	private String title;
	private Double lat;
	private Double lon;
	private String author;
	private String headpic;
	private String storeid;

	public String getStoreid() {
		return storeid;
	}

	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}

	public String getHeadpic() {
		return headpic;
	}

	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getBookId() {
		return bookId;
	}
	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}

	public SearchBookJson(Integer userId, String userName, String phone, Integer bookId, String image, String title, Double lat, Double lon, String author, String headpic, String storeid) {
		this.userId = userId;
		this.userName = userName;
		this.phone = phone;
		this.bookId = bookId;
		this.image = image;
		this.title = title;
		this.lat = lat;
		this.lon = lon;
		this.author = author;
		this.headpic = headpic;
		this.storeid = storeid;
	}

	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public SearchBookJson() {
		super();
	}
	
}
