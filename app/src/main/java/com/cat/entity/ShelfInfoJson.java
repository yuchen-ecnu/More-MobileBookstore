package com.cat.entity;

public class ShelfInfoJson implements java.io.Serializable{
	private String shelfName;
	private String phone;
	private String imageURI;
	private String shelfDescribe;
	public String getShelfName() {
		return shelfName;
	}
	public void setShelfName(String shelfName) {
		this.shelfName = shelfName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImageURI() {
		return imageURI;
	}
	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}
	public String getShelfDescribe() {
		return shelfDescribe;
	}
	public void setShelfDescribe(String shelfDescribe) {
		this.shelfDescribe = shelfDescribe;
	}
	@Override
	public String toString() {
		return "ShelfInfoJson [shelfName=" + shelfName + ", phone=" + phone + ", imageURI=" + imageURI
				+ ", shelfDescribe=" + shelfDescribe + "]";
	}
	public ShelfInfoJson(String shelfName, String phone, String imageURI, String shelfDescribe) {
		super();
		this.shelfName = shelfName;
		this.phone = phone;
		this.imageURI = imageURI;
		this.shelfDescribe = shelfDescribe;
	}
	public ShelfInfoJson() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
