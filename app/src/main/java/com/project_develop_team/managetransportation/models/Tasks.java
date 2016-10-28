package com.project_develop_team.managetransportation.models;


public class Tasks {

    public String uid;
    public String name;
    public String task_name;
    public String task_address;
    public String task_phone;
    public double latitude;
    public double longitude;
    public double task_distance;

    public Tasks() {
    }

    public Tasks(String uid, String name, String task_name, String task_address, String task_phone, double latitude, double longitude, double task_distance) {
        this.uid = uid;
        this.name = name;
        this.task_name = task_name;
        this.task_address = task_address;
        this.task_phone = task_phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.task_distance = task_distance;
    }

    public Tasks(double task_distance) {
    }
}
