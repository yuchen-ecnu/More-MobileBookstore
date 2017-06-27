package com.cat.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 雨晨 on 21/06/2016.
 */
public class Travel implements Parcelable{
    String name;//书名
    String image;//封面图
    String isbn;//isbn
    String author;//作者
    String bookStoreMessage;//书架描述
    String bookStoreUserID;//该书所有者ID
    String headPic;//用户头像路径URI
    String rating;//书评分
    List<String> tags;//tag
    String pages;//页数
    String binds;//装帧
    String publisher;//出版社
    String doubanURL;//豆瓣书评
    String price;//价格
    String storeID;//书架号
    String userName;//用户名
    String bookid;//书id

    public Travel(String name, String image, String isbn, String author, String bookStoreMessage, String bookStoreUserID, String headPic, String rating, List<String> tags, String pages, String binds, String publisher, String doubanURL, String price, String storeID, String userName, String bookid) {
        this.name = name;
        this.image = image;
        this.isbn = isbn;
        this.author = author;
        this.bookStoreMessage = bookStoreMessage;
        this.bookStoreUserID = bookStoreUserID;
        this.headPic = headPic;
        this.rating = rating;
        this.tags = tags;
        this.pages = pages;
        this.binds = binds;
        this.publisher = publisher;
        this.doubanURL = doubanURL;
        this.price = price;
        this.storeID = storeID;
        this.userName = userName;
        this.bookid = bookid;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getBinds() {
        return binds;
    }

    public void setBinds(String binds) {
        this.binds = binds;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDoubanURL() {
        return doubanURL;
    }

    public void setDoubanURL(String doubanURL) {
        this.doubanURL = doubanURL;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookStoreMessage() {
        return bookStoreMessage;
    }

    public void setBookStoreMessage(String bookStoreMessage) {
        this.bookStoreMessage = bookStoreMessage;
    }

    public String getBookStoreUserID() {
        return bookStoreUserID;
    }

    public void setBookStoreUserID(String bookStoreUserID) {
        this.bookStoreUserID = bookStoreUserID;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    protected Travel(Parcel in) {
        name = in.readString();
        image = in.readString();
        isbn= in.readString();
        author= in.readString();
        bookStoreMessage= in.readString();
        bookStoreUserID= in.readString();
        headPic= in.readString();
        rating= in.readString();
        tags = in.readArrayList(List.class.getClassLoader());
        pages= in.readString();
        binds= in.readString();
        publisher= in.readString();
        doubanURL= in.readString();
        price= in.readString();
        storeID = in.readString();
        userName = in.readString();
        bookid = in.readString();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(isbn);
        dest.writeString(author);
        dest.writeString(bookStoreMessage);
        dest.writeString(bookStoreUserID);
        dest.writeString(headPic);
        dest.writeString(rating);
        dest.writeList(tags);
        dest.writeString(pages);
        dest.writeString(binds);
        dest.writeString(publisher);
        dest.writeString(doubanURL);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(storeID);
        dest.writeString(userName);
        dest.writeString(bookid);
    }

    public Travel() {
    }

}
