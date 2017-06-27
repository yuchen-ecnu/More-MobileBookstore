package com.cat.entity;

public class ReservationJson {
    private String userName;
    private String bookName;
    private String phone;
    private Integer bookId;

    @Override
    public String toString() {
        return "ReservationJson [userName=" + userName + ", bookName=" + bookName + ", phone=" + phone + ", bookId="
                + bookId + "]";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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

    public ReservationJson() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ReservationJson(String userName, String bookName, String phone, Integer bookId) {
        super();
        this.userName = userName;
        this.bookName = bookName;
        this.phone = phone;
        this.bookId = bookId;
    }
}
