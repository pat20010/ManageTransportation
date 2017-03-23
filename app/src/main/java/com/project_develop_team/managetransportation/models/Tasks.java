package com.project_develop_team.managetransportation.models;


public class Tasks {

    public String uid;
    public String name;
    public String task_name_collect;
    public String task_address_collect;
    public String task_phone_collect;
    public String task_name_deliver;
    public String task_address_deliver;
    public String task_phone_deliver;
    public int task_date;
    public double task_time;
    public double task_latitude_collect;
    public double task_longitude_collect;
    public double task_latitude_deliver;
    public double task_longitude_deliver;

    public Tasks() {
    }

    public Tasks(String uid, String name, String task_name_collect, String task_address_collect, String task_phone_collect,
                 String task_name_deliver, String task_address_deliver, String task_phone_deliver, int task_date, double task_time,
                 double task_latitude_collect, double task_longitude_collect, double task_latitude_deliver,
                 double task_longitude_deliver) {

        this.uid = uid;
        this.name = name;
        this.task_name_collect = task_name_collect;
        this.task_address_collect = task_address_collect;
        this.task_phone_collect = task_phone_collect;
        this.task_name_deliver = task_name_deliver;
        this.task_address_deliver = task_address_deliver;
        this.task_phone_deliver = task_phone_deliver;
        this.task_date = task_date;
        this.task_time = task_time;
        this.task_latitude_collect = task_latitude_collect;
        this.task_longitude_collect = task_longitude_collect;
        this.task_latitude_deliver = task_latitude_deliver;
        this.task_longitude_deliver = task_longitude_deliver;
    }

    public Tasks(double task_distance) {
    }
}
