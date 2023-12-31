package com.example.eduhub;

public class user_ModelCategory {
    //make sure to user same spellings for model variables in firebase

    String id, category, uid;
    int color;
    long timestamp;
    boolean isSelected;

    //constructor empty required for firebase
    public user_ModelCategory(){

    }

    public user_ModelCategory(String id, String category, String uid, long timestamp){
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    //Getters and setters
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color){
        this.color = color;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
    }
}
