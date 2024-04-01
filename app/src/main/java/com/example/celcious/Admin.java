package com.example.celcious;

import java.util.Map;

public class Admin {
    private String emailaddress;
    private String facebook;
    private String fulladdress;
    private String province;
    private String city;
    private String barangay;
    private String fullname;
    private String paymentmethod;
    private String phonenumber;
    private String postalcode;
    private String schedule;
    private String region;
    private boolean setenablebutton;
    private boolean done;
    private String status;
    private int totalprice;
    private String tracknumber;
    private String shipmentNumber;
    private Map<String,AdminProduct> products;
    private String uid;
    private String userName;
    private String time;

    public Admin(){

    }

    public Admin(String emailaddress, String facebook, String fulladdress, String province, String city, String barangay, String fullname, String paymentmethod, String phonenumber, String postalcode, String schedule, String region, boolean setenablebutton, boolean done, String status, int totalprice, String tracknumber, String shipmentNumber, Map<String, AdminProduct> products, String uid, String userName, String time) {
        this.emailaddress = emailaddress;
        this.facebook = facebook;
        this.fulladdress = fulladdress;
        this.province = province;
        this.city = city;
        this.barangay = barangay;
        this.fullname = fullname;
        this.paymentmethod = paymentmethod;
        this.phonenumber = phonenumber;
        this.postalcode = postalcode;
        this.schedule = schedule;
        this.region = region;
        this.setenablebutton = setenablebutton;
        this.done = done;
        this.status = status;
        this.totalprice = totalprice;
        this.tracknumber = tracknumber;
        this.shipmentNumber = shipmentNumber;
        this.products = products;
        this.uid = uid;
        this.userName = userName;
        this.time = time;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFulladdress() {
        return fulladdress;
    }

    public void setFulladdress(String fulladdress) {
        this.fulladdress = fulladdress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isSetenablebutton() {
        return setenablebutton;
    }

    public void setSetenablebutton(boolean setenablebutton) {
        this.setenablebutton = setenablebutton;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public String getTracknumber() {
        return tracknumber;
    }

    public void setTracknumber(String tracknumber) {
        this.tracknumber = tracknumber;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public Map<String, AdminProduct> getProducts() {
        return products;
    }

    public void setProducts(Map<String, AdminProduct> products) {
        this.products = products;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "emailaddress='" + emailaddress + '\'' +
                ", facebook='" + facebook + '\'' +
                ", fulladdress='" + fulladdress + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", barangay='" + barangay + '\'' +
                ", fullname='" + fullname + '\'' +
                ", paymentmethod='" + paymentmethod + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", schedule='" + schedule + '\'' +
                ", region='" + region + '\'' +
                ", setenablebutton=" + setenablebutton +
                ", done=" + done +
                ", status='" + status + '\'' +
                ", totalprice=" + totalprice +
                ", tracknumber='" + tracknumber + '\'' +
                ", shipmentNumber='" + shipmentNumber + '\'' +
                ", products=" + products +
                ", uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
