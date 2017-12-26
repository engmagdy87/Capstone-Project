package com.mm.plume.javaclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MM on 12/24/2017.
 */

public class BookInfo implements Parcelable {
    private String id;
    private String title;
    private String[] authors;
    private String publisher;
    private String publishedDate;
    private String isbn;
    private String description;
    private String[] categories;
    private String thumbnail;
    private String shareLink;

    public BookInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        authors = in.createStringArray();
        publisher = in.readString();
        publishedDate = in.readString();
        isbn = in.readString();
        description = in.readString();
        categories = in.createStringArray();
        thumbnail = in.readString();
        shareLink = in.readString();
    }

    public BookInfo() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeStringArray(authors);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(isbn);
        dest.writeString(description);
        dest.writeStringArray(categories);
        dest.writeString(thumbnail);
        dest.writeString(shareLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
    public String getIsbn() {
        return isbn;
    }

    public String getDescription() {
        return description;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getShareLink() {
        return shareLink;
    }
}
