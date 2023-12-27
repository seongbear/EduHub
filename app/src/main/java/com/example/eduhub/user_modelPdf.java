package com.example.eduhub;

public class user_modelPdf {
    //variables
    String uid, author_uid, id, title, description, category, categoryId, url;
    long timestamp;
    int likes;

    //constructor
    public user_modelPdf(){
    }

    public user_modelPdf(String uid, String author_uid, String id, String title, String description, String category, String categoryId, String url, long timestamp, int likes) {
        this.uid = uid;
        this.author_uid = author_uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.categoryId = categoryId;
        this.url = url;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    //Getters and setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor_uid() {
        return author_uid;
    }

    public void setAuthor_uid(String author_uid) {
        this.author_uid = author_uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
