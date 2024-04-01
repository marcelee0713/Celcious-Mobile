package com.example.celcious;

public class Upload {
    private String userName;
    private String message;
    private String image;
    private String date;
    private String uid;
    private String bought;
    private int rating;

    public Upload(){}

    public Upload(String userName, String message, String image, String date, String uid, String bought, int rating) {
        this.userName = userName;
        this.message = message;
        this.image = image;
        this.date = date;
        this.uid = uid;
        this.bought = bought;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBought() {
        return bought;
    }

    public void setBought(String bought) {
        this.bought = bought;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Upload{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", uid='" + uid + '\'' +
                ", bought='" + bought + '\'' +
                ", rating=" + rating +
                '}';
    }
}
