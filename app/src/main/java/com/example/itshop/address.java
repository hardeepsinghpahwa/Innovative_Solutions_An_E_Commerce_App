package com.example.itshop;

public class address {

    String name,phone,pincode,housenumber,colony,city,state;

    public address(String name, String phone, String pincode, String housenumber, String colony, String city, String state) {
        this.name = name;
        this.phone = phone;
        this.pincode = pincode;
        this.housenumber = housenumber;
        this.colony = colony;
        this.city = city;
        this.state = state;
    }

    public address() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
