package com.project_develop_team.managetransportation;


import android.content.Intent;
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


public class TaskListFragment extends Fragment {

    @BindView(R.id.datesTaskTextView)
    TextView datesTaskTextView;

    FirebaseRecyclerAdapter<Tasks, TasksViewHolder> recyclerAdapter;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;

    public TaskListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        Query tasksQuery = getQuery(databaseReference);
        recyclerAdapter = new FirebaseRecyclerAdapter<Tasks, TasksViewHolder>(Tasks.class, R.layout.tasks_layout,
                TasksViewHolder.class, tasksQuery) {
            @Override
            protected void populateViewHolder(TasksViewHolder viewHolder, Tasks model, int position) {
                DatabaseReference tasksRef = getRef(position);

                viewHolder.bindToTasks(model);
                databaseReference.child("tasks").child(tasksRef.getKey());
                databaseReference.child("users-tasks").child(model.uid).child(tasksRef.getKey());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SaveListActivity.class);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recyclerAdapter != null) {
            recyclerAdapter.cleanup();
        }
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("users-tasks").child(getUid());
    }
}
