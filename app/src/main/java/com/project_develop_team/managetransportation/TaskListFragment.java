package com.project_develop_team.managetransportation;


import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.Query;


public class TaskListFragment extends ListFragment {

    public TaskListFragment() {
    }


    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference;
    }

}
