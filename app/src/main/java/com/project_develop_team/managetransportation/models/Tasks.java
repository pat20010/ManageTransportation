package com.project_develop_team.managetransportation.models;


public class Tasks {

    public String uid;
    public String name;
    public String task_name;
    public String task_address;
    public String task_phone;

    public Tasks() {
    }

    public Tasks(String uid, String name, String task_name, String task_address, String task_phone) {
        this.uid = uid;
        this.name = name;
        this.task_name = task_name;
        this.task_address = task_address;
        this.task_phone = task_phone;
    }

}
