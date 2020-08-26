package com.example.itshop;

public class profiledetails {
    String profilepic,name,gender,email,phone;

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public profiledetails() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public profiledetails(String profilepic, String name, String gender, String email, String phone) {
        this.profilepic = profilepic;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
    }
}
