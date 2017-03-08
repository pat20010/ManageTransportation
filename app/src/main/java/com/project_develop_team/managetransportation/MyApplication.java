package com.project_develop_team.managetransportation;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
