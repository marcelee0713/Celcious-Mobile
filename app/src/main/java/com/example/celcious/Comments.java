package com.example.celcious;

public class Comments {
    private String commentCode;
    private String message;
    private String reviewUID;
    private String userUID;
    private String username;
    private String time;

    public Comments(){}

    public Comments(String commentCode, String message, String reviewUID, String userUID, String username, String time) {
        this.commentCode = commentCode;
        this.message = message;
        this.reviewUID = reviewUID;
        this.userUID = userUID;
        this.username = username;
        this.time = time;
    }

    public String getCommentCode() {
        return commentCode;
    }

    public void setCommentCode(String commentCode) {
        this.commentCode = commentCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReviewUID() {
        return reviewUID;
    }

    public void setReviewUID(String reviewUID) {
        this.reviewUID = reviewUID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "commentCode='" + commentCode + '\'' +
                ", message='" + message + '\'' +
                ", reviewUID='" + reviewUID + '\'' +
                ", userUID='" + userUID + '\'' +
                ", username='" + username + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
