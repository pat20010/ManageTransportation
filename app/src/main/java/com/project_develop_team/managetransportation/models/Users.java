package com.project_develop_team.managetransportation.models;


public class Users {
    String firstName;
    String lastName;
    String address;
    String gender;


    public Users() {

    }

    public Users(String firstName, String lastName, String address, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getAddress() {
        return address;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
}

