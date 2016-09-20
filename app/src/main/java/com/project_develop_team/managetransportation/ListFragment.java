package com.project_develop_team.managetransportation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project_develop_team.managetransportation.models.Tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class ListFragment extends Fragment {

    @BindView(R.id.datesTaskTextView)
    TextView datesTaskTextView;

    FirebaseRecyclerAdapter<Tasks, TasksViewHolder> recyclerAdapter;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.tasks_list);
        recyclerView.setHasFixedSize(true);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE d MMMM", Locale.getDefault());
        datesTaskTextView.setText(simpleDateFormat.format(new Date()));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Query tasksQuery = getQuery(mDatabase);
        recyclerAdapter = new FirebaseRecyclerAdapter<Tasks, TasksViewHolder>(Tasks.class, R.layout.tasks_layout,
                TasksViewHolder.class, tasksQuery) {
            @Override
            protected void populateViewHolder(TasksViewHolder viewHolder, Tasks model, int position) {
                DatabaseReference tasksRef = getRef(position);

                viewHolder.bindToTasks(model);
                mDatabase.child("tasks").child(tasksRef.getKey());
                mDatabase.child("users-tasks").child(model.uid).child(tasksRef.getKey());
            }
        };

        recyclerView.setAdapter(recyclerAdapter);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
