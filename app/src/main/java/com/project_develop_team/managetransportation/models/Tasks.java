package com.project_develop_team.managetransportation.models;


public class Tasks {

    public String uid;
    public String taskName;
    public String taskAddress;
    public String taskPhone;

    public Tasks() {
    }

    public Tasks(String uid, String taskName, String taskAddress, String taskPhone) {
        this.uid = uid;
        this.taskName = taskName;
        this.taskAddress = taskAddress;
        this.taskPhone = taskPhone;
    }
}
