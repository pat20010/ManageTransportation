package com.project_develop_team.managetransportation.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tasks {

    public String uid;
    public String taskName;
    public String taskAddress;

    public Tasks() {
    }

    public Tasks(String uid, String taskName, String taskAddress) {
        this.uid = uid;
        this.taskName = taskName;
        this.taskAddress = taskAddress;
    }
}
