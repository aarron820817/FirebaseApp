package com.example.firebaseapp;

public class Users {

    private  String id;
    private  String username;
    private  String imageURL;
    private  String status;
    private  String intro;

    //Constructors
    public Users(){

    }

    public String getIntro() {
        return intro;
    }

    public void setIn(String intro) {
        this.intro = intro;
    }

    public Users(String id, String username, String imageURL, String status, String intro) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.intro = intro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
