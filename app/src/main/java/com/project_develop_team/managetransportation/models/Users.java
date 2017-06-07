package com.project_develop_team.managetransportation.models;

public class Users {

    public String email;
    public String name;
    public String address;
    public String gender;
    public String age;
    public String phone;
    public String image;

    public Users() {
    }

    public Users(String email, String name, String address, String gender, String age, String phone, String image) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.image = image;
    }
}
