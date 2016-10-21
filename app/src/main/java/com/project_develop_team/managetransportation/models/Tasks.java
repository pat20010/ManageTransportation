package com.project_develop_team.managetransportation.models;



public class Tasks {

    public String uid;
    public String name;
    public String taskName;
    public String taskAddress;
    public String taskPhone;
    public double latitude;
    public double longitude;
    public double taskDistance;

    public Tasks() {
    }

    public Tasks(String uid, String name, String taskName, String taskAddress, String taskPhone, double latitude, double longitude, double taskDistance) {
        this.uid = uid;
        this.name = name;
        this.taskName = taskName;
        this.taskAddress = taskAddress;
        this.taskPhone = taskPhone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.taskDistance = taskDistance;
    }

    public Tasks(double taskDistance) {
    }
}
