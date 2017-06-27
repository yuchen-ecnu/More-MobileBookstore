package com.cat.entity;

/**
 *    Created by 赵晨 on 2017/6/3.
 */

public class BookLendHistoryItem {

    private String bookName;


    private String operTime;
    private String operType;
    private String isbn;
    private String message;

    public BookLendHistoryItem(String bookName, String operTime,
                               String operType, String ISBN, String message) {
        super();
        this.bookName = bookName;
        this.operTime = operTime;
        this.operType = operType;
        this.isbn = ISBN;
        this.message = message;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public void setISBN(String ISBN) {
        this.isbn = ISBN;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBookName() {
        return bookName;
    }
    public String getOperTime() {
        return operTime;
    }
    public String getOperType() {
        return operType;
    }
    public String getISBN() {
        return isbn;
    }
    public String getMessage() {
        return message;
    }
    public BookLendHistoryItem() {
        super();
    }

}
