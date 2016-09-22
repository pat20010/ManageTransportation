package com.project_develop_team.managetransportation.models;


public class Tasks {

    public String uid;
    public String name;
    public String taskName;
    public String taskAddress;
    public String taskPhone;

    public Tasks() {
    }

    public Tasks(String uid, String name, String taskName, String taskAddress, String taskPhone) {
        this.uid = uid;
        this.name = name;
        this.taskName = taskName;
        this.taskAddress = taskAddress;
        this.taskPhone = taskPhone;
    }

}
