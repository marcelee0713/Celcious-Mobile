package com.example.celcious;

public class AdminMessages {
    private String fullName;
    private String email;
    private String message;
    private String time;
    private String questionNumber;

    public AdminMessages(){

    }

    public AdminMessages(String fullName, String email, String message, String time, String questionNumber) {
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.time = time;
        this.questionNumber = questionNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    @Override
    public String toString() {
        return "AdminMessages{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", questionNumber='" + questionNumber + '\'' +
                '}';
    }
}
